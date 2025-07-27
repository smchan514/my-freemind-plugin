package smchan.freemind_my_plugin.timeline;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Generic interface of a renderer for a timeline event, to be used by
 * {@link JTimeline}.
 */
interface ITimelineEventRenderer
{
	void renderEvent(Graphics2D g2d, Dimension size, ITimeScale timeScale, ITimelineEvent event);
}
