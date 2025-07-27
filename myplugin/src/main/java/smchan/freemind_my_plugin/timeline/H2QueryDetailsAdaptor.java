package smchan.freemind_my_plugin.timeline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

/**
 * Timeline data query adapter for the in-process H2 database
 */
class H2QueryDetailsAdaptor implements ITimeScaleChangedListener {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(H2QueryDetailsAdaptor.class.getName());

    private ITimeScaleSource _timeScaleSource;
    private ITimelineModel<NodeEvent> _timelineModel;
    private ScheduledExecutorService _executor;
    private final String _jdbcCnxString;

    public H2QueryDetailsAdaptor(String jdbcCnxString, ITimeScaleSource timeScaleSource,
            ITimelineModel<NodeEvent> timelineModel, ScheduledExecutorService executor) {
        assert jdbcCnxString != null;
        assert timeScaleSource != null;
        assert timelineModel != null;
        assert executor != null;

        try {
            // Explicitly register H2 driver...
            // We have to do this likely because the H2 jar is in the plugin class path
            // instead of the starting class path
            DriverManager.registerDriver(new org.h2.Driver());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load H2 driver", e);
        }

        _jdbcCnxString = jdbcCnxString;
        _timeScaleSource = timeScaleSource;
        _timelineModel = timelineModel;
        _executor = executor;

        // Register for time scale change events so that we can update the
        // timeline model with corresponding data in the new time range
        _timeScaleSource.addTimeScaleChangedListener(this);
    }

    @Override
    public void updateTimeScale(ITimeScale timeScale) {

        _executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    fetch(timeScale.getMinTime(), timeScale.getMaxTime());
                } catch (Throwable t) {
                    LOGGER.log(Level.WARNING, "Failed to fetch", t);
                }

            }
        });
    }

    public void fetch(long minTime, long maxTime) throws Exception {
        try (Connection conn = DriverManager.getConnection(_jdbcCnxString)) {
            LinkedList<NodeEvent> list = new LinkedList<>();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT TS, NODE_TEXT, NODE_ID, PATH FROM ALL_TIMESTAMPS INNER JOIN ALL_NODES ON ALL_TIMESTAMPS.NODE_IDENTITY = ALL_NODES.NODE_IDENTITY JOIN ALL_MAPS ON ALL_NODES.MAP_IDENTITY = ALL_MAPS.MAP_IDENTITY WHERE TS >= ? AND TS <= ? ORDER BY TS;");
            stmt.setTimestamp(1, new Timestamp(minTime));
            stmt.setTimestamp(2, new Timestamp(maxTime));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp(1);
                String text = rs.getString(2);
                String nodeId = rs.getString(3);
                String path = rs.getString(4);
                list.add(new NodeEvent(text, ts.getTime(), path, nodeId));
            }

            // Update UI in AWT event dispatcher
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _timelineModel.clearEvents();
                    _timelineModel.addEvents(list);
                }
            });
        }
    }

}
