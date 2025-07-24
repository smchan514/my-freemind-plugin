package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

/**
 * Generic interface for a search result scoring component
 */
public interface IScoring {

    /**
     * Get the match score for the argument node and its computed relevance based on
     * the scoring implementation
     * 
     * @param  node      a non-null instance of {@link MindMapNode}
     * @param  relevance computed relevance of the node
     * @return           match score as a 64-bit integer, node with higher score
     *                   should be shown before others
     */
    long getMatchScore(MindMapNode node, int relevance);

}
