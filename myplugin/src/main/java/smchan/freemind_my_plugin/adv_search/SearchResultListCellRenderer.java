package smchan.freemind_my_plugin.adv_search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

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

        // Set icons
        _compositeIcon.setIcons(value.getNode().getIcons());
        _label.setIcon(_compositeIcon);

        // Set JLabel width to not exceed the parent JList width using the HTML "View"
        // The goal is to avoid the horizontal scroll bar in JList's parent JScrollPane
        View v = (View) _label.getClientProperty("html");
        if (v != null) {
            // Ask the JList for its current width
            int width = list.getWidth();
            v.setSize(width, 0);
            // Ask the View for its preferred spans based on the width set
            int vpw = (int) v.getPreferredSpan(View.X_AXIS);
            int vph = (int) v.getPreferredSpan(View.Y_AXIS);
            // Use the View's preferred spans as the JLabel's preferred size
            _label.setPreferredSize(new Dimension(vpw, vph));
        }

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
