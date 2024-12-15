package smchan.freemind_my_plugin;

import java.awt.Component;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.modes.NodeAdapter;
import freemind.modes.mindmapmode.MindMapController;

/**
 * Prompt user for a node ID and go to the node
 */
public class GoToNodeId extends ExportHook {

    public GoToNodeId() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        MindMapController mmc = (MindMapController) getController();

        Component parent = mmc.getFrame().getJFrame();
        Object message = "Enter the node ID (e.g. ID_123456)";
        String title = "Go to node ID";
        String nodeId = JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);

        if (nodeId == null || nodeId.isEmpty()) {
            // Skip the rest if no input
            return;
        }
        
        // Remove leading '#' if present
        if (nodeId.startsWith("#")) {
            nodeId = nodeId.substring(1);
        }

        try {
            NodeAdapter node = mmc.getNodeFromID(nodeId);
            mmc.centerNode(node);
        } catch (Exception e) {
            message = "Failed to find node '" + nodeId + "'";
            JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }
}
