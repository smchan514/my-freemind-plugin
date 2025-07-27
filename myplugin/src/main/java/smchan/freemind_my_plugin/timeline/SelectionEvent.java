package smchan.freemind_my_plugin.timeline;

/**
 * A timeline selection event targeting {@link SelectionEventRenderer}.
 */
class SelectionEvent extends DefaultEvent {

    public SelectionEvent(long startTime, long endTime) {
        super(startTime, endTime);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
