package smchan.freemind_my_plugin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import freemind.controller.actions.generated.instance.CompoundAction;
import freemind.controller.actions.generated.instance.DeleteNodeAction;
import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.controller.actions.generated.instance.NewNodeAction;
import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

public class InsertWeekNumbers extends ExportHook {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(InsertWeekNumbers.class.getName());

    // Per ISO-8601, the first week has at least four (4) days in it
    private static final int DEFAULT_MIN_DAYS_IN_FIRST_WEEK = 4;

    // Plugin resource name
    private static final String RES_KEY_MIN_DAYS_IN_FIRST_WEEK = "min_days_in_first_week";

    ////////////////////////////////////////////////////////////
    // Configurations

    private boolean _initialized = false;

    private int _minDaysInFirstWeek = DEFAULT_MIN_DAYS_IN_FIRST_WEEK;

    public InsertWeekNumbers() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Lazy init
        if (!_initialized) {
            performInit();
        }

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int firstWeek = 1;

        String str = JOptionPane.showInputDialog("Calendar Year?", Integer.toString(year));
        year = Integer.parseInt(str);

        str = JOptionPane.showInputDialog("First week?", Integer.toString(firstWeek));
        firstWeek = Integer.parseInt(str);

        String[] nodeTexts = generateNodeTexts(year, firstWeek);

        // Add new nodes (with undo in one step)
        addNewNodes(nodeTexts);
    }

    private void performInit() {
        String str;

        if ((str = getResourceString(RES_KEY_MIN_DAYS_IN_FIRST_WEEK)) != null) {
            _minDaysInFirstWeek = Integer.parseInt(str);
        }

        _initialized = true;
    }

    private void addNewNodes(String[] nodeTexts) {
        MindMapController mmc = (MindMapController) getController();
        MindMapNode parent = mmc.getSelected();
        String parentNodeId = mmc.getNodeID(parent);
        int insertIndex = parent.getChildCount();

        // Create compound DO and UNDO actions
        CompoundAction doActions = new CompoundAction();
        CompoundAction undoActions = new CompoundAction();

        // For all the node nodes to create...
        for (int i = 0; i < nodeTexts.length; i++) {
            String nodeText = nodeTexts[i];
            String newNodeId = mmc.getModel().getLinkRegistry().generateUniqueID(null);

            // Add to the compound DO action: create a new node and set the node text
            doActions.addChoice(createNewNodeAction(parentNodeId, insertIndex + i, newNodeId, nodeText));
            doActions.addChoice(createEditNodeAction(newNodeId, nodeText));

            // Add to the compound UNDO action: delete the new node using its node Id
            undoActions.addChoice(createDeleteNodeAction(newNodeId));
        }

        // Execute the transaction
        mmc.doTransaction("Insert weeks", new ActionPair(doActions, undoActions));
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

    private String[] generateNodeTexts(int year, int firstWeek) {
        LinkedList<String> lst = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        cal.setMinimalDaysInFirstWeek(_minDaysInFirstWeek);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, firstWeek);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date startdate = cal.getTime();

        LOGGER.info("startdate=" + startdate);

        do {
            // Insert the week number label
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            Date date = cal.getTime();
            lst.add(String.format("[%s] wk%02d", sdf.format(date), week));

            // Move up 7 days
            cal.add(Calendar.DAY_OF_MONTH, 7);
        } while (cal.get(Calendar.YEAR) <= year);

        return lst.toArray(new String[0]);
    }

}
