package smchan.freemind_my_plugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;

public class AdvancedSearch extends ModeControllerHookAdapter {

    public AdvancedSearch() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        String regex = JOptionPane.showInputDialog("Search REGEX's");
        Pattern patterns[] = compileRegexes(regex);

        List<?> list = getController().getSelecteds();
        for (Object object : list) {
            MindMapNode node = (MindMapNode) object;
            recursiveSearch(patterns, node);
        }

    }

    private Pattern[] compileRegexes(String regex) {
        String[] parts = regex.split("\\s");
        Pattern[] patterns = new Pattern[parts.length];

        for (int i = 0; i < parts.length; i++) {
            patterns[i] = Pattern.compile(parts[i], Pattern.CASE_INSENSITIVE);
        }
        return patterns;
    }

    private void recursiveSearch(Pattern[] patterns, MindMapNode node) {
        String nodeText = node.getText();
        if (hasAtLeastOneMatch(patterns, nodeText)) {
            logPositiveResult(nodeText);
        }

        List<MindMapNode> children = node.getChildren();
        for (MindMapNode child : children) {
            recursiveSearch(patterns, child);
        }
    }

    private boolean hasAtLeastOneMatch(Pattern[] patterns, String text) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // Return true when one of the pattern has a match
                return true;
            }
        }
        // None of the patterns matched
        return false;
    }

    private void logPositiveResult(String nodeText) {
        System.out.println(nodeText);
    }
}
