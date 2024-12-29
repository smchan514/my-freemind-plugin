package smchan.freemind_my_plugin;

import java.util.List;

/**
 * Data object representing a node path, e.g. 1.2.1.3.0, each number
 * corresponding the children nodes index starting from the root node down to
 * the last parent node in order to reach a specific node.
 * 
 * The node path is complementary to the "node ID" with the main differences
 * below:
 * <OL>
 * <LI>The node path to a node is always defined where as some nodes may have
 * have node ID assigned.
 * <LI>Once a node ID is assigned to a node, the ID is unlikely to change due to
 * subsequent edits in the corresponding mind map. The node path, on the other
 * hand, is susceptible to change when any node in the path is modified, e.g.
 * add/remove children nodes, moved from one branch to another.
 * </OL>
 */
public class NodePath {
    // Immutable array of indexes
    private final int[] _path;

    /**
     * @param list non-null {@linkplain List} of {@linkplain Integer}s
     */
    public NodePath(List<Integer> list) {
        assert list != null;

        _path = new int[list.size()];
        int i = 0;
        for (Integer integer : list) {
            _path[i++] = integer;
        }
    }

    /**
     * Compute the pseudo Euclidean distance between this node path and an other one
     * specified in parameter.
     * 
     * It is pseudo Euclidean distance to account for the cases where the two node
     * paths have different number of dimensions. In the context of a mind map, any
     * node is "one away" from its parent node
     * 
     * @param  other a non-null instance of NodePath
     * @return       distance between the node paths
     */
    public double getDistance(NodePath other) {
        assert other != null;

        // Each extra dimension counts as a distance of 1
        // Need to raise to the power of 2 because we are
        // doing a square root at the end
        double sum = Math.pow(this._path.length - other._path.length, 2);

        // Compute Euclidean distance for the "common dimensions"
        int dim = Math.min(this._path.length, other._path.length);
        for (int i = 0; i < dim; i++) {
            int v0 = this._path[i];
            int v1 = other._path[i];
            sum += Math.pow(v1 - v0, 2);
        }

        return Math.sqrt(sum);
    }

    /**
     * @param  other a non-null instance of {@linkplain NodePath}
     * @return       true if this node path is part of the argument node path, false
     *               otherwise
     */
    public boolean isPartOf(NodePath other) {
        assert other != null;

        if (this._path.length < other._path.length) {
            // This node path can't be part of it other one because it is shorter
            return false;
        }

        // This node path is part of the other one if all the indexes are matching
        for (int i = 0; i < other._path.length; i++) {
            if (this._path[i] != other._path[i]) {
                // One mismatch found, this node path is not part of the other one
                return false;
            }
        }

        // If we get here, that means this node path is part of the other one
        return true;
    }

    @Override
    public int hashCode() {
        int sum = _path.length;
        for (int i = 0; i < _path.length; i++) {
            sum += _path[i];
        }
        return sum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof NodePath)) {
            // Not equal: The other object is not a NodePath
            return false;
        }

        NodePath other = (NodePath) obj;
        if (this._path.length != other._path.length) {
            // Not equal: Not the same number of indexes
            return false;
        }

        for (int i = 0; i < this._path.length; i++) {
            if (this._path[i] != other._path[i]) {
                // Not equal: One of the indexes not matching
                return false;
            }
        }

        // If we get here, that means the other object has the same path as this object
        return true;
    }

    /**
     * Convert to the string representation of the node path, e.g.
     * <tt>"1.2.1.3.0"</tt>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _path.length; i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(_path[i]);
        }

        return sb.toString();
    }
}
