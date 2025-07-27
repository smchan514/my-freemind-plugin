package smchan.freemind_my_plugin.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * {@link ITimelineEventRenderer} for {@link IntValueEvent}
 */
class IntValueEventRenderer implements ITimelineEventRenderer {

    private final Color[] _colormap;

    public IntValueEventRenderer(IColormapGenerator colormapGenerator) {
        _colormap = colormapGenerator.generateColormap();
    }

    @Override
    public void renderEvent(Graphics2D g2d, Dimension size, ITimeScale timeScale, ITimelineEvent event) {
        int x0, width;

        if (event.isPointInTime()) {
            x0 = timeScale.timeToPixels(event.getEventTime());
            width = 1;
        } else {
            x0 = timeScale.timeToPixels(event.getStartTime());
            int x1 = timeScale.timeToPixels(event.getEndTime());
            width = x1 - x0;
            if (width == 0) {
                width = 1;
            }
        }

        int value = ((IntValueEvent) event).getValue();
        value = (int) (Math.log10(value) * 50);
        if (value >= _colormap.length) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(_colormap[value]);
        }

        g2d.fillRect(x0, 0, width, size.height);
    }

}
