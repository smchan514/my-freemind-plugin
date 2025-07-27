package smchan.freemind_my_plugin.timeline;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * List cell renderer for {@link NodeEvent} instances
 */
class NodeEventListCellRenderer extends JComponent
        implements ListCellRenderer<Object>, ITimeScaleChangedListener {

    private static final long serialVersionUID = 1L;
    private final ITimeScaleSource _timeScaleSource;
    private ITimeScale _timeScale;

    private String _text;
    private long _startTime;
    private Color _bgColor;
    private Color _fgColor;

    public NodeEventListCellRenderer(ITimeScaleSource timeScaleSource) {
        _timeScaleSource = timeScaleSource;
        _timeScaleSource.addTimeScaleChangedListener(this);
        _timeScale = timeScaleSource.getTimeScale();

        setPreferredSize(new Dimension(320, 20));
    }

    @Override
    public void updateTimeScale(ITimeScale timeScale) {
        _timeScale = timeScale;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        if (isSelected) {
            _bgColor = list.getSelectionBackground();
            _fgColor = list.getSelectionForeground();
        } else {
            _bgColor = list.getBackground();
            _fgColor = list.getForeground();
        }

        if (value instanceof NodeEvent) {
            NodeEvent event = (NodeEvent) value;
            _text = event.getText();
            _startTime = event.getStartTime();
        } else if (value instanceof ITimelineEvent) {
            ITimelineEvent event = (ITimelineEvent) value;
            _text = event.toString();
            _startTime = event.getStartTime();
        }

        return this;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Dimension size = getSize();

        g2d.setColor(_bgColor);
        g2d.fillRect(0, 0, size.width, size.height);

        if (_text != null) {
            int sx = _timeScale.timeToPixels(_startTime);

            FontMetrics fm = g2d.getFontMetrics();
            int h = fm.getHeight();

            g2d.setColor(_fgColor);
            g2d.drawString(_text, sx, h);
        }
    }

}
