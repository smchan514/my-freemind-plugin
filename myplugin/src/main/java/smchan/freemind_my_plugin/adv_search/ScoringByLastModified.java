package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

/**
 * Implementation of {@link IScoring} where the <i>match score</i> is a function
 * of the last modified timestamp of the node.
 */
public class ScoringByLastModified implements IScoring {

    private boolean _oldestFirst = true;
    private final long _refTime;

    /**
     * @param oldestFirst boolean value, when true, indicates search results should
     *                    be sorted in chronological order
     */
    public ScoringByLastModified(boolean oldestFirst) {
        _oldestFirst = oldestFirst;
        _refTime = System.currentTimeMillis();
    }

    @Override
    public long getMatchScore(MindMapNode node, int relevance) {
        long lastModified = node.getHistoryInformation().getLastModifiedAt().getTime();

        if (_oldestFirst) {
            // Return number of milliseconds from last modified time and the reference time
            // taken during construction of this object
            return _refTime - lastModified;
        }

        // Newest first... return the last modified time as-is
        return lastModified;
    }

}
