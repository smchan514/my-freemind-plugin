package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;

/**
 * Generic interface of"matcher for icon decoration.
 */
public interface IMatcherIcon {
    /**
     * @param  node a non-null instance of {@link MindMapNode}
     * @return      true if there is a match for icon decoration based on the
     *              concrete rule implemeted by the concrete class
     */
    boolean hasMatch(MindMapNode node);
}
