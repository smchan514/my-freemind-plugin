package smchan.freemind_my_plugin;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import freemind.controller.MapModuleManager.MapModuleChangeObserver;
import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.Mode;
import freemind.modes.ModeController.NodeLifetimeListener;
import freemind.modes.ModeController.NodeSelectionListener;
import freemind.view.MapModule;
import freemind.view.mindmapview.NodeView;

/**
 * Open the current mindmap in Windows Explorer
 */
public class ShowMostRecentlyUsedNodes extends ModeControllerHookAdapter {
    private TextFrame _textFrame;

    public ShowMostRecentlyUsedNodes() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        if (_textFrame == null) {
            _textFrame = new TextFrame();
        }

        getController().getFrame().getController().getMapModuleManager().addListener(_textFrame);

        // getController().getMap().addTreeModelListener(_textFrame);
        getController().registerNodeSelectionListener(_textFrame, true);
        getController().registerNodeLifetimeListener(_textFrame, false);
    }

    @Override
    public void shutdownMapHook() {
        // ...
    }

    ////////////////////
    private class TextFrame extends JFrame
            implements TreeModelListener, NodeSelectionListener, NodeLifetimeListener, MapModuleChangeObserver {
        private static final long serialVersionUID = 1L;
        private JTextArea _textArea;

        public TextFrame() {
            initComponents();
        }

        private void initComponents() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            getContentPane().add(createMainPanel(), BorderLayout.CENTER);

            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private Component createMainPanel() {
            JPanel panel = new JPanel(new BorderLayout());

            _textArea = new JTextArea(5, 80);

            panel.add(new JScrollPane(_textArea));

            return panel;
        }

        private void appendText(String string) {
            _textArea.append(string);
            _textArea.append("\n");
        }

        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            StringBuilder sb = new StringBuilder();
            sb.append("treeNodesChanged:");
            sb.append(e.getTreePath());

            Object lpc = e.getTreePath().getLastPathComponent();
            sb.append(", lpc=");
            sb.append(lpc.getClass().getName());

            appendText(sb.toString());

        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            StringBuilder sb = new StringBuilder();
            sb.append("treeNodesInserted:");
            sb.append(e.getTreePath());
            appendText(sb.toString());
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            StringBuilder sb = new StringBuilder();
            sb.append("treeNodesRemoved:");
            sb.append(e.getTreePath());
            appendText(sb.toString());
        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            StringBuilder sb = new StringBuilder();
            sb.append("treeStructureChanged:");
            sb.append(e.getTreePath());
            appendText(sb.toString());
        }

        @Override
        public void onUpdateNodeHook(MindMapNode node) {
            StringBuilder sb = new StringBuilder();
            sb.append("onUpdateNodeHook:");
            sb.append(node);
            sb.append(" #");
            sb.append(node.getObjectId(getController()));
            appendText(sb.toString());
        }

        @Override
        public void onFocusNode(NodeView node) {
            StringBuilder sb = new StringBuilder();
            sb.append("onFocusNode:");
            sb.append(node.getModel().getPlainTextContent());
            sb.append(" #");
            sb.append(node.getModel().getObjectId(getController()));
            appendText(sb.toString());
        }

        @Override
        public void onLostFocusNode(NodeView node) {
        }

        @Override
        public void onSaveNode(MindMapNode node) {
        }

        @Override
        public void onSelectionChange(NodeView node, boolean isSelected) {
        }

        @Override
        public void onCreateNodeHook(MindMapNode node) {
        }

        @Override
        public void onPreDeleteNode(MindMapNode node) {
            StringBuilder sb = new StringBuilder();
            sb.append("onPreDeleteNode:");
            sb.append(node);
            sb.append(" #");
            sb.append(node.getObjectId(getController()));
            appendText(sb.toString());
        }

        @Override
        public void onPostDeleteNode(MindMapNode node, MindMapNode parent) {
        }

        @Override
        public boolean isMapModuleChangeAllowed(MapModule oldMapModule, Mode oldMode, MapModule newMapModule,
                Mode newMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("isMapModuleChangeAllowed: oldMapModule=");
            sb.append(oldMapModule);
            sb.append(", oldMode=");
            sb.append(oldMode);
            sb.append(", newMapModule=");
            sb.append(newMapModule);
            sb.append(", newMode=");
            sb.append(newMode);
            appendText(sb.toString());

            return true;
        }

        @Override
        public void beforeMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterMapClose(MapModule oldMapModule, Mode oldMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("afterMapClose:");
            sb.append(oldMapModule);
            sb.append(", mode=");
            sb.append(oldMode);
            appendText(sb.toString());

        }

        @Override
        public void afterMapModuleChange(MapModule oldMapModule, Mode oldMode, MapModule newMapModule, Mode newMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("afterMapModuleChange: oldMapModule=");
            sb.append(oldMapModule);
            sb.append(", oldMode=");
            sb.append(oldMode);
            sb.append(", newMapModule=");
            sb.append(newMapModule);
            sb.append(", newMode=");
            sb.append(newMode);
            appendText(sb.toString());

        }

        @Override
        public void numberOfOpenMapInformation(int number, int pIndex) {
            StringBuilder sb = new StringBuilder();
            sb.append("numberOfOpenMapInformation: number=");
            sb.append(number);
            sb.append(", pIndex=");
            sb.append(pIndex);
            appendText(sb.toString());

        }
    }
}
