package smchan.freemind_my_plugin;

import java.awt.Component;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * Prompt user for a node path, e.g. 1.0.2, and go to the node
 */
public class GoToNodeAtPath extends ExportHook {

    public GoToNodeAtPath() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        MindMapController mmc = (MindMapController) getController();

        Component parent = mmc.getFrame().getJFrame();
        Object message = "Enter the node ID (e.g. 1.0.2)";
        String title = "Go to node at path";
        String numericPath = JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
        int[] parts;

        if (numericPath == null) {
            // Skip the rest if no input
            return;
        }

        numericPath = numericPath.trim();
        if (numericPath.isEmpty()) {
            // Skip the rest if no input
            return;
        }

        try {
            parts = decomposeNumericPath(numericPath);
        } catch (Exception e) {
            message = "Failed to decode path '" + numericPath + "'\n\n" + e;
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            MindMapNode node = mmc.getRootNode();
            for (int i = 0; i < parts.length; i++) {
                node = (MindMapNode) node.getChildren().get(parts[i]);
            }

            mmc.centerNode(node);
        } catch (Exception e) {
            message = "Failed to find node '" + numericPath + "'";
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    private int[] decomposeNumericPath(String numericPath) {
        String[] strs = numericPath.split("\\.");
        int[] parts = new int[strs.length];

        for (int i = 0; i < strs.length; i++) {
            parts[i] = Integer.parseInt(strs[i]);
        }

        return parts;
    }
}
