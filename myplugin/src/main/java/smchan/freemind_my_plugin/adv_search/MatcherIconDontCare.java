package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

/**
 * Null implementation of {@link IMatcherIcon} which matches any node.
 */
public class MatcherIconDontCare implements IMatcherIcon {

    public MatcherIconDontCare() {
        // ...
    }

    @Override
    public boolean hasMatch(MindMapNode node) {
        return true;
    }
}
