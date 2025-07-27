package smchan.freemind_my_plugin.timeline;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.MapModule;
import smchan.freemind_my_plugin.adv_search.ITreeWalker;
import smchan.freemind_my_plugin.adv_search.TreeWalkerDepthFirst;
import smchan.freemind_my_plugin.timeline.DialogDataIngest.IngestScope;
import smchan.freemind_my_plugin.timeline.DialogDataIngest.TimestampSource;
import smchan.freemind_my_plugin.timeline.DialogTimelineResults.IUserActionHandler;

public class ShowTimeline extends ModeControllerHookAdapter implements IUserActionHandler {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(ShowTimeline.class.getName());

    private static DialogTimelineResults _timelineDialog;
    private static ScheduledThreadPoolExecutor _h2ThreadPool;

    private MindMapController _lastMmc;

    public ShowTimeline() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        _lastMmc = (MindMapController) getController();

        // Show conifg dialog
        JFrame frame = getController().getFrame().getJFrame();
        DialogDataIngest dlg = new DialogDataIngest(frame);
        if (!dlg.showDialog(frame)) {
            // User cancelled
            return;
        }

        String jdbcCnxString = dlg.getJdbcConnectionString();
        IngestScope ingestScope = dlg.getIngestScope();
        TimestampSource timestampSource = dlg.getTimestampSource();

        // Perform data ingest
        try (H2DataIngestAdaptor dataIngestAdaptor = new H2DataIngestAdaptor(jdbcCnxString)) {
            performIngest(dataIngestAdaptor, ingestScope, timestampSource);
        } catch (Exception e) {
            String title = "Ingest failed";
            JOptionPane.showMessageDialog(dlg, "<html><body><pre>" + e.getMessage(), title, JOptionPane.ERROR_MESSAGE);
        }

        // Close the existing timeline dialog
        if (_timelineDialog != null) {
            _timelineDialog.setVisible(false);
        }

        // Show results
        _timelineDialog = createTimelineDialog(frame, jdbcCnxString);
        _timelineDialog.showTimeline();
    }

    @Override
    public void gotoNode(JDialog dlg, String mapPath, String nodeId) {
        // Put this in invokeLater even though we should already be in AWT event
        // dispatcher... otherwise we get the occasional lock-up
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ModeController map = _lastMmc.load(new File(mapPath));
                    NodeAdapter node = map.getNodeFromID(nodeId);
                    map.centerNode(node);
                } catch (Exception e) {
                    String title = "Ingest failed";
                    JOptionPane.showMessageDialog(dlg, "<html><body><pre>" + e.getMessage(), title,
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

    private DialogTimelineResults createTimelineDialog(JFrame frame, String jdbcCnxString) {
        // Time scale source for the "overall timeline"
        DefaultTimeScaleSource tssOverall = new DefaultTimeScaleSource();

        // Time scale source for the "details timeline"
        DefaultTimeScaleSource tssDetails = new DefaultTimeScaleSource();

        // Model for "overall timeline" is the composite of two models:
        // One model for aggregated events (fetched from database) and a second
        // model for the display of time selection in the "details timeline"
        DefaultTimelineModel<ITimelineEvent> tmOverall = new DefaultTimelineModel<>();
        DefaultTimelineModel<ITimelineEvent> tmSelection = new DefaultTimelineModel<>();
        CompositeTimelineModel<ITimelineEvent> tmComposite = new CompositeTimelineModel<ITimelineEvent>(tmOverall,
                tmSelection);

        // Model for "details timeline"
        DefaultTimelineModel<NodeEvent> tmDetails = new DefaultTimelineModel<>();

        if (_h2ThreadPool == null) {
            // Create a thread pool with one thread so all that database operations
            // are sequenced (no concurrent queries)
            // It is a static variable because "hook adapters" are re-created
            _h2ThreadPool = new ScheduledThreadPoolExecutor(1);
        }

        H2QueryOverallAdaptor queryOverall = new H2QueryOverallAdaptor(jdbcCnxString, new TimeScaleDelay(tssOverall),
                tssDetails, tmOverall, tmSelection, _h2ThreadPool);
        new H2QueryDetailsAdaptor(jdbcCnxString, new TimeScaleDelay(tssDetails), tmDetails, _h2ThreadPool);

        // Trigger an initial update of the overall timeline
        _h2ThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    queryOverall.triggerUpdateTimeScaleSourceOverall();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to update overall timeine", e);
                }
            }
        });

        return new DialogTimelineResults(frame, tssOverall, tssDetails, tmComposite, tmDetails, this);
    }

    private void performIngest(H2DataIngestAdaptor dataIngestAdaptor, IngestScope ingestScope,
            TimestampSource timestampSource) throws SQLException {
        // Select search scope
        MindMapController mmc = (MindMapController) getController();
        MindMapNode[] nodes;
        switch (ingestScope) {
        case CurrentMindMap:
            nodes = new MindMapNode[] { mmc.getRootNode() };
            break;
        case AllOpenMaps:
            nodes = extractAllRootNodes(mmc);
            break;
        case None:
            // Skip ingest
            return;
        default:
            throw new RuntimeException("Invalid injest scope");
        }

        // Fixed search orientation
        ITreeWalker treeWalker = new TreeWalkerDepthFirst(nodes);

        // Perform data ingest
        dataIngestAdaptor.recreateTables();
        HashMap<File, Long> mapFile2MapIdentity = new HashMap<>();

        int totalMapCount = 0;
        int totalNodeCount = 0;
        for (MindMapNode node : treeWalker) {
            ModeController mc = node.getMap().getModeController();
            File file = mc.getMap().getFile();
            String nodeId = node.getObjectId(mc);
            String text = node.getPlainTextContent();

            long mapIdentity;

            if (!mapFile2MapIdentity.containsKey(file)) {
                mapIdentity = dataIngestAdaptor.insertMap(file.getPath());
                mapFile2MapIdentity.put(file, mapIdentity);
                ++totalMapCount;
            } else {
                mapIdentity = mapFile2MapIdentity.get(file);
            }

            long nodeIdentity = dataIngestAdaptor.insertNode(mapIdentity, nodeId, text);
            long ts;
            String tsType;

            switch (timestampSource) {
            case CreatedAt:
                ts = node.getHistoryInformation().getCreatedAt().getTime();
                tsType = "c";
                break;

            case LastModified:
            default:
                ts = node.getHistoryInformation().getLastModifiedAt().getTime();
                tsType = "m";
                break;
            }

            dataIngestAdaptor.insertTimestamp(ts, tsType, nodeIdentity);

            ++totalNodeCount;
        }

        LOGGER.info("totalMapCount=" + totalMapCount + ", totalNodeCount=" + totalNodeCount);
    }

    private MindMapNode[] extractAllRootNodes(MindMapController mmc) {
        @SuppressWarnings("unchecked")
        List<MapModule> mm = mmc.getFrame().getController().getMapModuleManager().getMapModuleVector();
        LinkedList<MindMapNode> selected = new LinkedList<>();

        for (MapModule mapModule : mm) {
            selected.add(mapModule.getModel().getRootNode());
        }

        return selected.toArray(new MindMapNode[0]);
    }

}
