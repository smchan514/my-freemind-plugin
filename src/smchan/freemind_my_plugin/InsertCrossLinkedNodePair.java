package smchan.freemind_my_plugin;

import freemind.controller.actions.generated.instance.AddLinkXmlAction;
import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class InsertCrossLinkedNodePair extends ExportHook {

    public InsertCrossLinkedNodePair() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        addNewNodes();
    }

    private void addNewNodes() {
        MindMapController mmc = (MindMapController) getController();
        MindMapNode parent = mmc.getSelected();
        String parentNodeId = mmc.getNodeID(parent);
        int insertIndex = parent.getChildCount();

        // Create compound DO and UNDO actions
        CompoundAction doActions = new CompoundAction();
        CompoundAction undoActions = new CompoundAction();

        // Create the actions to do/undo node creation
        String nodeIdArrowRite = addNode(mmc, doActions, undoActions, parentNodeId, ">>>", insertIndex);
        String nodeIdArrowLeft = addNode(mmc, doActions, undoActions, parentNodeId, "<<<", insertIndex+1);

        // Create the actions to set cross links
        // No need for actions to undo cross links... we'll just delete the nodes
        addSetLinkActions(doActions, nodeIdArrowRite, nodeIdArrowLeft);
        addSetLinkActions(doActions, nodeIdArrowLeft, nodeIdArrowRite);

        // Execute the transaction
        mmc.doTransaction("Insert cross-linked node pair", new ActionPair(doActions, undoActions));
    }

    private void addSetLinkActions(CompoundAction compoundAction, String nodeIdSrc, String nodeIdDst) {
        AddLinkXmlAction childAction = new AddLinkXmlAction();
        childAction.setNode(nodeIdSrc);
        childAction.setDestination("#" + nodeIdDst);
        compoundAction.addChoice(childAction);

        childAction = new AddLinkXmlAction();
        childAction.setNode(nodeIdDst);
        childAction.setDestination("#" + nodeIdSrc);
        compoundAction.addChoice(childAction);
    }

    private String addNode(MindMapController mmc, CompoundAction doActions, CompoundAction undoActions,
            String parentNodeId, String nodeText, int nodeIdex) {
        String newNodeId = mmc.getModel().getLinkRegistry().generateUniqueID(null);

        // Add to the compound DO action: create a new node and set the node text
        doActions.addChoice(createNewNodeAction(parentNodeId, nodeIdex, newNodeId, nodeText));
        doActions.addChoice(createEditNodeAction(newNodeId, nodeText));

        // Add to the compound UNDO action: delete the new node using its node Id
        undoActions.addChoice(createDeleteNodeAction(newNodeId));
        return newNodeId;
    }

    private NewNodeAction createNewNodeAction(String parentNodeId, int index, String newId, String nodeText) {
        NewNodeAction newNodeAction = new NewNodeAction();
        newNodeAction.setNode(parentNodeId);
        newNodeAction.setPosition("right");
        newNodeAction.setIndex(index);
        newNodeAction.setNewId(newId);
        return newNodeAction;
    }

    private EditNodeAction createEditNodeAction(String nodeId, String newText) {
        EditNodeAction editAction = new EditNodeAction();
        editAction.setNode(nodeId);
        editAction.setText(newText);
        return editAction;
    }

    private DeleteNodeAction createDeleteNodeAction(String newId) {
        DeleteNodeAction deleteAction = new DeleteNodeAction();
        deleteAction.setNode(newId);
        return deleteAction;
    }

}
