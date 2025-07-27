package smchan.freemind_my_plugin.timeline;

import java.awt.Color;

/**
 * Generic interface of a colormap generator
 */
interface IColormapGenerator {
    /**
     * @return a non-null array of {@link Color} instances, the number of colors is
     *         determined by the implementation of the generator
     */
    Color[] generateColormap();
}
