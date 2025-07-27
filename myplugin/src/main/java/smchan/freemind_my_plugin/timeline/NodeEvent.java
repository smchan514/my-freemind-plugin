package smchan.freemind_my_plugin.timeline;

/**
 * A timeline event augmented by info representing a mind map "node"
 */
class NodeEvent extends DefaultEvent {

    private final String _text;
    private final String _path;
    private final String _nodeId;

    public NodeEvent(String text, long eventTime, String path, String nodeId) {
        super(eventTime);

        _text = text;
        _path = path;
        _nodeId = nodeId;
    }

    public String getText() {
        return _text;
    }

    public String getPath() {
        return _path;
    }

    public String getNodeId() {
        return _nodeId;
    }

    @Override
    public String toString() {
        return _text;
    }

}
