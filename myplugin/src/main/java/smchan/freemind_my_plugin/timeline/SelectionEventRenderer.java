package smchan.freemind_my_plugin.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;

/**
 * {@link ITimelineEventRenderer} for {@link IntValueEvent}
 */
class SelectionEventRenderer implements ITimelineEventRenderer {
    private Color _color;

    public SelectionEventRenderer() {
        // ...
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

        if (x0 < size.width) {
            Area area = new Area(new Rectangle(0, 0, size.width, size.height));
            area.subtract(new Area(new Rectangle(x0, 0, width, size.height)));
            g2d.setClip(area);
        }

        if (_color == null) {
            _color = new Color(80, 80, 80, 128);
        }
        g2d.setColor(_color);
        g2d.fillRect(0, 0, size.width, size.height);

        g2d.setClip(null);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x0, 0, width, size.height - 1);
    }

}
