package smchan.freemind_my_plugin.timeline;

import java.awt.Color;

/**
 * Simple implementation of an {@link IColormapGenerator} with linear
 * interpolation of RGB components at predefined "milestones".
 */
class ColormapGeneratorInterpolated implements IColormapGenerator {
    private static final int NBR_COLORS = 256;
    private static final int[][] MILESTONES = { { 0, 0, 255, 0 }, { 64, 255, 255, 0 }, { 64 + 128, 255, 0, 0 },
            { NBR_COLORS - 1, 143, 13, 163 }, { NBR_COLORS, 143, 13, 163 } };

    public ColormapGeneratorInterpolated() {
        // ...
    }

    @Override
    public Color[] generateColormap() {
        Color[] colormap = new Color[NBR_COLORS];

        // Milestones in the color map: {step_index, R, G, B}
        int[] milestone0 = MILESTONES[0];
        int[] milestone1 = MILESTONES[1];
        int nextMilestone = 2;
        int stride = milestone1[0] - milestone0[0];

        for (int i = 0; i < colormap.length; i++) {
            int increment = i - milestone0[0];

            // Interpolate between milestones
            int r = milestone0[1] + (milestone1[1] - milestone0[1]) * increment / stride;
            int g = milestone0[2] + (milestone1[2] - milestone0[2]) * increment / stride;
            int b = milestone0[3] + (milestone1[3] - milestone0[3]) * increment / stride;
            colormap[i] = new Color(r, g, b);

            // Move to the next milestone
            if (milestone1[0] == i) {
                milestone0 = milestone1;
                milestone1 = MILESTONES[nextMilestone];
                stride = milestone1[0] - milestone0[0];
                ++nextMilestone;
            }
        }

        return colormap;
    }

}
