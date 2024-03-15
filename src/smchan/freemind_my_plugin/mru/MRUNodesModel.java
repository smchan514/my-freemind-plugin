package smchan.freemind_my_plugin.mru;

import java.util.LinkedList;

import javax.swing.AbstractListModel;

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
 * 
 * [2024-03-13] Support pinned nodes in addition to MRU nodes
 */
public class MRUNodesModel extends AbstractListModel<MindMapNode>
        implements MapModuleChangeObserver, NodeLifetimeListener, NodeSelectionListener {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(MRUNodesModel.class.getName());

    private static final long serialVersionUID = 1L;

    private final LinkedList<MindMapNode> _lstPinnedNodes = new LinkedList<>();
    private final LinkedList<MindMapNode> _lstMruNodes = new LinkedList<>();

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

        if (_lstPinnedNodes.contains(node)) {
            // Do nothing if the node is already pinned
        } else {
            // Move the MRU node to the top of MRU nodes list
            removeMruNode(node);
            addFirstMruNode(node);
        }
    }

    public void removeNode(MindMapNode node) {
        // Skip the rest if MRU inhibited
        if (_inhibitMRU)
            return;

        if (removePinnedNode(node)) {
            // Pinned node removed
        } else {
            // Remove node from MRU nodes list
            removeMruNode(node);
        }
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

    // Methods from DefaultListModel<MindMapNode>
    public void clear() {
        int nbrMruNode = _lstMruNodes.size();

        // Clear MRU nodes only
        if (nbrMruNode > 0) {
            int nbrPinnedNodes = _lstPinnedNodes.size();
            _lstMruNodes.clear();
            fireIntervalRemoved(this, nbrPinnedNodes, nbrPinnedNodes + nbrMruNode - 1);
        }
    }

    public void togglePinStatus(MindMapNode node) {
        if (removePinnedNode(node)) {
            // Removed from pinned nodes list...
            // If successful, add node to MRU nodes list
            addFirstMruNode(node);
        } else if (removeMruNode(node)) {
            // Removed from MRU nodes list...
            // If successful, add node to pinned nodes list
            addLastPinnedNode(node);
        }
    }

    public boolean isNodePinned(MindMapNode node) {
        return _lstPinnedNodes.contains(node);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Private methods

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

    /**
     * Add a node at the end of pinned nodes list, firing list data event
     * 
     * @param node a non-null instance of MindMapNode
     */
    private void addLastPinnedNode(MindMapNode node) {
        int nbrPinnedNodes = _lstPinnedNodes.size();
        _lstPinnedNodes.addLast(node);
        fireIntervalAdded(this, nbrPinnedNodes, nbrPinnedNodes);
    }

    /**
     * Add a node at the top of MRU nodes list (not pinned), firing list data event
     * 
     * @param node a non-null instance of MindMapNode
     */
    private void addFirstMruNode(MindMapNode node) {
        int nbrPinnedNodes = _lstPinnedNodes.size();
        _lstMruNodes.addFirst(node);
        fireIntervalAdded(this, nbrPinnedNodes, nbrPinnedNodes);

        // Remove overflow elements in the MRU list
        if (_lstMruNodes.size() > _nbrMruElements) {
            _lstMruNodes.removeLast();
            int totalNodes = _lstMruNodes.size() + nbrPinnedNodes;
            fireIntervalRemoved(this, totalNodes, totalNodes);
        }
    }

    /**
     * Attempt to remove a pinned node, firing list data event when successful
     * 
     * @param node a non-null instance of MindMapNode
     * @return true if remove successful, false otherwise (node not in the list of
     *         pinned nodes)
     */
    private boolean removePinnedNode(MindMapNode node) {
        int index = _lstPinnedNodes.indexOf(node);
        if (index >= 0) {
            _lstPinnedNodes.remove(node);
            fireIntervalRemoved(this, index, index);
            return true;
        }

        return false;
    }

    /**
     * Attempt to remove a MRU node (not pinned), firing list data event when
     * successful
     * 
     * @param node a non-null instance of MindMapNode
     * @return true if remove successful, false otherwise (node not in the MRU list)
     */
    private boolean removeMruNode(MindMapNode node) {
        int index = _lstMruNodes.indexOf(node);
        if (index >= 0) {
            int nbrPinnedNodes = _lstPinnedNodes.size();
            _lstMruNodes.remove(node);
            fireIntervalRemoved(this, index + nbrPinnedNodes, index + nbrPinnedNodes);
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Implements MapModuleChangeObserver

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
    // Implements NodeLifetimeListener

    @Override
    public void onPreDeleteNode(MindMapNode node) {
        recursiveRemoveNode(node);
    }

    private void recursiveRemoveNode(MindMapNode node) {
        removeNode(node);

        // Recursively remove all children nodes
        for (Object obj : node.getChildren()) {
            if (obj instanceof MindMapNode) {
                recursiveRemoveNode((MindMapNode) obj);
            }
        }
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
    // Implements NodeSelectionListener

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
    // Implements methods from AbstractListModel<MindMapNode>

    @Override
    public int getSize() {
        return _lstMruNodes.size() + _lstPinnedNodes.size();
    }

    @Override
    public MindMapNode getElementAt(int index) {
        int nbrPinnedNodes = _lstPinnedNodes.size();
        if (index < nbrPinnedNodes) {
            return _lstPinnedNodes.get(index);
        }

        return _lstMruNodes.get(index - nbrPinnedNodes);
    }

}
