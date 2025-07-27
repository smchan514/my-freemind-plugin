package smchan.freemind_my_plugin.timeline;

/**
 * Time scale converting between a time value in milliseconds from the Epoch
 * (1970-01-01 00:00 GMT) and pixel coordinate.
 */
interface ITimeScale {
    /**
     * @return the minimum time, i.e. start time, represented by this time scale
     */
    long getMinTime();

    /**
     * @return the maximum time, i.e. end time, represented by this time scale
     */
    long getMaxTime();

    /**
     * @return the number of pixels represented by this time scale
     */
    int getWidthPixels();

    /**
     * @param  millis time in milliseconds since start of Epoch
     * @return        true if specified time is in the time scale, or false
     *                otherwise
     */
    boolean isInRange(long millis);

    /**
     * @param  millis time in milliseconds since start of Epoch
     * @return        pixel coordinate corresponding to the specified time in this
     *                time scale
     */
    int timeToPixels(long millis);

    /**
     * @param  pixel a pixel coordinate
     * @return       time in milliseconds since start of Epoch corresponding to the
     *               pixel coordinate
     */
    long pixelsToTime(int pixel);

    /**
     * Move the display origin (x=0) to the position specified by the argument
     * number of pixels
     * 
     * @param  pixels number of pixels to move
     * @return        a new instance of {@link ITimeScale}
     */
    ITimeScale translate(int pixels);

    /**
     * Create a new instance of {@link ITimeScale} with the specified time range and
     * number of pixels.
     * 
     * @param  minTime start time in milliseconds
     * @param  maxTime end time in milliseconds
     * @param  pixels  number of pixels for the new time scale
     * @return         a new instance of {@link ITimeScale}
     */
    ITimeScale reset(long minTime, long maxTime, int pixels);

    /**
     * Create a new instance of {@link ITimeScale} with the specified time range.
     * 
     * @param  minTime start time in milliseconds
     * @param  maxTime end time in milliseconds
     * @return         a new instance of {@link ITimeScale}
     */
    ITimeScale reset(long startTime, long endTime);

    /**
     * Create a new instance of {@link ITimeScale} by zooming around the specified
     * position and factor on the current time scale.
     * 
     * @param  centerPixel zoom position in pixel coordinate
     * @param  factor      floating point value greater than 0, > 1.0 to zoom out, <
     *                     1.0 to zoom in
     * @return             a new instance of {@link ITimeScale}
     */
    ITimeScale zoom(int centerPixel, double factor);

    /**
     * Create a new instance of {@link ITimeScale} by resizing current time scale,
     * keeping the same min time and max time.
     * 
     * @param  widthPixels number of pixels for the new time scale
     * @return             a new instance of {@link ITimeScale}
     */
    ITimeScale resize(int widthPixels);

}
