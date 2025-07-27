package smchan.freemind_my_plugin.timeline;

/**
 * Generic interface of a timeline event
 */
interface ITimelineEvent
{
    boolean isPointInTime();

    long getEventTime();

    long getStartTime();

    long getEndTime();
}
