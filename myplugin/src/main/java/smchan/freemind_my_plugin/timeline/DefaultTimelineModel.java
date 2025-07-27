package smchan.freemind_my_plugin.timeline;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Default implementation of {@link ITimelineModel}.
 */
class DefaultTimelineModel<E extends ITimelineEvent> implements ITimelineModel<E> {
    private final List<E> _lstEvents;
    private final ObservableSupport<ChangeListener> _obsSupport = new ObservableSupport<>(ChangeListener.class);

    public DefaultTimelineModel() {
        _lstEvents = new LinkedList<>();
    }

    public DefaultTimelineModel(List<E> lstEvents) {
        assert lstEvents != null;

        _lstEvents = lstEvents;
    }

    @Override
    public void addEvent(E event) {
        _lstEvents.add(event);
        _obsSupport.notifyListeners(new ChangeEvent(this));
    }

    @Override
    public void addEvents(Collection<E> events) {
        _lstEvents.addAll(events);
        _obsSupport.notifyListeners(new ChangeEvent(this));
    }

    @Override
    public void removeEvent(ITimelineEvent event) {
        if (_lstEvents.remove(event)) {
            _obsSupport.notifyListeners(new ChangeEvent(this));
        }
    }

    @Override
    public void clearEvents() {
        _lstEvents.clear();
        _obsSupport.notifyListeners(new ChangeEvent(this));
    }

    @Override
    public Collection<E> getEvents() {
        return _lstEvents;
    }

    @Override
    public Iterator<E> iterator() {
        return _lstEvents.iterator();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        _obsSupport.addListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        _obsSupport.removeListener(listener);
    }

}
