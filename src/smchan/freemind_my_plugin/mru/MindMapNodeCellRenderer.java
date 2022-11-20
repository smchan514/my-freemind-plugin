package smchan.freemind_my_plugin.mru;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import freemind.main.Tools;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;

public class MindMapNodeCellRenderer extends DefaultListCellRenderer
{
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(MindMapNodeCellRenderer.class.getName());

    private static final long serialVersionUID = 1L;

    private static final int MAX_WIDTH = 640;
    private static final int MAX_HEIGHT = 50;

    private static final Color BGCOLOR = new Color(0, 0, 0, 32);

    private ImageIcon _iconLink;
    private ImageIcon _iconLinkLocal;
    private ImageIcon _iconMail;
    private ImageIcon _iconExec;

    private CompositeIcon _compIcon = new CompositeIcon();

    public MindMapNodeCellRenderer()
    {
        _iconLink = getImageIcon("/images/Link.png");
        _iconLinkLocal = getImageIcon("/images/LinkLocal.png");
        _iconMail = getImageIcon("/images/Mail.png");
        _iconExec = getImageIcon("/images/Executable.png");
    }

    private ImageIcon getImageIcon(String resName)
    {
        URL url = getClass().getResource(resName);
        if (url == null)
        {
            LOGGER.warning("Failed to find resource: " + resName);
            return new ImageIcon();
        }

        return new ImageIcon(url);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (comp instanceof JLabel && value instanceof MindMapNode)
        {
            MindMapNode node = (MindMapNode) value;
            JLabel label = (JLabel) comp;

            // Set text and font
            label.setText(node.getText());
            if (node.getFont() != null)
                label.setFont(node.getFont());

            // Set icons
            _compIcon.setIcons(node.getIcons());
            label.setIcon(_compIcon);

            String link = node.getLink();
            if (link != null)
            {
                ImageIcon icon = _iconLink;
                if (link.startsWith("#"))
                {
                    icon = _iconLinkLocal;
                }
                else if (link.startsWith("mailto:"))
                {
                    icon = _iconMail;
                }
                else if (Tools.executableByExtension(link))
                {
                    icon = _iconExec;
                }
                _compIcon.addImageIcon(icon);
            }

            // Check if the label exceeds certain dimensions
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
        int imageWidth = Math.min(prefSize.width, MAX_WIDTH);
        int imageHeight = Math.min(prefSize.height, MAX_HEIGHT);
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = image.createGraphics();

        // Paint the label into the image
        label.setSize(prefSize);
        label.paint(g);

        // Paint a background to indicate this is a rendered image
        g.setColor(BGCOLOR);
        for (int x = 0; x < imageWidth + imageHeight; x += 12)
            g.drawLine(x, 0, x - imageHeight, imageHeight);

        g.dispose();

        return image;
    }

    //////////////////
    private class CompositeIcon implements Icon
    {
        private final LinkedList<ImageIcon> _lstIcons = new LinkedList<>();

        public CompositeIcon()
        {
            // ...
        }

        public void addImageIcon(ImageIcon icon)
        {
            _lstIcons.add(icon);
        }

        public void setIcons(List<?> icons)
        {
            _lstIcons.clear();

            for (Object object : icons)
            {
                if (object instanceof MindIcon)
                {
                    _lstIcons.add(((MindIcon) object).getIcon());
                }
            }
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            for (ImageIcon icon : _lstIcons)
            {
                Image img = icon.getImage();
                g.drawImage(img, x, y, null);

                x += icon.getIconWidth();
            }
        }

        @Override
        public int getIconWidth()
        {
            int value = 0;

            for (ImageIcon icon : _lstIcons)
            {
                value += icon.getIconWidth();
            }

            return value;
        }

        @Override
        public int getIconHeight()
        {
            int value = 0;

            for (ImageIcon icon : _lstIcons)
            {
                value = Math.max(value, icon.getIconHeight());
            }

            return value;
        }

    }
}
