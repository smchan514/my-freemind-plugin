package smchan.freemind_my_plugin.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

/**
 * Simple Java Swing implementation of a linear timeline ruler.
 */
class JTimeRuler extends JComponent implements ITimeScaleChangedListener {
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_WIDTH = 320;
	private static final int DEFAULT_HEIGHT = 40;

	private ITimeScaleSource _timeScaleSource;
	private ITimeScale _timeScale;

	public JTimeRuler() {
		// Default scale: from now to 24 hour later
		long minTime = System.currentTimeMillis();
		long maxTime = minTime + 24 * 3600 * 1000;
		int widthPixels = DEFAULT_WIDTH;
		_timeScale = new DefaultTimeScale(minTime, maxTime, widthPixels);

		// Set component default size
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setBorder(BorderFactory.createEtchedBorder());

		// Register for component size change
		addComponentListener(new MyComponentAdapter());
	}

	public void setTimeScaleSource(ITimeScaleSource timeScaleSource) {
		if (_timeScaleSource != null) {
			_timeScaleSource.removeTimeScaleChangedListener(this);
			_timeScaleSource = null;
		}

		if (timeScaleSource != null) {
			_timeScaleSource = timeScaleSource;
			_timeScaleSource.addTimeScaleChangedListener(this);
			updateTimeScale(_timeScaleSource.getTimeScale());
		}
	}

	@Override
	public void updateTimeScale(ITimeScale timeScale) {
		_timeScale = timeScale;

		Dimension size = getSize();
		if (size.width > 0) {
			_timeScale = timeScale.resize(size.width);
		}

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Dimension size = getSize();

		// Clear with background color
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, size.width, size.height);

		ITimeScale ts = _timeScale;
		RendererEntry entry = getRulerEntry(ts);

		if (entry._rMajor != null) {
			entry._rMajor.renderMajor(g2d, size, ts);
		}

		g2d.translate(0, size.height >> 1);

		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawLine(0, 0, size.width, 0);
		entry._rMinor.renderMinor(g2d, size, ts);

