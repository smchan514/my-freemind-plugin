package smchan.freemind_my_plugin.timeline;

import java.util.Collection;

import javax.swing.event.ChangeListener;

/**
 * Generic interface of a timeline data model, to be used by {@link JTimeline}.
 */
interface ITimelineModel<E extends ITimelineEvent> extends Iterable<E> {

    /**
     * Add a listener to receive model change notification.
     * 
     * @param listener a non-null instance of {@link ChangeListener}
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Remove a previously added listener.
     * 
     * @param listener an instance of a previously added listener
     */
    void removeChangeListener(ChangeListener listener);

    /**
     * Get all the instances of {@link ITimelineEvent} in the model.
     * 
     * @return a non-null {@link Collection} of zero or more instances of event
     *         objects
     */
    Collection<E> getEvents();

    /**
     * Clear all events in the model. Listeners will be notified.
     */
    void clearEvents();

    /**
     * Add an event to the model. Listeners will be notified.
     * 
     * @param event a non-null instance of an event object
     */
    void addEvent(E event);

    /**
     * Add an event to the model. Listeners will be notified.
     * 
     * @param events a non-null {@link Collection} with zero or more event objects
     */
    void addEvents(Collection<E> events);

    /**
     * Remove the specified event from the model. Listeners will be notified if the
     * removal was successful.
     * 
     * @param event a non-null instance of an event object
     */
    void removeEvent(E event);

}
