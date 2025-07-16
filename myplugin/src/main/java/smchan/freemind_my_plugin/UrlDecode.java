package smchan.freemind_my_plugin;

import java.net.URLDecoder;
import java.util.List;

import javax.swing.JOptionPane;

import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * Perform URL decode of the currently selected node(s)
 */
public class UrlDecode extends ExportHook {

	private static final String DEFAULT_ENCODING = "utf-8";

	public UrlDecode() {
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

		try {
			MindMapNode selectedNode = (MindMapNode) selected.get(0);
			String oldText = selectedNode.getText();
			if(oldText == null || oldText.length() == 0) {
				return;
			}
			
			String newText = URLDecoder.decode(oldText, DEFAULT_ENCODING);

	        // Assign new text to the selected node with support for undo
	        // using the existing "transaction" facility via MindMapController.
	        MindMapController mmc = (MindMapController) getController();

	        // Forward action
	        EditNodeAction editAction = new EditNodeAction();
	        editAction.setNode(mmc.getNodeID(selectedNode));
	        editAction.setText(newText);

	        // Undo action
	        EditNodeAction undoEditAction = new EditNodeAction();
	        undoEditAction.setNode(mmc.getNodeID(selectedNode));
	        undoEditAction.setText(oldText);

	        // Perform the transaction
	        mmc.doTransaction(getName(), new ActionPair(editAction, undoEditAction));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
