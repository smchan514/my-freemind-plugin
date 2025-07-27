package smchan.freemind_my_plugin.timeline;

/**
 * Default implementation of {@link ITimeScale} for linear timeline.
 */
class DefaultTimeScale implements ITimeScale {
    // 100 years in milliseconds
    private static final long MAX_TIME_LAPSE_MILLIS = 100L * 365 * 24 * 3600 * 1000L;
    private static final int DEFAULT_WIDTH = 320;

    private final long _minTime;
    private final long _maxTime;
    private final long _deltaTime;
    private final int _widthPixels;

    public DefaultTimeScale() {
        // Default scale: from now to 24 hour later
        this(System.currentTimeMillis(), System.currentTimeMillis() + 24 * 3600 * 1000, DEFAULT_WIDTH);
    }

    public DefaultTimeScale(long minTime, long maxTime, int widthPixels) {
        assert minTime >= 0;
        assert minTime < maxTime;
        assert (maxTime - minTime) < MAX_TIME_LAPSE_MILLIS;
        assert widthPixels > 0;

        _minTime = minTime;
        _maxTime = maxTime;
        _deltaTime = _maxTime - _minTime;
        _widthPixels = widthPixels;
    }

    @Override
    public long getMinTime() {
        return _minTime;
    }

    @Override
    public long getMaxTime() {
        return _maxTime;
    }

    @Override
    public int getWidthPixels() {
        return _widthPixels;
    }

    @Override
    public boolean isInRange(long time) {
        return time >= _minTime && time <= _maxTime;
    }

    @Override
    public int timeToPixels(long time) {
        long d0 = time - _minTime;
        double proportion = (double) d0 / (double) _deltaTime;

        return (int) (proportion * _widthPixels);
    }

    @Override
    public long pixelsToTime(int pixel) {
        double proportion = (double) pixel / (double) _widthPixels;
        return _minTime + (long) (proportion * _deltaTime);
    }

    @Override
    public ITimeScale translate(int pixel) {
        // Compute the new origin in time
        long newMinTime = pixelsToTime(pixel);

        // Limit to start of epoch
        if (newMinTime < 0) {
            newMinTime = 0;
        }

        return new DefaultTimeScale(newMinTime, newMinTime + _deltaTime, _widthPixels);
    }

    @Override
    public ITimeScale zoom(int centerPixel, double factor) {
        long centerTime = pixelsToTime(centerPixel);
        long newDeltaTime = (long) (_deltaTime * factor);

        // Prevent zoom-in below sub-millisecond scale
        newDeltaTime = Math.max(newDeltaTime, _widthPixels);

        double proportion = (double) centerPixel / (double) _widthPixels;
        long newMinTime = centerTime - (long) (proportion * newDeltaTime);

        // Limit to start of epoch
        if (newMinTime < 0) {
            newMinTime = 0;
        }

        long newMaxTime = newMinTime + newDeltaTime;

        return new DefaultTimeScale(newMinTime, newMaxTime, _widthPixels);
    }

    @Override
    public ITimeScale reset(long minTime, long maxTime, int pixels) {
        return new DefaultTimeScale(minTime, maxTime, pixels);
    }

    @Override
    public ITimeScale reset(long minTime, long maxTime) {
        return new DefaultTimeScale(minTime, maxTime, _widthPixels);
    }

    @Override
    public ITimeScale resize(int widthPixel) {
        return new DefaultTimeScale(_minTime, _maxTime, widthPixel);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DefaultTimeScale [_minTime=");
        builder.append(_minTime);
        builder.append(", _maxTime=");
        builder.append(_maxTime);
        builder.append(", _deltaTime=");
        builder.append(_deltaTime);
        builder.append(", _widthPixels=");
        builder.append(_widthPixels);
        builder.append("]");
        return builder.toString();
    }

}
