package smchan.freemind_my_plugin;

import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import freemind.extensions.ExportHook;
import freemind.main.HtmlTools;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * Toggle selected nodes between the following cases:
 * <UL>
 * <LI>ALL UPPER CASE
 * <li>all lower case
 * <LI>Title Case With Support for Minor Words
 * </UL>
 */
public class ToggleWordCases extends ExportHook {
    // Plugin resource name
    private static final String RES_KEY_MINOR_WORDS = "minor_words";

    // Regex used to split a list of words separated by at least one whitespace
    private static final String REGEX_WHITESPACES = "\\W+";

    private enum WordCase {
        ALL_UPPER_CASE,
        ALL_LOWER_CASE,
        TITLE_CASE,
    }

    ////////////////////////////////////////////////////////////
    // Configurations

    private boolean _initialized = false;

    // Set of minor words
    private final HashSet<String> _minorWords = new HashSet<>();

    public ToggleWordCases() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Lazy init
        if (!_initialized) {
            performInit();
        }

        MindMapController mmc = (MindMapController) getController();
        List<?> selected = mmc.getSelecteds();
        WordCase currCase = null;

        // Check preconditions
        try {
            checkAtLeastOneNodeSelected(selected);

            // Determine the current case using the first selected ode
            currCase = getCurrentWordCase((MindMapNode) selected.get(0));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the word case to which to transform the node texts
        WordCase nextCase = getNextCase(currCase);

        for (Object object : selected) {
            changeWordCase(mmc, (MindMapNode) object, nextCase);
        }
    }

    private void performInit() {
        String str;

        if ((str = getResourceString(RES_KEY_MINOR_WORDS)) != null) {
            // Minor words separated by whitespace characters
            String[] parts = str.split(REGEX_WHITESPACES);
            for (String p : parts) {
                _minorWords.add(p);
            }
        }

        _initialized = true;
    }

    private void checkAtLeastOneNodeSelected(List<?> selected) {
        if (selected == null || selected.isEmpty()) {
            throw new RuntimeException("No node selected");
        }
    }

    private void changeWordCase(MindMapController mmc, MindMapNode node, WordCase wordCase) {
        String nodeText = node.getText();
        if (nodeText == null || nodeText.isEmpty()) {
            // No text? Do nothing...
            return;
        }

        if (HtmlTools.isHtmlNode(nodeText)) {
            // Strip HTML (rich formatting) in original node text
            nodeText = HtmlTools.htmlToPlain(nodeText);
        }

        // Perform the edit with automatic undo provided by MindMapController
        String newText = transformNodeText(wordCase, nodeText);
        mmc.setNodeText(node, newText);
    }

    private String transformNodeText(WordCase wordCase, String nodeText) {
        switch (wordCase) {
        case ALL_UPPER_CASE:
            return nodeText.toUpperCase();

        case ALL_LOWER_CASE:
            return nodeText.toLowerCase();

        case TITLE_CASE:
            return changeNodeTextToTitleCase(nodeText);

        default:
            throw new RuntimeException("Unknown WordCase: " + wordCase);
        }
    }

    private String changeNodeTextToTitleCase(String nodeText) {
        String[] parts = nodeText.split(REGEX_WHITESPACES);
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
                
            // Don't change minor words after the first word
            if (i > 0 && _minorWords.contains(p)) {
                sb.append(p);
            } else {
                // Set the first letter to upper case
                sb.append(Character.toUpperCase(p.charAt(0)));
                
                // Change the remaining letters to lower case
                if (p.length() > 1) {
                    sb.append(p.substring(1).toLowerCase());
                }
            }
        }

        return sb.toString();
    }

    private WordCase getNextCase(WordCase currCase) {
        WordCase[] values = WordCase.values();
        return values[(currCase.ordinal() + 1) % values.length];
    }

    private WordCase getCurrentWordCase(MindMapNode node) {
        String text = node.getText();
        if (text == null || text.isEmpty()) {
            throw new RuntimeException("Unable to determine current word case: the first selected node has no text");
        }

        char[] chars = text.toCharArray();
        if (isAllUpperCase(chars)) {
            return WordCase.ALL_UPPER_CASE;
        } else if (isAllLowerCase(chars)) {
            return WordCase.ALL_LOWER_CASE;
        }

        // If we get here, assume the text has mixed cases
        return WordCase.TITLE_CASE;
    }

    private boolean isAllLowerCase(char[] chars) {
        for (char c : chars) {
            // Return false at the first upper case letter
            if (Character.isUpperCase(c)) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllUpperCase(char[] chars) {
        for (char c : chars) {
            // Return false at the first lower case letter
            if (Character.isLowerCase(c)) {
                return false;
            }
        }

        return true;
    }

}
