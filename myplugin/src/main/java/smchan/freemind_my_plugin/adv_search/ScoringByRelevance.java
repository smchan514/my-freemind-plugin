package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

public class ScoringByRelevance implements IScoring {

    public ScoringByRelevance() {
        // ...
    }

    @Override
    public long getMatchScore(MindMapNode node, int relevance) {
        return relevance;
    }

}
