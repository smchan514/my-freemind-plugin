package smchan.freemind_my_plugin;

import java.util.List;

import javax.swing.JOptionPane;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;
import smchan.freemind_my_plugin.mru.MRUNodesModel;
import smchan.freemind_my_plugin.mru.MRUNodesModelAccessor;

/**
 * Pin currently selected nodes in MRUNodes dialog.
 */
public class PinMRUNodes extends ModeControllerHookAdapter {

    public PinMRUNodes() {
        // ...
    }

    @Override
    public void startupMapHook() {
        // Skip the rest unless there is at least one node selected
        List<?> selected = getController().getSelecteds();
        if (selected == null || selected.size() == 0) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Preconditions not met: No node selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get access to the global instance of MRUNodesModel
        MRUNodesModel mruModel = MRUNodesModelAccessor.getMRUNodesModel(getController().getFrame().getController());

        // Mark all selected nodes as pinned in the MRUNodes dialog
        for (Object object : selected) {
            MindMapNode selectedNode = (MindMapNode) object;
            mruModel.setPinStatus(selectedNode);
        }
    }
}
