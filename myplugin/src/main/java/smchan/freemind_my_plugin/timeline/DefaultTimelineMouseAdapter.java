package smchan.freemind_my_plugin.timeline;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JComponent;

/**
 * Handle mouse events for timeline pan and zoom
 */
class DefaultTimelineMouseAdapter extends MouseAdapter {
    private final ITimeScaleSource _timeScaleSource;

    private int _lastX;

    public DefaultTimelineMouseAdapter(JComponent owner, ITimeScaleSource timeScaleSource) {
        _timeScaleSource = timeScaleSource;

        owner.addMouseListener(this);
        owner.addMouseMotionListener(this);
        owner.addMouseWheelListener(this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent evt) {
        // Check modifier bit(s) required to activate event handling
        boolean modCtrl = (evt.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK)) != 0;
        boolean modShift = (evt.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) != 0;

        if (!modCtrl) {
            // Zoom when CTRL not held down
            int x = evt.getX();
            double factor;

            if (modShift) {
                // 5% zoom when SHIFT held down
                factor = 1.05;
            } else {
                // 50% zoom when SHIFT held down
                factor = 1.5;
            }

            if (evt.getWheelRotation() > 0) {
                // factor = factor;
            } else {
                factor = 1 / factor;
            }

            ITimeScale ts = _timeScaleSource.getTimeScale();
            _timeScaleSource.setTimeScale(ts.zoom(x, factor));
        } else {
            // Pan when CTRL held down
            ITimeScale ts = _timeScaleSource.getTimeScale();
            int pixels;

            if (modShift) {
                // 5% of the original time scale when SHIFT held down
                pixels = ts.getWidthPixels() / 20;
            } else {
                // 25% of the original time scale when SHIFT held down
                pixels = ts.getWidthPixels() / 4;
            }

            if (evt.getWheelRotation() > 0) {
                // pixels = 1;
            } else {
                pixels *= -1;
            }

            _timeScaleSource.setTimeScale(ts.translate(pixels));
        }


        JComponent source = (JComponent) evt.getSource();
        source.repaint();
        evt.consume();
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        _lastX = evt.getX();
        evt.consume();
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        JComponent source = (JComponent) evt.getSource();
        int x = evt.getX();

        ITimeScale ts = _timeScaleSource.getTimeScale();
        _timeScaleSource.setTimeScale(ts.translate(_lastX - x));

        source.repaint();
        evt.consume();

        _lastX = x;
    }

}
