package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

public class ScoringByLastModified implements IScoring {

    private boolean _oldestFirst = true;
    private final long _refTime;

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
