package smchan.freemind_my_plugin.adv_search;

import freemind.modes.MindMapNode;
import smchan.freemind_my_plugin.NodePath;
import smchan.freemind_my_plugin.NodePathUtil;

/**
 * Data object representing one search result
 * 
 * This class implements the {@linkplain Comparable} interface so that instances
 * can be added in the descending order of "match score" in a sorted set, e.g.
 * {@linkplain java.util.TreeSet}.
 */
class SearchResult implements Comparable<SearchResult> {

    private final MindMapNode _node;
    private final NodePath _nodePath;
    private final float _score;
    private String _html;

    public SearchResult(MindMapNode node, float score) {
        assert node != null;

        _node = node;
        _nodePath = NodePathUtil.getNodePath(_node);
        _score = score;
    }

    public MindMapNode getNode() {
        return _node;
    }

    public float getScore() {
        return _score;
    }

    public NodePath getNodePath() {
        return _nodePath;
    }

    /**
     * @return pre-rendered HTML document of this {@linkplain SearchResult}, if set,
     *         or <tt>null</tt> otherwise
     */
    public String getHtml() {
        return _html;
    }

    /**
     * Set the pre-rendered HTML document for this search result
     * 
     * @param html string containing a HTML document
     */
    public void setHtml(String html) {
        _html = html;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SearchResult)) {
            return false;
        }

        SearchResult other = (SearchResult) obj;
        // Equal: same MindMapNode
        return this._node == other._node;
    }

    @Override
    public int hashCode() {
        // Use the hash from MindMapNode
        return _node.hashCode();
    }

    @Override
    public int compareTo(SearchResult o) {
        if (o != null) {
            if (this._node == o._node) {
                // Equal: same MindMapNode
                return 0;
            }

            // This object is BEFORE 'o'
            if (this._score > o._score) {
                return -1;
            }
        }

        // This object is AFTER 'o'
        return 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%.1f] ", _score));
        sb.append(_node.getPlainTextContent());
        sb.append(" ");
        sb.append(_nodePath);
        return sb.toString();
    }

}
