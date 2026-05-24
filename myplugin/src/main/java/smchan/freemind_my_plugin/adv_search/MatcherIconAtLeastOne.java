package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

/**
 * Implementation of {@link IMatcherIcon} selecting nodes with at least one icon
 * decoration.
 */
public class MatcherIconAtLeastOne implements IMatcherIcon {

    public MatcherIconAtLeastOne() {
        // ...
    }

    @Override
    public boolean hasMatch(MindMapNode node) {
        return !node.getIcons().isEmpty();
    }
}
