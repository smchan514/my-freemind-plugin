package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

public interface IScoring {

    long getMatchScore(MindMapNode node, int relevance);

}
