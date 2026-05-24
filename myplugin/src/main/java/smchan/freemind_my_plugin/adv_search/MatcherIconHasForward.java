package smchan.freemind_my_plugin.adv_search;

import java.util.List;

import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;

/**
 * Implementation of {@link IMatcherIcon} selecting nodes with the forward arrow
 * icon.
 */
public class MatcherIconHasForward implements IMatcherIcon {
    private static final String FORWARD_ICON_NAME = "forward";

    public MatcherIconHasForward() {
        // ...
    }

    @Override
    public boolean hasMatch(MindMapNode node) {
        @SuppressWarnings("unchecked")
        List<MindIcon> icons = node.getIcons();

        if (icons.isEmpty()) {
            // Node has no icon
            return false;
        }

        for (MindIcon icon : icons) {
            if (FORWARD_ICON_NAME.equals(icon.getName())) {
                // Matched the icon name!
                return true;
            }
        }

        // None of the icon matched
        return false;
    }
}
