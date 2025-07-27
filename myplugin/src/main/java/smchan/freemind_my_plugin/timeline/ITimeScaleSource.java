package smchan.freemind_my_plugin.timeline;

/**
 * Generic interface for a notification source for {@link ITimeScale} objects.
 */
interface ITimeScaleSource {
    /**
     * @return a non-null instance of {@link ITimeScale}
     */
    ITimeScale getTimeScale();

    /**
     * @param timeScale a non-null instance of {@link ITimeScale
     */
    void setTimeScale(ITimeScale timeScale);

    /**
     * Add a listener to receive notification of new {@link ITimeScale}.
     * 
     * @param listener a non-null instance of {@link ITimeScaleChangedListener}
     */
    void addTimeScaleChangedListener(ITimeScaleChangedListener listener);

    /**
     * Remove a previously added listener.
     * 
     * @param listener an instance of a previously added listener
     */
    void removeTimeScaleChangedListener(ITimeScaleChangedListener listener);

}
