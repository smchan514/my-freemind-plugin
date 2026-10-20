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
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Unable to get mindmap file name (probably not saved yet?)", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OpenLinkedFileInExplorer.openExplorer(getController().getFrame().getJFrame(), file);
    }

}
