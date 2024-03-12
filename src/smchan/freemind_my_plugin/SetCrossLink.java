package smchan.freemind_my_plugin;

import java.util.List;

import javax.swing.JOptionPane;

import freemind.controller.actions.generated.instance.AddLinkXmlAction;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.XmlAction;
import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * Precondition:
 * 
 * - Two nodes selected
 * 
 * Action:
 * 
 * - Set the local link mutually referencing each other on the two nodes
 *
 * <PRE>
 * [2020-12-22]
 *   - Added support for undo
 * </PRE>
 */
public class SetCrossLink extends ExportHook {
    public SetCrossLink() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Check for the preconditions
        List<?> selected = getController().getSelecteds();
        if (selected == null || selected.size() != 2) {
            JOptionPane.showMessageDialog(null, "Preconditions not met: get two nodes selected", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the node ID of each node
        MindMapNode node1 = (MindMapNode) selected.get(0);
        MindMapNode node2 = (MindMapNode) selected.get(1);

        if (node1.getLink() != null || node2.getLink() != null) {
            int rc = JOptionPane.showConfirmDialog(null, "Overwrite existing link(s) in nodes?", "Confirm",
                    JOptionPane.OK_CANCEL_OPTION);

            if (rc != JOptionPane.OK_OPTION) {
                // Cancel
                return;
            }
        }

        // Assign new text to the selected node with support for undo
        // using the existing "transaction" facility via MindMapController.
        MindMapController mmc = (MindMapController) getController();
        performTransaction(mmc, node1, node2);
    }

    public static void performTransaction(MindMapController mmc, MindMapNode node1, MindMapNode node2) {
        XmlAction editAction = createEditAction(mmc, node1, node2);
        XmlAction undoAction = createUndoAction(mmc, node1, node2);

        // Perform the transaction
        mmc.doTransaction("setLocalLinks", new ActionPair(editAction, undoAction));
    }

    /**
     * Create the action corresponding to the forward edit action which consists of
     * the two actions:
     * <OL>
     * <LI>Set the link on node1 to node2
     * <LI>Set the link on node2 to node1
     * </OL>
     */
    private static XmlAction createEditAction(MindMapController mmc, MindMapNode node1, MindMapNode node2) {
        CompoundAction compoundAction = new CompoundAction();
        AddLinkXmlAction childAction = new AddLinkXmlAction();
        childAction.setNode(mmc.getNodeID(node1));
        childAction.setDestination(getLocalLinkToNode(mmc, node2));
        compoundAction.addChoice(childAction);

        childAction = new AddLinkXmlAction();
        childAction.setNode(mmc.getNodeID(node2));
        childAction.setDestination(getLocalLinkToNode(mmc, node1));
        compoundAction.addChoice(childAction);
        return compoundAction;
    }

    /**
     * Create the action corresponding to the undo action which consists of the two
     * actions:
     * <OL>
     * <LI>Set the link on node1 to its current value
     * <LI>Set the link on node2 to its current value
     * </OL>
     */
    private static XmlAction createUndoAction(MindMapController mmc, MindMapNode node1, MindMapNode node2) {
        CompoundAction compoundAction = new CompoundAction();
        AddLinkXmlAction childAction = new AddLinkXmlAction();
        childAction.setNode(mmc.getNodeID(node1));
        childAction.setDestination(node1.getLink());
        compoundAction.addChoice(childAction);

        childAction = new AddLinkXmlAction();
        childAction.setNode(mmc.getNodeID(node2));
        childAction.setDestination(node2.getLink());
        compoundAction.addChoice(childAction);
        return compoundAction;
    }

    private static String getLocalLinkToNode(MindMapController mmc, MindMapNode node) {
        return "#" + mmc.getNodeID(node);
    }
}
