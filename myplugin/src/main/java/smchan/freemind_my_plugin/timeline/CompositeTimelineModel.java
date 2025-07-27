package smchan.freemind_my_plugin.timeline;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Composite {@link ITimelineModel} backed by two models.
 */
class CompositeTimelineModel<E extends ITimelineEvent> implements ITimelineModel<E>, ChangeListener {
    private final ITimelineModel<E> _a;
    private final ITimelineModel<E> _b;
    private final ObservableSupport<ChangeListener> _obsSupport = new ObservableSupport<>(ChangeListener.class);

    public CompositeTimelineModel(ITimelineModel<E> a, ITimelineModel<E> b) {
        assert a != null;
        assert b != null;

        _a = a;
        _b = b;

        // Register for notifications
        _a.addChangeListener(this);
        _b.addChangeListener(this);
    }

    @Override
    public void addEvent(E event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addEvents(Collection<E> events) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeEvent(ITimelineEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearEvents() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<E> getEvents() {
        LinkedList<E> list = new LinkedList<>();
        list.addAll(_a.getEvents());
        list.addAll(_b.getEvents());
        return list;
    }

    @Override
    public Iterator<E> iterator() {
        return new ChainedIterator<>(_a.iterator(), _b.iterator());
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        _obsSupport.addListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        _obsSupport.removeListener(listener);
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        _obsSupport.notifyListeners(evt);
    }

    ////////////////////////////////////
    private static class ChainedIterator<E> implements Iterator<E> {
        private final LinkedList<Iterator<E>> _list = new LinkedList<Iterator<E>>();
        private Iterator<E> _curr;

        public ChainedIterator(Iterator<E> a, Iterator<E> b) {
            _curr = a;
            _list.add(b);
        }

        @Override
        public boolean hasNext() {
            // Check if current iterator has next...
            while (!_curr.hasNext()) {
                if (_list.isEmpty()) {
                    return false;
                }

                // Current iterator ran dry... move on to the next iterator in the list
                _curr = _list.removeFirst();
            }

            return true;
        }

        @Override
        public E next() {
            // Check if current iterator has next...
            while (!_curr.hasNext()) {
                if (_list.isEmpty()) {
                    throw new NoSuchElementException();
                }

                // Current iterator ran dry... move on to the next iterator in the list
                _curr = _list.removeFirst();
            }

            return _curr.next();
        }

    }

}
