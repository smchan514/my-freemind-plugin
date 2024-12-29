package smchan.freemind_my_plugin.adv_search;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import freemind.modes.MindMapNode;

class TreeWalkerDepthFirst implements ITreeWalker {

    private MindMapNode[] _nodes;

    public TreeWalkerDepthFirst(MindMapNode[] nodes) {
        _nodes = nodes;
    }

    @Override
    public Iterator<MindMapNode> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<MindMapNode> {
        private final LinkedList<MindMapNode> _queue = new LinkedList<>();

        public MyIterator() {
            for (MindMapNode node : _nodes) {
                _queue.add(node);
            }
        }

        @Override
        public boolean hasNext() {
            return !_queue.isEmpty();
        }

        @Override
        public MindMapNode next() {
            MindMapNode node = _queue.removeFirst();

            // Add children nodes to the front of the queue
            @SuppressWarnings("unchecked")
            List<MindMapNode> children = node.getChildren();
            _queue.addAll(0, children);

            return node;
        }
    }
}
