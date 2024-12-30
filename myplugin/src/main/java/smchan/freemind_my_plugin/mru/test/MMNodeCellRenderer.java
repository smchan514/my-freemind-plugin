package smchan.freemind_my_plugin.mru.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class MMNodeCellRenderer extends DefaultListCellRenderer
{
    private static final long serialVersionUID = 1L;

    private static final int MAX_HEIGHT = 50;

    public MMNodeCellRenderer()
    {
        // ...
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (comp instanceof JLabel && value instanceof MMNode)
        {
            MMNode node = (MMNode) value;
            JLabel label = (JLabel) comp;
            label.setText(node.getText());

            Dimension prefSize = label.getPreferredSize();
            if (prefSize.getHeight() > MAX_HEIGHT)
            {
                BufferedImage image = renderLabel(label, prefSize);
                label.setText(null);
                label.setIcon(new ImageIcon(image));
            }
        }
        
        return comp;
    }

    private BufferedImage renderLabel(JLabel label, Dimension prefSize)
    {
        System.out.println("renderLabel");
        int imageWidth = prefSize.width;
        int imageHeight = Math.min(prefSize.height, MAX_HEIGHT);
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();

        // Paint a background to indicate this is a rendered image
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < imageWidth + imageHeight; x += 10)
            g.drawLine(x, 0, x - imageHeight, imageHeight);

        // Paint the label into the image
        label.setSize(prefSize);
        label.paint(g);

        g.dispose();

        return image;
    }

}
