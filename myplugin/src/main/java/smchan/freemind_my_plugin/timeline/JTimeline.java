package smchan.freemind_my_plugin.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Simple Java Swing implementation of a linear timeline.
 */
class JTimeline extends JComponent implements ITimeScaleChangedListener, ChangeListener {
    // Default serial ID
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 20;

    private final HashMap<Class<?>, ITimelineEventRenderer> _mapEventClass2Renderer = new HashMap<>();

    private ITimeScaleSource _timeScaleSource;
    private ITimeScale _timeScale;
    private ITimelineModel<?> _model;

    public JTimeline() {
        _timeScale = new DefaultTimeScale();

        // Set component default time
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        // Register for component size change
        addComponentListener(new MyComponentAdapter());
    }

    public void setTimelineModel(ITimelineModel<?> model) {
        _model = model;
        _model.addChangeListener(this);
    }

    public void setTimelineEventRenderer(Class<?> eventClass, ITimelineEventRenderer renderer) {
        _mapEventClass2Renderer.put(eventClass, renderer);
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
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    public void updateTimeScale(ITimeScale timeScale) {
        Dimension size = getSize();
        _timeScale = timeScale;
        if (size.width > 0) {
            _timeScale = timeScale.resize(size.width);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = getSize();

        // Fill background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, size.width, size.height);

        if (_model == null) {
            return;
        }

        for (ITimelineEvent event : _model) {
            ITimelineEventRenderer renderer = _mapEventClass2Renderer.get(event.getClass());
            if (renderer != null) {
                // Use a custom event render if there is one specified for the event class
                renderer.renderEvent(g2d, size, _timeScale, event);
            } else {
                // Otherwise use the generic rendering
                if (isEventVisible(event)) {
                    renderEvent(g2d, size, event);
                }
            }
        }
    }

    private void renderEvent(Graphics2D g2d, Dimension size, ITimelineEvent event) {
        int x0, width;

        if (event.isPointInTime()) {
            x0 = _timeScale.timeToPixels(event.getEventTime());
            width = 1;
        } else {
            x0 = _timeScale.timeToPixels(event.getStartTime());
            int x1 = _timeScale.timeToPixels(event.getEndTime());
            width = x1 - x0;
            if (width == 0) {
                width = 1;
            }
        }

        g2d.setColor(Color.GREEN);
        g2d.fillRect(x0, 0, width, size.height);
    }

    private boolean isEventVisible(ITimelineEvent event) {
        if (event.isPointInTime()) {
            return _timeScale.isInRange(event.getEventTime());
        }

        // If the interval-type event encompass the current time scale
        return event.getStartTime() <= _timeScale.getMaxTime() && event.getEndTime() >= _timeScale.getMinTime();
    }

    ////////////////////////////////////////////////////////
    private class MyComponentAdapter implements ComponentListener {
        public MyComponentAdapter() {
            // ...
        }

        @Override
        public void componentResized(ComponentEvent evt) {
            updateTimeScale(_timeScale);
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

}
