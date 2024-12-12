package smchan.freemind_my_plugin;

import java.awt.Component;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import freemind.extensions.ExportHook;
import freemind.main.HtmlTools;
import freemind.modes.ControllerAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * Normalize white space in node text
 */
public class NormalizeWhiteSpace extends ExportHook {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(NormalizeWhiteSpace.class.getName());

    // Regex used to split a list of words separated by at least one whitespace
    private static final String REGEX_WHITESPACES = "\\s+";

    public NormalizeWhiteSpace() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        MindMapController mmc = (MindMapController) getController();

        // Handle the case where this plugin is activated when a short node edit is in
        // progress. Check if keyboard focus is on a JTextField...
        JTextField jtf = getShortNodeEditTextField(mmc);
        if (jtf != null) {
            applyCaseChangeInTextField(jtf);
        } else {
            applyCaseChangeToSelectedNodes(mmc);
        }
    }

    private void applyCaseChangeToSelectedNodes(MindMapController mmc) {
        List<?> selected = mmc.getSelecteds();

        // Check preconditions
        try {
            checkAtLeastOneNodeSelected(selected);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(), e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Object object : selected) {
            changeNodeText(mmc, (MindMapNode) object);
        }
    }

    private void applyCaseChangeInTextField(JTextField jtf) {
        int startPos = jtf.getSelectionStart();
        int endPos = jtf.getSelectionEnd();
        boolean hasSelectedText = (startPos != endPos);
        String nodeText = (hasSelectedText) ? jtf.getSelectedText() : jtf.getText();
        String newText = getUpdatedNodeText(nodeText);

        if (hasSelectedText) {
            Document doc = jtf.getDocument();
            try {
                // Remove previously selected text then insert the new text
                doc.remove(startPos, endPos - startPos);
                doc.insertString(startPos, newText, null);
            } catch (BadLocationException e) {
                LOGGER.log(Level.WARNING, "Failed to manipulate document", e);
            }

            // Select the new text
            jtf.select(startPos, startPos + newText.length());
        } else {
            // Replace the entire text field
            jtf.setText(newText);
        }
        return;
    }

    private JTextField getShortNodeEditTextField(ControllerAdapter mmc) {
        Component comp = mmc.getFrame().getJFrame().getFocusOwner();
        if (comp instanceof JTextField) {
            return (JTextField) comp;
        }

        return null;
    }

    private void checkAtLeastOneNodeSelected(List<?> selected) {
        if (selected == null || selected.isEmpty()) {
            throw new RuntimeException("No node selected");
        }
    }

    private void changeNodeText(MindMapController mmc, MindMapNode node) {
        String nodeText = node.getText();
        if (nodeText == null || nodeText.isEmpty()) {
            // No text? Do nothing...
            return;
        }

        String newText = getUpdatedNodeText(nodeText);
        mmc.setNodeText(node, newText);
    }

    private String getUpdatedNodeText(String nodeText) {
        if (HtmlTools.isHtmlNode(nodeText)) {
            // Strip HTML (rich formatting) in original node text
            nodeText = HtmlTools.htmlToPlain(nodeText);
        }

        String[] parts = nodeText.trim().split(REGEX_WHITESPACES);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];

            if (i > 0) {
                // Add single space between words
                sb.append(' ');
            }

            // Skip empty string
            if (p.isEmpty()) {
                continue;
            }

            sb.append(p);
        }

        return sb.toString();
    }
}
