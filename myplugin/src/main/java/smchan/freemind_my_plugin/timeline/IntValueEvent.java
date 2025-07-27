package smchan.freemind_my_plugin.timeline;

/**
 * A timeline event augmented by an integer value to be used for example to
 * represent the number of nodes between the start start and end time
 */
class IntValueEvent extends DefaultEvent {

    private final int _value;

    public IntValueEvent(int value, long startTime, long endTime) {
        super(startTime, endTime);
        _value = value;
    }

    public int getValue() {
        return _value;
    }

    @Override
    public String toString() {
        return Integer.toString(_value);
    }

}
