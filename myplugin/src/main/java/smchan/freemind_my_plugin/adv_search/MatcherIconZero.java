package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

/**
 * Implementation of {@link IMatcherIcon} selecting nodes with zero icon
 * decoration.
 */
public class MatcherIconZero implements IMatcherIcon {

    public MatcherIconZero() {
        // ...
    }

    @Override
    public boolean hasMatch(MindMapNode node) {
        return node.getIcons().isEmpty();
    }
}
