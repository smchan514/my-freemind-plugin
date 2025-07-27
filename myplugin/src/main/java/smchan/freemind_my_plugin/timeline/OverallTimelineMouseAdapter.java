package smchan.freemind_my_plugin.timeline;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class OverallTimelineMouseAdapter extends DefaultTimelineMouseAdapter {

    private final ITimeScaleSource _tsOverall;
    private final ITimeScaleSource _tsDetails;

    private boolean _isDrawing;
    private int _startX;
    private int _lastX;

    public OverallTimelineMouseAdapter(JComponent owner, ITimeScaleSource tsOverall, ITimeScaleSource tsDetails) {
        super(owner, tsOverall);

        _tsOverall = tsOverall;
        _tsDetails = tsDetails;
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        boolean modShift = (evt.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK)) != 0;

        if (modShift) {
            // Remember starting X position
            _lastX = _startX = evt.getX();

            // Flag for subsequent mouse events
            _isDrawing = true;
            evt.consume();
        } else {
            super.mousePressed(evt);
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        if (_isDrawing) {
            // Undo the XOR rectangle at the last X position
            drawXORRect(evt, _startX, _lastX);

            // Draw the XOR rectangle at he new X position
            _lastX = evt.getX();
            drawXORRect(evt, _startX, _lastX);

            evt.consume();
        } else {
            super.mouseDragged(evt);
        }
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if (_isDrawing) {
            // Compute the selected time range
            ITimeScale tsOverall = _tsOverall.getTimeScale();
            long startTime = tsOverall.pixelsToTime(_startX);

            int x = evt.getX();
            long endTime = tsOverall.pixelsToTime(x);

            // Set the new time scale on the "details timeline"
            ITimeScale tsDetails = _tsDetails.getTimeScale();
            _tsDetails.setTimeScale(tsDetails.reset(startTime, endTime));
            _isDrawing = false;

            // Clean up the UI potentially messed up by XOR rectangles
            JComponent source = (JComponent) evt.getSource();
            source.repaint();
            evt.consume();
        } else {
            super.mouseReleased(evt);
        }
    }

    private void drawXORRect(MouseEvent evt, int x0, int x1) {
        JComponent source = (JComponent) evt.getSource();
        int height = source.getHeight();
        Graphics2D g2d = (Graphics2D) source.getGraphics();
        g2d.setXORMode(Color.WHITE);

        if (x0 > x1) {
            int t = x0;
            x0 = x1;
            x1 = t;
        }

        g2d.drawRect(x0, 0, x1 - x0, height - 1);
    }

}
