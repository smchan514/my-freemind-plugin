package smchan.freemind_my_plugin;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;

/**
 * Show path from the root node of the mind map to on selected node
 */
public class ShowNodePath extends ModeControllerHookAdapter {
    public ShowNodePath() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Check for the preconditions
        List<?> selected = getController().getSelecteds();
        if (selected == null || selected.size() != 1) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Preconditions not met: get one node selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the path to the selected node
        MindMapNode node = (MindMapNode) selected.get(0);
        NodePath nodePath = NodePathUtil.getNodePath(node);
        LinkedList<String> path = new LinkedList<>();

        // Iterate until we hit the root node
        while (node != null) {
            // Get plain text to avoid HTML
            path.addFirst(node.getPlainTextContent());
            node = node.getParentNode();
        }

        // Show path in GUI
        SimpleTextDialog dialog = new SimpleTextDialog("Node Path", nodePath + "\n\n" + renderPath(path));
        dialog.setVisible(true);
    }

    private String renderPath(LinkedList<String> path) {
        int levels = 0;
        StringBuilder sb = new StringBuilder();
        for (String p : path) {
            for (int i = 0; i < levels; i++) {
                sb.append("  ");
            }
            sb.append(p);
            sb.append('\n');
            ++levels;
        }

        return sb.toString();
    }
}
