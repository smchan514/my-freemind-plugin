package smchan.freemind_my_plugin.mru;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import freemind.controller.actions.generated.instance.AddLinkXmlAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;
import smchan.freemind_my_plugin.PutMindMapNameInClipboard;
import smchan.freemind_my_plugin.SetCrossLink;

/**
 * MRU nodes display.
 * 
 * [2024-03-12] Added pop-up menu on right-click with option to "Set cross link"
 */
public class MRUNodesView extends JDialog {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(MRUNodesView.class.getName());

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private static final int ALL_MODFIERS = KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK | KeyEvent.META_DOWN_MASK
            | KeyEvent.ALT_DOWN_MASK;

    private JList<MindMapNode> _jlistMRUNodes;

    private MRUNodesModel _mruModel;

    public MRUNodesView(JFrame owner) {
        super(owner);

        LOGGER.info("Creating MRUNodesView");
        initComponents();
    }

    public void setMRUNodesModel(MRUNodesModel mruModel) {
        if (mruModel != null) {
            _jlistMRUNodes.setModel(mruModel);
        }

        _mruModel = mruModel;
    }

    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("MRU nodes");
        setMinimumSize(new Dimension(320, 240));

        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        getContentPane().add(createControlPanel(), BorderLayout.SOUTH);
    }

    private Component createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;
        JComponent comp;

        gbc = new GridBagConstraints();
        gbc.insets = DEFAULT_INSETS;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        comp = new JButton(new ClearAction());
        panel.add(comp, gbc);

        return panel;
    }

    private Component createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        _jlistMRUNodes = new JList<>();
        _jlistMRUNodes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        _jlistMRUNodes.setCellRenderer(new MindMapNodeCellRenderer());

        // Listen for user inputs
        _jlistMRUNodes.addKeyListener(new MyKeyListener());
        _jlistMRUNodes.addMouseListener(new MyMouseListener());

        panel.add(new JScrollPane(_jlistMRUNodes));

        return panel;
    }

    void showNodeInList() {
        MindMapNode node = _jlistMRUNodes.getSelectedValue();
        if (node != null) {
            // Show the corresponding mind map and scroll to the node
            _mruModel.selectNode(node);
        }
    }

    void showContextualMenu() {
        // Create pop-up menu
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem(new SetRemoteCrossLinksAction()));

        // Show pop-up menu at mouse cursor location
        Point point = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(point, this);
        popupMenu.show(this, point.x, point.y);
    }

    void doSetRemoteCrossLinks() {
        // Precondition 1: exactly two nodes selected in the list
        List<MindMapNode> selected = _jlistMRUNodes.getSelectedValuesList();
        if (selected.size() != 2) {
            JOptionPane.showMessageDialog(null, "Preconditions not met: get two nodes selected", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        MindMapNode node1 = selected.get(0);
        MindMapNode node2 = selected.get(1);

        // Precondition 2: the two selected nodes are from different maps
        MindMapController mmc1 = (MindMapController) node1.getMap().getModeController();
        MindMapController mmc2 = (MindMapController) node2.getMap().getModeController();

        // Warn for overwriting links
        if (node1.getLink() != null || node2.getLink() != null) {
            int rc = JOptionPane.showConfirmDialog(null, "Overwrite existing link(s) in nodes?", "Confirm",
                    JOptionPane.OK_CANCEL_OPTION);

            if (rc != JOptionPane.OK_OPTION) {
                // Cancel
                return;
            }
        }

        if (mmc1 == mmc2) {
            // The two nodes are in the same mind map, set local cross links instead
            SetCrossLink.performTransaction(mmc1, node1, node2);
        } else {
            // The nodes are in two separate mind maps, perform two transactions, one on
            // each map
            performTransaction(mmc1, node1, node2);
            performTransaction(mmc2, node2, node1);
        }
    }

    private void performTransaction(MindMapController mmc, MindMapNode nodeSrc, MindMapNode nodeDst) {
        XmlAction editAction = createEditAction(mmc, nodeSrc, PutMindMapNameInClipboard.getFullPathToNode(nodeDst));
        XmlAction undoAction = createUndoAction(mmc, nodeSrc);
        mmc.doTransaction("mmc1", new ActionPair(editAction, undoAction));
        // Find a better way to trigger update of file change indicator...
        // mmc.showThisMap();
    }

    /**
     * Create the action corresponding to the forward edit action which sets the
     * node's link to newDestination
     */
    private XmlAction createEditAction(ModeController mmc, MindMapNode node, String newDestination) {
        AddLinkXmlAction action = new AddLinkXmlAction();
        action.setNode(mmc.getNodeID(node));
        action.setDestination(newDestination);
        return action;
    }

    /**
     * Create the action corresponding to the undo action which reverts the node's
     * link to its current value
     */
    private XmlAction createUndoAction(ModeController mmc, MindMapNode node) {
        AddLinkXmlAction action = new AddLinkXmlAction();
        action.setNode(mmc.getNodeID(node));
        action.setDestination(node.getLink());
        return action;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class SetRemoteCrossLinksAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public SetRemoteCrossLinksAction() {
            super("Set cross link");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doSetRemoteCrossLinks();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class MyMouseListener extends MouseAdapter {
        public MyMouseListener() {
            // ...
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
                // Double-click with mouse left button
                showNodeInList();
                evt.consume();
            } else if (evt.getButton() == MouseEvent.BUTTON3 && evt.getClickCount() == 1) {
                // Single-click with mouse right button
                showContextualMenu();
                evt.consume();
            } else {
                System.out.println(evt);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class MyKeyListener extends KeyAdapter {

        public MyKeyListener() {
            // ...
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            if ((evt.getModifiersEx() & ALL_MODFIERS) == 0 && evt.getKeyCode() == KeyEvent.VK_ENTER) {
                showNodeInList();
                evt.consume();
            } else if ((evt.getModifiersEx() & ALL_MODFIERS) == 0 && evt.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
                showContextualMenu();
                evt.consume();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private class ClearAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ClearAction() {
            super("Clear");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            _mruModel.clear();
        }

    }

}
