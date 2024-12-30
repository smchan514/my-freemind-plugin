package smchan.freemind_my_plugin.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import freemind.modes.MindIcon;

public class CompositeIcon implements Icon {
    private final LinkedList<ImageIcon> _lstIcons = new LinkedList<>();

    public CompositeIcon() {
        // ...
    }

    public void addImageIcon(ImageIcon icon) {
        _lstIcons.add(icon);
    }

    public void addIcons(Collection<?> icons) {
        for (Object object : icons) {
            if (object instanceof MindIcon) {
                _lstIcons.add(((MindIcon) object).getIcon());
            }
        }
    }

    public void setIcons(Collection<?> icons) {
        _lstIcons.clear();
        addIcons(icons);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        for (ImageIcon icon : _lstIcons) {
            Image img = icon.getImage();
            g.drawImage(img, x, y, null);

            x += icon.getIconWidth();
        }
    }

    @Override
    public int getIconWidth() {
        int value = 0;

        for (ImageIcon icon : _lstIcons) {
            value += icon.getIconWidth();
        }

        return value;
    }

    @Override
    public int getIconHeight() {
        int value = 0;

        for (ImageIcon icon : _lstIcons) {
            value = Math.max(value, icon.getIconHeight());
        }

        return value;
    }

}
