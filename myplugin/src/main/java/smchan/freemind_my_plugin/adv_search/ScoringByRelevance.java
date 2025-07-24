package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

/**
 * Implementation of {@link IScoring} where the <i>match score</i> is simply the
 * <i>match relevance</i>.
 */
public class ScoringByRelevance implements IScoring {

    public ScoringByRelevance() {
        // ...
    }

    @Override
    public long getMatchScore(MindMapNode node, int relevance) {
        return relevance;
    }

}
