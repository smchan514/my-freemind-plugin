package smchan.freemind_my_plugin;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.ModeController;

/**
 * Put into system clip-board the URL to the currently selected node or, if no
 * nodes are selected, the currently visible mind map.
 * 
 * [2024-09-20] HTML-escape file path to support space characters
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
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(), e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
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

        // Perform HTML escape to support file path containing space characters
        filePath = escapeFilePath(filePath);

        ModeController modeController = node.getMap().getModeController();

        // Convert the filePath to point to the selected node
        StringBuilder sb = new StringBuilder();
        sb.append("file:///");
        sb.append(filePath);
        sb.append('#');
        sb.append(modeController.getNodeID(node));
        return sb.toString();
    }

    /**
     * Get the relative URL from the source Node to the target Node, if possible.
     * Otherwise, return the absolute URL to the target Node.
     * 
     * @param  source a non-null instance of {@link MindMapNode}
     * @param  target a non-null instance of {@link MindMapNode}
     * @return        URL to the argument node as a string
     */
    public static String getRelativePathToNode(MindMapNode source, MindMapNode target) {
        assert (source != null);
        assert (target != null);

        File srcFile = source.getMap().getFile();
        if (srcFile == null) {
            throw new RuntimeException("Unable to get mindmap file name of the source node (probably not saved yet?)");
        }

        File tgtFile = target.getMap().getFile();
        if (tgtFile == null) {
            throw new RuntimeException("Unable to get mindmap file name of the target node (probably not saved yet?)");
        }

        String relPath = getRelativePathToTarget(srcFile, tgtFile);

        // Failed to get relative path
        if (relPath == null) {
            // Return the full URL to target node
            return getFullPathToNode(target);
        }

        // Perform HTML escape to support file path containing space characters
        String filePath = escapeFilePath(relPath.toString());

        ModeController modeController = target.getMap().getModeController();

        // Convert the filePath to point to the target node
        StringBuilder sb = new StringBuilder();
        sb.append(filePath);
        sb.append('#');
        sb.append(modeController.getNodeID(target));
        return sb.toString();
    }

    /**
     * Get the relative path from the source File to the target File.
     * 
     * Instead of using {@link Path#relativize(Path)}, we implement our own relative
     * path algorithm here simply to use the cross-platform path separator '/', so
     * that the cross-links in the mindmaps can work on Windows and on Linux.
     * 
     * @param  source a non-null instance of {@link File} which can be a file or a
     *                directory
     * @param  target a non-null instance of {@link File}, which can be a file or a
     *                directory
     * @return        String representation of the relative path, or null if there
     *                is no reasonable relative path from source to target
     */
    private static String getRelativePathToTarget(File source, File target) {
        assert source != null : "source cannot be null";
        assert target != null : "target cannot be null";
        assert target.isFile() : "target must be a directory";

        if (source.isFile()) {
            // Source is a file, get its parent directory
            source = source.getParentFile();
        }

        Path srcPath = Paths.get(source.getAbsolutePath());
        Path tgtPath = Paths.get(target.getAbsolutePath());

        // Not the same root (drive letter in Windows), no reasonable relative path
        // from source to target
        if (!srcPath.getRoot().equals(tgtPath.getRoot())) {
            return null;
        }

        // Find the common parent directory
        int len = Math.min(srcPath.getNameCount(), tgtPath.getNameCount());
        int commonIndex = 0;
        for (; commonIndex < len; commonIndex++) {
            if (!srcPath.getName(commonIndex).equals(tgtPath.getName(commonIndex))) {
                break;
            }
        }

        // Construct the relative path
        StringBuilder sb = new StringBuilder();

        // Travel from source up to commonIndex
        for (int i = srcPath.getNameCount(); i > commonIndex; i--) {
            sb.append("../");
        }

        // Travel from commonIndex down to target's parent
        for (int i = commonIndex; i < tgtPath.getNameCount() - 1; i++) {
            sb.append(tgtPath.getName(i).toString());
            sb.append("/");
        }

        // Add the target itself
        sb.append(tgtPath.getName(tgtPath.getNameCount() - 1).toString());

        return sb.toString();
    }

    /**
     * Escape an assumed file path for HTML / URL compatibility. For the moment it
     * means replacing the space character ' ' by the string "%20"
     * 
     * @param path non-null string representing a valid file path
     * @return escaped version of the file path
     */
    private static String escapeFilePath(String path) {
        assert (path != null);
        return path.replace(" ", "%20");
    }

}
