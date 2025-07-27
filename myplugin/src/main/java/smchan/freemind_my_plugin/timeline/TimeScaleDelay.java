package smchan.freemind_my_plugin.timeline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * Debounce event notifications from an {@link ITimeScaleSource} by adding some
 * delay
 */
class TimeScaleDelay implements ITimeScaleSource, ITimeScaleChangedListener, ActionListener {
    private static final int DEFAULT_DELAY_MILLIS = 500;
    private final ObservableSupport<ITimeScaleChangedListener> _obs = new ObservableSupport<>(
            ITimeScaleChangedListener.class);
    private final Timer _timer;

    private ITimeScaleSource _source;
    private ITimeScale _timerScale;

    public TimeScaleDelay(ITimeScaleSource source) {
        _timer = new Timer(DEFAULT_DELAY_MILLIS, this);
        _timer.setRepeats(false);

        _timerScale = new DefaultTimeScale();

        _source = source;
        _source.addTimeScaleChangedListener(this);
    }

    @Override
    public void updateTimeScale(ITimeScale timeScale) {
        _timerScale = timeScale;
        _timer.restart();
    }

    @Override
    public ITimeScale getTimeScale() {
        return _source.getTimeScale();
    }

    @Override
    public void setTimeScale(ITimeScale timeScale) {
        _source.setTimeScale(timeScale);
    }

    @Override
    public void addTimeScaleChangedListener(ITimeScaleChangedListener listener) {
        _obs.addListener(listener);
    }

    @Override
    public void removeTimeScaleChangedListener(ITimeScaleChangedListener listener) {
        _obs.removeListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        _obs.notifyListeners(_timerScale);
    }

}
