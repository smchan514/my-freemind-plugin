package smchan.freemind_my_plugin;

import java.io.File;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;

/**
 * Open the current mindmap in Windows Explorer
 */
public class OpenMindMapInExplorer extends ExportHook {
    public OpenMindMapInExplorer() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        File file = getController().getMap().getFile();
        if (file == null) {
            JOptionPane.showMessageDialog(getController().getView(),
                    "Unable to get mindmap file name (probably not saved yet?)", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Start an 'explorer' process
            // See http://support.microsoft.com/kb/152457
            String cmd = "explorer /select," + file;
            Runtime.getRuntime().exec(cmd);

            // Not sure if we need to wait for explorer to return...
            // proc.waitFor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getController().getView(), "Failed to open explorer " + e.getMessage(),
                    "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

}
