package smchan.freemind_my_plugin.timeline;

/**
 * Generic interface for a listener to {@link ITimeScaleSource}.
 */
interface ITimeScaleChangedListener
{
    void updateTimeScale(ITimeScale timeScale);
}
