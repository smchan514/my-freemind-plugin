package smchan.freemind_my_plugin.timeline;

class DefaultTimeScaleSource implements ITimeScaleSource {

	private final ObservableSupport<ITimeScaleChangedListener> _obs = new ObservableSupport<>(
			ITimeScaleChangedListener.class);

	private ITimeScale _timeScale;

	public DefaultTimeScaleSource() {
        _timeScale = new DefaultTimeScale();
	}

	@Override
	public ITimeScale getTimeScale() {
		return _timeScale;
	}

	@Override
	public void setTimeScale(ITimeScale timeScale) {
		_timeScale = timeScale;
		_obs.notifyListeners(timeScale);
	}

	@Override
	public void addTimeScaleChangedListener(ITimeScaleChangedListener listener) {
		_obs.addListener(listener);
	}

	@Override
	public void removeTimeScaleChangedListener(ITimeScaleChangedListener listener) {
		_obs.removeListener(listener);
	}

}
