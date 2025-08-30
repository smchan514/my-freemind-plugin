package smchan.freemind_my_plugin;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;

/**
 * Open the linked file, if applicable, in Windows Explorer
 */
public class OpenLinkedFileInExplorer extends ExportHook {
    private static final String DEFAULT_ENCDOING = "utf-8";

    public OpenLinkedFileInExplorer() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Skip the rest unless there is only one node selected
        List<?> selected = getController().getSelecteds();
        if (selected == null || selected.size() != 1) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Preconditions not met: get one node selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MindMapNode selectedNode = (MindMapNode) selected.get(0);
        String link = selectedNode.getLink();

        if (link == null) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Node has no link", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Try to resolve link to a path to local file system
        File path = getLocalPath(link);
        if (path == null) {
            try {
                // Unable to resolve link... maybe there is URL encoding in the link?
                String link2 = URLDecoder.decode(link, DEFAULT_ENCDOING);

                // Try to resolve again...
                path = getLocalPath(link2);
            } catch (UnsupportedEncodingException e) {
                // Failed to decode URL... give up
            }
        }

        // If we managed to resolve the link
        if (path != null) {
            openExplorer(path);
        } else {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Unable to resolve link to local file: " + link, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private File getLocalPath(String link) {
        // Check if the link is absolute path to an existing file
        File file = new File(link);
        if (file.exists()) {
            return file;
        }

        // Path relative to current mindmap
        File fileMap = getController().getMap().getFile();
        if (fileMap != null) {
            file = new File(fileMap.getParent(), link);
            if (file.exists()) {
                return file;
            }
        }

        // link is unlikely pointing to a local file
        return null;
    }

    private void openExplorer(File file) {
        try {
            // Start an 'explorer' process in Windows
            // https://stackoverflow.com/questions/13680415/how-to-open-explorer-with-a-specific-file-selected
            
            // The following works with files with space characters
            ProcessBuilder pb = new ProcessBuilder("explorer.exe", "/select,", file.toString());
            pb.start();

            // Not sure if we need to wait for explorer to return...
            // proc.waitFor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Failed to open explorer " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

}
