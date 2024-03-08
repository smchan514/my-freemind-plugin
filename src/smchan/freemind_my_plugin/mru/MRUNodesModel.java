package smchan.freemind_my_plugin.mru;

import javax.swing.DefaultListModel;

import freemind.controller.Controller;
import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.view.MapModule;
import freemind.view.mindmapview.NodeView;

/**
 * Data model for list of MRU {@link MindMapNode} with adapters to register to
 * the current mind map as per user selection and to convert "map node events"
 * into MRU list updates.
 */
public class MRUNodesModel extends DefaultListModel<MindMapNode>
        implements MapModuleChangeObserver, NodeLifetimeListener, NodeSelectionListener {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(MRUNodesModel.class.getName());

    private static final long serialVersionUID = 1L;

    private final int _nbrMruElements;
    private Controller _controller;
    private boolean _inhibitMRU = false;

    public MRUNodesModel(int nbrMruElements) {
        _nbrMruElements = nbrMruElements;
    }

    public void putNode(MindMapNode node) {
        // Skip the rest if MRU inhibited
        if (_inhibitMRU)
            return;

        super.removeElement(node);
        super.add(0, node);

        // Remove overflow elements
        if (super.size() > _nbrMruElements) {
            super.remove(super.size() - 1);
        }
    }

    public void removeNode(MindMapNode node) {
        // Skip the rest if MRU inhibited
        if (_inhibitMRU)
            return;

        super.removeElement(node);
    }

    public void setController(Controller controller) {
        if (controller != null) {
            LOGGER.info("Connecting to MapModuleManager");
            controller.getMapModuleManager().addListener(this);

            LOGGER.info("Registering for map node events from current map");
            registerFromMapNodeEvents(controller.getModeController());
        } else {
            LOGGER.info("Unregistering from current map");
            unregisterFromMapNodeEvents(_controller.getModeController());

            LOGGER.info("Disconnecting from MapModuleManager");
            _controller.getMapModuleManager().addListener(this);
        }

        _controller = controller;
    }

    public void selectNode(MindMapNode node) {
        // Verify inputs
        if (_controller == null || node == null)
            return;

        try {
            _inhibitMRU = true;

            // Show the corresponding mind map, if it's not already shown
            ModeController modeController = node.getMap().getModeController();
            MapModule mapModule = _controller.getMapModuleManager().getModuleGivenModeController(modeController);
            _controller.getMapModuleManager().changeToMapModule(mapModule);

            // Go to the selected node
            modeController.centerNode(node);
        } finally {
            _inhibitMRU = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isMapModuleChangeAllowed(MapModule oldMapModule, Mode oldMode, MapModule newMapModule,
            Mode newMode) {
        // Always allow
        return true;
    }

    @Override
    public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
        // Disconnect from oldMapModule
        if (oldMapModule != null) {
            unregisterFromMapNodeEvents(oldMapModule.getModeController());
        }

        // Connect to newMapModule
        if (newMapModule != null) {
            registerFromMapNodeEvents(newMapModule.getModeController());
        }
    }

    @Override
    public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
        // Do nothing
    }

    @Override
    public void afterMapClose(MapModule oldMapModule, Mode oldMode) {
        // Do nothing
    }

    @Override
    public void numberOfOpenMapInformation(int number, int pIndex) {
        // Do nothing
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPreDeleteNode(MindMapNode node) {
        removeNode(node);
    }

    @Override
    public void onCreateNodeHook(MindMapNode node) {
        // Do nothing
    }

    @Override
    public void onPostDeleteNode(MindMapNode node, MindMapNode parent) {
        // Do nothing
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onUpdateNodeHook(MindMapNode node) {
        putNode(node);
    }

    @Override
    public void onFocusNode(NodeView node) {
        putNode(node.getModel());
    }

    @Override
    public void onLostFocusNode(NodeView node) {
        // Do nothing
    }

    @Override
    public void onSaveNode(MindMapNode node) {
        // Do nothing
    }

    @Override
    public void onSelectionChange(NodeView pNode, boolean pIsSelected) {
        // Do nothing
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void registerFromMapNodeEvents(ModeController modeController) {
        if (modeController != null) {
            // Register for node deletion events
            modeController.registerNodeLifetimeListener(this, false);

            // Register for selection change events
            modeController.registerNodeSelectionListener(this, true);
        }
    }

    private void unregisterFromMapNodeEvents(ModeController modeController) {
        if (modeController != null) {
            modeController.deregisterNodeLifetimeListener(this);
            modeController.deregisterNodeSelectionListener(this);
        }
    }

}
