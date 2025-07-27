package smchan.freemind_my_plugin.timeline;

/**
 * Default immutable implementation of {@link ITimelineEvent}.
 */
class DefaultEvent implements ITimelineEvent
{
    private boolean _isPointInTime;
    private long _startTime;
    private long _endTime;

    public DefaultEvent(long startTime, long endTime, boolean isPointInTime)
    {
		_isPointInTime = isPointInTime;
		_startTime = startTime;
		_endTime = endTime;
    }

    public DefaultEvent(long eventTime) {
        this(eventTime, eventTime, true);
	}

    public DefaultEvent(long startTime, long endTime)
    {
        this(startTime, endTime, false);
    }

    @Override
    public boolean isPointInTime()
    {
        return _isPointInTime;
    }

    @Override
    public long getEventTime()
    {
        return _startTime;
    }

    @Override
    public long getStartTime()
    {
        return _startTime;
    }

    @Override
    public long getEndTime()
    {
        return _endTime;
    }

}