		g2d.translate(0, -size.height >> 1);
	}

	private RendererEntry getRulerEntry(ITimeScale ts) {
		long tMin = ts.getMinTime();
		long tMax = ts.getMaxTime();
		float millisPerPixel = (float) (tMax - tMin) / ts.getWidthPixels();

		RendererEntry lastEntry = entries[0];
		for (int i = 0; i < entries.length; i++) {
			if (millisPerPixel > entries[i]._maxMPP) {
				lastEntry = entries[i];
			} else {
				return lastEntry;
			}
		}

		return lastEntry;
	}

	////////////////////////////////////////////////////////
	private class MyComponentAdapter implements ComponentListener {
		public MyComponentAdapter() {
			// ...
		}

		@Override
		public void componentResized(ComponentEvent evt) {
			// Resize the timescale at the source so that transform operations in the UI
			// (translation and zoom) are at the same "scale"
			// Here we assume all the components share the same width
			Dimension size = getSize();
			if (size.width > 0) {
				_timeScaleSource.setTimeScale(_timeScale.resize(size.width));
			}
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			// ...
		}

		@Override
		public void componentShown(ComponentEvent e) {
			// ...
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			// ...
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	private final IRulerRenderer RR_MILLIS = new RRMillis(1);
	private final IRulerRenderer RR_2MILLIS = new RRMillis(2);
	private final IRulerRenderer RR_5MILLIS = new RRMillis(5);
	private final IRulerRenderer RR_10MILLIS = new RRMillis(10);
	private final IRulerRenderer RR_25MILLIS = new RRMillis(25);
	private final IRulerRenderer RR_50MILLIS = new RRMillis(50);
	private final IRulerRenderer RR_100MILLIS = new RRMillis(100);
	private final IRulerRenderer RR_250MILLIS = new RRMillis(250);
	private final IRulerRenderer RR_500MILLIS = new RRMillis(500);
	private final IRulerRenderer RR_SEC = new RRSecond(1);
	private final IRulerRenderer RR_2SEC = new RRSecond(2);
	private final IRulerRenderer RR_5SEC = new RRSecond(5);
	private final IRulerRenderer RR_10SEC = new RRSecond(10);
	private final IRulerRenderer RR_30SEC = new RRSecond(30);
	private final IRulerRenderer RR_MIN = new RRMinute(1);
	private final IRulerRenderer RR_2MIN = new RRMinute(2);
	private final IRulerRenderer RR_5MIN = new RRMinute(5);
	private final IRulerRenderer RR_10MIN = new RRMinute(10);
	private final IRulerRenderer RR_30MIN = new RRMinute(30);
	private final IRulerRenderer RR_HOUR = new RRHour(1);
	private final IRulerRenderer RR_3HOUR = new RRHour(3);
	private final IRulerRenderer RR_6HOUR = new RRHour(6);
	private final IRulerRenderer RR_12HOUR = new RRHour(12);
	private final IRulerRenderer RR_DAY = new RRDay(1);
	private final IRulerRenderer RR_2DAY = new RRDay(2);
	private final IRulerRenderer RR_5DAY = new RRDay(5);
	private final IRulerRenderer RR_10DAY = new RRDay(10);
	private final IRulerRenderer RR_MONTH = new RRMonth(1);
	private final IRulerRenderer RR_3MONTH = new RRMonth(3);
	private final IRulerRenderer RR_6MONTH = new RRMonth(6);
	private final IRulerRenderer RR_YEAR = new RRYear(1);
	private final IRulerRenderer RR_2YEAR = new RRYear(2);
	private final IRulerRenderer RR_5YEAR = new RRYear(5);
	private final IRulerRenderer RR_10YEAR = new RRYear(10);
	private final IRulerRenderer RR_20YEAR = new RRYear(20);
	private final IRulerRenderer RR_50YEAR = new RRYear(50);
	private final IRulerRenderer RR_100YEAR = new RRYear(100);
	private final IRulerRenderer RR_200YEAR = new RRYear(200);
	private final IRulerRenderer RR_500YEAR = new RRYear(500);
	private final IRulerRenderer RR_1000YEAR = new RRYear(1000);
	private final IRulerRenderer RR_2000YEAR = new RRYear(2000);
	private final IRulerRenderer RR_5000YEAR = new RRYear(5000);

	private RendererEntry entries[] = { new RendererEntry(RR_MILLIS, RR_SEC, 0f),
			new RendererEntry(RR_2MILLIS, RR_SEC, .02f), new RendererEntry(RR_5MILLIS, RR_SEC, .05f),
			new RendererEntry(RR_10MILLIS, RR_SEC, .1f), new RendererEntry(RR_25MILLIS, RR_SEC, .25f),
			new RendererEntry(RR_50MILLIS, RR_SEC, .5f), new RendererEntry(RR_100MILLIS, RR_SEC, 1),
			new RendererEntry(RR_250MILLIS, RR_SEC, 2.5f), new RendererEntry(RR_500MILLIS, RR_5SEC, 5),
			new RendererEntry(RR_SEC, RR_MIN, 10), new RendererEntry(RR_2SEC, RR_MIN, 20),
			new RendererEntry(RR_5SEC, RR_MIN, 50), new RendererEntry(RR_10SEC, RR_MIN, 100),
			new RendererEntry(RR_30SEC, RR_MIN, 300), new RendererEntry(RR_MIN, RR_HOUR, 600),
			new RendererEntry(RR_2MIN, RR_HOUR, 1200), new RendererEntry(RR_5MIN, RR_HOUR, 3000),
			new RendererEntry(RR_10MIN, RR_HOUR, 6000), new RendererEntry(RR_30MIN, RR_HOUR, 18000),
			new RendererEntry(RR_HOUR, RR_DAY, 36000), new RendererEntry(RR_3HOUR, RR_DAY, 108000),
			new RendererEntry(RR_6HOUR, RR_DAY, 216000), new RendererEntry(RR_12HOUR, RR_DAY, 432000),
			new RendererEntry(RR_DAY, RR_MONTH, 864000), new RendererEntry(RR_2DAY, RR_MONTH, 1728000),
			new RendererEntry(RR_5DAY, RR_MONTH, 4320000), new RendererEntry(RR_10DAY, RR_MONTH, 8640000),
			new RendererEntry(RR_MONTH, RR_YEAR, 25920000f), new RendererEntry(RR_3MONTH, RR_YEAR, 77760000f),
			new RendererEntry(RR_6MONTH, RR_YEAR, 155520000f), new RendererEntry(RR_YEAR, null, 311040000f),
			new RendererEntry(RR_2YEAR, null, 622080000f), new RendererEntry(RR_5YEAR, null, 1555200000f),
			new RendererEntry(RR_10YEAR, null, 3110400000f), new RendererEntry(RR_20YEAR, null, 6220800000f),
			new RendererEntry(RR_50YEAR, null, 15552000000f), new RendererEntry(RR_100YEAR, null, 31104000000f),
			new RendererEntry(RR_200YEAR, null, 62208000000f), new RendererEntry(RR_500YEAR, null, 155520000000f),
			new RendererEntry(RR_1000YEAR, null, 311040000000f), new RendererEntry(RR_2000YEAR, null, 622080000000f),
			new RendererEntry(RR_5000YEAR, null, 1555200000000f), };

	private class RendererEntry {
		public IRulerRenderer _rMajor;
		public IRulerRenderer _rMinor;
		public float _maxMPP;

		public RendererEntry(IRulerRenderer rMinor, IRulerRenderer rMajor, float maxMillisPerPixel) {
			_rMinor = rMinor;
			_rMajor = rMajor;
			_maxMPP = maxMillisPerPixel;
		}
	}

	private interface IRulerRenderer {
		void renderMajor(Graphics2D g2d, Dimension size, ITimeScale ts);

		void renderMinor(Graphics2D g2d, Dimension size, ITimeScale ts);
	}

	private abstract class RulerRenderer implements IRulerRenderer {
		protected final Calendar _cal;
		protected final int _multiple;
		protected final int _targetField;
		protected final SimpleDateFormat _sdfMajor;
		protected final SimpleDateFormat _sdfMinor;

		public RulerRenderer(int multiple, int targetField, String dfMajor, String dfMinor) {
			_cal = Calendar.getInstance();
			_targetField = targetField;
			_sdfMajor = new SimpleDateFormat(dfMajor);
			_sdfMinor = new SimpleDateFormat(dfMinor);

			if (multiple > 1) {
				_multiple = multiple;
			} else {
				_multiple = 1;
			}
		}

		@Override
		public void renderMajor(Graphics2D g2d, Dimension size, ITimeScale ts) {
			long tMin = ts.getMinTime();
			long tMax = ts.getMaxTime();
			long tNext;
			int xNext;

			_cal.setTimeInMillis(tMin);
			tNext = snapToLeft();
			xNext = ts.timeToPixels(tNext);

			g2d.setColor(Color.BLACK);
			do {
				long tTick = tNext;
				int xTick = xNext;

				// Draw the tick
				g2d.drawLine(xTick, 0, xTick, size.height);

				// Draw the text label
				FontMetrics fm = g2d.getFontMetrics();
				String str = getMajorTickLabel(tTick);

				// Move on to the next tick before drawing the label
				tNext = moveToNextTick();
				xNext = ts.timeToPixels(tNext);

				if (xTick < 0) {
					int strWidth = fm.stringWidth(str);
					if (strWidth >= xNext) {
						xTick = xNext - strWidth - 2;
					} else {
						xTick = 0;
					}
				}

				g2d.drawString(str, xTick + 2, fm.getLeading() + fm.getAscent());
			} while (tNext < tMax);
		}

		@Override
		public void renderMinor(Graphics2D g2d, Dimension size, ITimeScale ts) {
			long tMin = ts.getMinTime();
			long tMax = ts.getMaxTime();
			long tTick;

			_cal.setTimeInMillis(tMin);
			tTick = snapToLeft();

			g2d.setColor(Color.BLACK);
			do {
				int xTick = ts.timeToPixels(tTick);

				// Draw the tick
				g2d.drawLine(xTick, 0, xTick, size.height);

				// Draw the text label
				FontMetrics fm = g2d.getFontMetrics();
				String str = getMinorTickLabel(tTick);

				g2d.drawString(str, xTick + 2, fm.getLeading() + fm.getAscent());

				// Move on to the next tick
				tTick = moveToNextTick();
			} while (tTick < tMax);
		}

		protected long snapToLeft() {
			// No more snapping required
			if (_multiple > 1) {
				int value = _cal.get(_targetField);
				value -= (value % _multiple);
				_cal.set(_targetField, value);
			}

			return _cal.getTimeInMillis();
		}

		protected long moveToNextTick() {
			_cal.add(_targetField, _multiple);
			return _cal.getTimeInMillis();
		}

		protected String getMajorTickLabel(long timeInMillis) {
			return _sdfMajor.format(timeInMillis);
		}

		protected String getMinorTickLabel(long timeInMillis) {
			return _sdfMinor.format(timeInMillis);
		}
	}

	private class RRYear extends RulerRenderer {
		public RRYear(int multiple) {
			super(multiple, Calendar.YEAR, "yyyy", "yyyy");
		}

		@Override
		protected long snapToLeft() {
			_cal.set(Calendar.MONTH, 0);
			_cal.set(Calendar.DAY_OF_MONTH, 1);
			_cal.set(Calendar.HOUR_OF_DAY, 0);
			_cal.set(Calendar.MINUTE, 0);
			_cal.set(Calendar.SECOND, 0);
			_cal.set(Calendar.MILLISECOND, 0);

			return super.snapToLeft();
		}
	}

	private class RRMonth extends RulerRenderer {
		public RRMonth(int multiple) {
			super(multiple, Calendar.MONTH, "MMM yyyy", "MMM");
		}

		@Override
		protected long snapToLeft() {
			_cal.set(Calendar.DAY_OF_MONTH, 1);
			_cal.set(Calendar.HOUR_OF_DAY, 0);
			_cal.set(Calendar.MINUTE, 0);
			_cal.set(Calendar.SECOND, 0);
			_cal.set(Calendar.MILLISECOND, 0);

			return super.snapToLeft();
		}
	}

	private class RRDay extends RulerRenderer {
		public RRDay(int multiple) {
			super(multiple, Calendar.DAY_OF_MONTH, "EEE yyyy-MM-dd", "dd");
		}

		@Override
		protected long snapToLeft() {
			_cal.set(Calendar.HOUR_OF_DAY, 0);
			_cal.set(Calendar.MINUTE, 0);
			_cal.set(Calendar.SECOND, 0);
			_cal.set(Calendar.MILLISECOND, 0);

			return super.snapToLeft();
		}
	}

	private class RRHour extends RulerRenderer {
		public RRHour(int multiple) {
			super(multiple, Calendar.HOUR_OF_DAY, "yyyy-MM-dd HH'h'", "HH'h'");
		}

		@Override
		protected long snapToLeft() {
			_cal.set(Calendar.MINUTE, 0);
			_cal.set(Calendar.SECOND, 0);
			_cal.set(Calendar.MILLISECOND, 0);

			return super.snapToLeft();
		}
	}

	private class RRMinute extends RulerRenderer {
		public RRMinute(int multiple) {
			super(multiple, Calendar.MINUTE, "yyyy-MM-dd HH:mm", "HH:mm");
		}

		@Override
		protected long snapToLeft() {
			_cal.set(Calendar.SECOND, 0);
			_cal.set(Calendar.MILLISECOND, 0);

			return super.snapToLeft();
		}
	}

	private class RRSecond extends RulerRenderer {
		public RRSecond(int multiple) {
			super(multiple, Calendar.SECOND, "yyyy-MM-dd HH:mm:ss", "ss's'");
		}

		@Override
		protected long snapToLeft() {
			_cal.set(Calendar.MILLISECOND, 0);

			return super.snapToLeft();
		}
	}

	private class RRMillis extends RulerRenderer {
		public RRMillis(int multiple) {
			super(multiple, Calendar.MILLISECOND, "yyyy-MM-dd HH:mm:ss.SSS", ".SSS");
		}
	}
}
