package smchan.freemind_my_plugin.adv_search;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.text.View;

import smchan.freemind_my_plugin.util.CompositeIcon;

class SearchResultListCellRenderer implements ListCellRenderer<SearchResult> {
    private Color _altBackground;
    private final JLabel _label = new JLabel();
    private final CompositeIcon _compositeIcon = new CompositeIcon();

    public SearchResultListCellRenderer() {
        _label.setOpaque(true);
        _label.setHorizontalAlignment(JLabel.LEFT);
        _label.setVerticalAlignment(JLabel.TOP);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends SearchResult> list, SearchResult value, int index,
            boolean isSelected, boolean cellHasFocus) {

        // Use the pre-rendered HTML as-is
        _label.setText(value.getHtml());

        // Set JLabel width by setting the "View" javax.swing.plaf.basic.BasicHTML$Renderer
        // which can be found through the JLabel's "client property" 
        // This trick is found by tracing jlabel.getPreferredSize()...
        View v = (View) _label.getClientProperty("html");
        if (v != null) {
            int width = list.getWidth();
            // Shave two pixels off the JList's width to avoid horizontal scroll bar
            width -= 2;
            v.setSize(width, 0);
        }

        // Set icons
        _compositeIcon.setIcons(value.getNode().getIcons());
        _compositeIcon.addIcons(value.getNode().getStateIcons().values());
        _label.setIcon(_compositeIcon);

        // Set colors...
        if (isSelected) {
            // Item selected: use the selection colors
            _label.setForeground(list.getSelectionForeground());
            _label.setBackground(list.getSelectionBackground());
        } else {
            // Item not selected: use the "normal" colors with alternating background
            _label.setForeground(list.getForeground());
            if ((index & 1) != 0) {
                _label.setBackground(list.getBackground());
            } else {
                // Lazy init...
                if (_altBackground == null) {
                    Color color = list.getBackground();
                    _altBackground = new Color(color.getRGB() ^ 0x80808);
                }
                _label.setBackground(_altBackground);
            }
        }

        return _label;
    }

}
