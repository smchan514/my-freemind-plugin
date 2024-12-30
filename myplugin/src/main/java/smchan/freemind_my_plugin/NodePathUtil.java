package smchan.freemind_my_plugin;

import java.util.LinkedList;

import freemind.modes.MindMapNode;

/**
 * Utility methods for {@linkplain NodePath}
 */
public class NodePathUtil {

    /**
     * Create a {@linkplain NodePath} object representing the path to the argument
     * {@linkplain MindMapNode} from the root node of the corresponding mind map
     * 
     * @param  node a non-null instance of {@linkplain MindMapNode}
     * @return      an instance of {@linkplain NodePath}
     */
    public static NodePath getNodePath(MindMapNode node) {
        assert node != null;

        LinkedList<Integer> path = new LinkedList<>();

        // Iterate until we hit the root node
        while (node != null) {
            MindMapNode parent = node.getParentNode();
            if (parent != null) {
                int index = parent.getChildPosition(node);
                path.addFirst(index);
            }
            node = parent;
        }

        return new NodePath(path);
    }
}
