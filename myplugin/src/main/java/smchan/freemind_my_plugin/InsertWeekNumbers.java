package smchan.freemind_my_plugin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

    // Per ISO 8601, the first week has at least four (4) days in it
    private static final int DEFAULT_MIN_DAYS_IN_FIRST_WEEK = 4;

    // Per ISO 8601, the first day of the week is Monday
    private static final int FIRST_DAT_OF_WEEK = Calendar.MONDAY;

    public InsertWeekNumbers() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Get user input from a modal dialog
        InsertWeekNumbersDialog dialog = new InsertWeekNumbersDialog();

        // Skip the rest if the user cancels
        if (!dialog.showDialog()) {
            return;
        }

        int year = dialog.getYear();
        int firstWeek = dialog.getFirstWeek();
        boolean separateByQuarters = dialog.getSeparateByQuarters();

        try {
            WeekModel[] weekModels = generateWeekModels(year, firstWeek);

            // Add new nodes (with undo in one step)
            addNewNodes(weekModels, separateByQuarters);
        } catch (IllegalArgumentException e) {
            // Catch the IllegalArgumentException raised by Calendar which has no message
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(), "Invalid input values", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(), e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewNodes(WeekModel[] nodeModels, boolean separateByQuarters) {
        // Keep track of the parent nodes
        HashMap<String, ParentNodeInfo> mapParents = new HashMap<>();

        // Insert the node selected in the mind map as the "root" node
        MindMapController mmc = (MindMapController) getController();
        MindMapNode rootNode = mmc.getSelected();
        String rootNodeId = mmc.getNodeID(rootNode);
        int rootInsertIndex = rootNode.getChildCount();
        mapParents.put("root", new ParentNodeInfo(rootNodeId, rootInsertIndex));

        // Create compound DO and UNDO actions
        CompoundAction doActions = new CompoundAction();
        CompoundAction undoActions = new CompoundAction();

        if (separateByQuarters) {
            // Insert the quarter nodes...
            String lastQuarter = null;
            for (int i = 0; i < nodeModels.length; i++) {
                String thisQuarter = nodeModels[i]._textQuarter;
                if (lastQuarter == null || !lastQuarter.equals(thisQuarter)) {
                    String newNodeId = createActions(mmc, doActions, undoActions, rootNodeId, rootInsertIndex++,
                            thisQuarter);

                    // Create a new "parent node" for all the weeks in this quarter
                    mapParents.put(thisQuarter, new ParentNodeInfo(newNodeId, 0));
                    lastQuarter = thisQuarter;
                }
            }
        }

        // For all the week nodes to create...
        ParentNodeInfo parent = mapParents.get("root");
        for (int i = 0; i < nodeModels.length; i++) {
            WeekModel weekModel = nodeModels[i];

            if (separateByQuarters) {
                parent = mapParents.get(weekModel._textQuarter);
                assert (parent != null);
            }

            createActions(mmc, doActions, undoActions, parent._nodeId, parent._insertIndex++, weekModel._textWeek);
        }

        // Execute the transaction
        mmc.doTransaction("Insert weeks", new ActionPair(doActions, undoActions));
    }

    private String createActions(MindMapController mmc, CompoundAction doActions, CompoundAction undoActions,
            String parentNodeId, int insertIndex, String nodeLabel) {
        String newNodeId = mmc.getModel().getLinkRegistry().generateUniqueID(null);

        // Add to the compound DO action: create a new node and set the node text
        doActions.addChoice(createNewNodeAction(parentNodeId, insertIndex, newNodeId));
        doActions.addChoice(createEditNodeAction(newNodeId, nodeLabel));

        // Add to the compound UNDO action: delete the new node using its node Id
        // At at zero so the last added node is deleted first
        undoActions.addAtChoice(0, createDeleteNodeAction(newNodeId));
        return newNodeId;
    }

    private NewNodeAction createNewNodeAction(String parentNodeId, int index, String newId) {
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

    private WeekModel[] generateWeekModels(int year, int firstWeek) {
        LinkedList<WeekModel> lst = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        // Use non-lenient calendar to detect invalid input values
        cal.setLenient(false);

        // Configure calendar
        cal.setMinimalDaysInFirstWeek(DEFAULT_MIN_DAYS_IN_FIRST_WEEK);
        cal.setFirstDayOfWeek(FIRST_DAT_OF_WEEK);

        // Set starting year/week to get total number of weeks for the year
        cal.setWeekDate(year, firstWeek, FIRST_DAT_OF_WEEK);
        Date startdate = cal.getTime();
        int totalWeeks = cal.getWeeksInWeekYear();

        LOGGER.info("startdate=" + startdate + ", totalWeeks=" + totalWeeks);

        // For all the week numbers, inclusive of the last one...
        for (int thisWeek = firstWeek; thisWeek <= totalWeeks; thisWeek++) {
            int quarter = getQauter(thisWeek);

            // Get the date for the first day of the week
            cal.setWeekDate(year, thisWeek, FIRST_DAT_OF_WEEK);
            Date date = cal.getTime();

            // Insert the node texts
            String textWeek = String.format("[%s] wk%02d", sdf.format(date), thisWeek);
            String textQuarter = String.format("Q%d", quarter);
            lst.add(new WeekModel(textQuarter, textWeek));
        }

        return lst.toArray(new WeekModel[0]);
    }

    /**
     * Compute the corresponding quarter number: 13 weeks per quarter, last quarter
     * may have 14 weeks
     * 
     * @param week week number, starting at 1
     * @return quarter number, starting at 1
     */
    private int getQauter(int week) {
        assert (week > 0);
        assert (week <= 53);

        if (week <= 13) {
            return 1;
        }

        if (week <= 26) {
            return 2;
        }

        if (week <= 39) {
            return 3;
        }

        return 4;
    }

    //////////////////////////////////////////////
    private static class WeekModel {
        public final String _textQuarter;
        public final String _textWeek;

        public WeekModel(String textQuarter, String textWeek) {
            _textQuarter = textQuarter;
            _textWeek = textWeek;
        }
    }

    //////////////////////////////////////////////
    private static class ParentNodeInfo {
        public String _nodeId;
        public int _insertIndex;

        public ParentNodeInfo(String nodeId, int insertIndex) {
            _nodeId = nodeId;
            _insertIndex = insertIndex;
        }
    }

}
