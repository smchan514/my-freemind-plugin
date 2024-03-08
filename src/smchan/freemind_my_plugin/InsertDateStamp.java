package smchan.freemind_my_plugin;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import freemind.controller.actions.generated.instance.EditNodeAction;
import freemind.extensions.ExportHook;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.modes.mindmapmode.actions.xml.ActionPair;

/**
 * Insert timestamp at the selected node in FreeMind
 * 
 * <PRE>
 * [2020-12-22]
 *   - Added property "time_zone"
 *   - Added support for undo
 * </PRE>
 */
public class InsertDateStamp extends ExportHook {
    private boolean _initialized = false;
    private SimpleDateFormat _sdf;

    /**
     * Timestamp insertion format with the following positional arguments
     * 
     * <PRE>
     *   - {0}: Existing text
     *   - {1}: Timestamp
     * </PRE>
     */
    private String _insertFormat;

    /**
     * Simple date format
     */
    private String _dateFormat = "'['yyyy-MM-dd']'";

    public InsertDateStamp() {
        // Use default format and time zone
        _sdf = new SimpleDateFormat(_dateFormat);
        _insertFormat = "{1} {0}";
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Lazy init of this action
        if (!_initialized)
            performInit();

        // Skip the rest unless there is only one node selected
        List<?> list = getController().getSelecteds();
        if (list.size() != 1)
            return;

        MindMapNode selectedNode = (MindMapNode) list.get(0);

        String oldText = selectedNode.getText();
        String newText = selectedNode.getText();
        String timestamp = _sdf.format(new Date());

        // Get current text in the node
        if (newText.isEmpty()) {
            // No current text, put the timestamp as-is
            newText = timestamp;
        } else {
            // Insert timestamp into current text using _insertFormat
            newText = MessageFormat.format(_insertFormat, newText, timestamp);
        }

        // Assign new text to the selected node with support for undo
        // using the existing "transaction" facility via MindMapController.
        // EditNodeAction is generated from XSD and is processed by
        // Freemind infrastructure.
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
    }

    /**
     * Perform initialization of this action
     */
    private void performInit() {
        String str;

        if ((str = getResourceString("date_format")) != null) {
            _dateFormat = str;
            _sdf = new SimpleDateFormat(str);
        }

        if ((str = getResourceString("insert_format")) != null) {
            _insertFormat = str;
        }

        if ((str = getResourceString("time_zone")) != null && !str.isEmpty()) {
            TimeZone tz = TimeZone.getTimeZone(str);
            _sdf.setTimeZone(tz);
        }

        _initialized = true;
    }
}
