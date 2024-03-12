package smchan.freemind_my_plugin;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * Put into system clip-board the URL to the currently selected node or, if no
 * nodes are selected, the currently visible mind map.
 */
public class PutMindMapNameInClipboard extends ExportHook implements ClipboardOwner {
    public PutMindMapNameInClipboard() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        try {
            // Put the full file name into system clipboard
            MindMapNode selNode = getController().getSelected();
            String filePath = getFullPathToNode(selNode);

            StringSelection stringSelection = new StringSelection(filePath);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // Nothing to do
    }

    /**
     * Get the fully qualified file URL to the argument node.
     * 
     * @param node a non-null instance of {@link MindMapNode}
     * @return URL to the argument node as a string
     */
    public static String getFullPathToNode(MindMapNode node) {
        assert (node != null);

        File file = node.getMap().getFile();
        if (file == null) {
            throw new RuntimeException("Unable to get mindmap file name (probably not saved yet?)");
        }

        String filePath = file.getAbsolutePath();
        ModeController modeController = node.getMap().getModeController();

        // Convert the filePath to point to the selected node
        StringBuilder sb = new StringBuilder();
        sb.append("file:///");
        sb.append(filePath);
        sb.append('#');
        sb.append(modeController.getNodeID(node));
        return sb.toString();
    }

}
