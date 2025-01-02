package smchan.freemind_my_plugin.adv_search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * Stand-alone window showing search results
 */
class AdvancedSearchResultsFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private JTextField _jtfSearchTerm;
    private JLabel _jlFinalCount;
    private JLabel _jlTotalCount;
    private SearchResultsListModel _listModel;
    private JList<SearchResult> _jlist;

    public AdvancedSearchResultsFrame() {
        initComponents();
    }

    public void showSearchResults(Component owner, String searchTerm, Set<SearchResult> results, int totalCount) {
        if (!isVisible()) {
            pack();

            // Align the frame to the lower right corner of the owner frame
            Point pt = new Point();
            owner.getLocation(pt);
            pt.x += owner.getWidth() - this.getWidth();
            pt.y += owner.getHeight() - this.getHeight();
            this.setLocation(pt);

            setVisible(true);
        } else {
            requestFocus();
        }

        // Change list content
        _listModel.setResults(results);

        // Change total count
        _jlTotalCount.setText(Integer.toString(totalCount));

        // Show initial search parameters
        _jtfSearchTerm.setText(searchTerm);
    }

    public void clearResults() {
        _listModel.clear();
    }

    private void initComponents() {
        setTitle("Advanced Search Results (v1)");
        setIconImage(new ImageIcon(this.getClass().getResource("/images/FreeMindWindowIcon.png")).getImage());
        getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new CloseAction(), keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new MyWindowListener());
    }

    private Component createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        JTextField jtf;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Search term:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        comp = jtf = _jtfSearchTerm = new JTextField();
        jtf.setEditable(false);
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Results:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        gbc.fill = GridBagConstraints.BOTH;
        comp = _jlFinalCount = new JLabel();
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("/");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        gbc.fill = GridBagConstraints.BOTH;
        comp = _jlTotalCount = new JLabel();
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JButton(new PreviousAction());
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        comp = new JButton(new NextAction());
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        _listModel = new SearchResultsListModel();
        _listModel.addListDataListener(new MyListDataListener());
        _jlist = new JList<>(_listModel);
        _jlist.addMouseListener(new MyMouseListener());
        _jlist.setCellRenderer(new SearchResultListCellRenderer());
        JScrollPane jsp;
        comp = jsp = new JScrollPane(_jlist);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // Set the preferred size on the JScrollPane
        jsp.setPreferredSize(new Dimension(640, 240));
        panel.add(comp, gbc);

        return panel;
    }

    private Component createButtonsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = DEFAULT_INSETS;
        comp = new JButton(new CloseAction());
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = DEFAULT_INSETS;
        comp = new JButton(new ClearAction());
        panel.add(comp, gbc);

        return panel;
    }

    void doClearAction() {
        clearResults();
    }

    void doCloseAction() {
        setVisible(false);
        dispose();
    }

    void doUpdateResultCount() {
        _jlFinalCount.setText(Integer.toString(_listModel.getSize()));
    }

    void doGotoSeachResult(SearchResult searchResult) {
        MindMapNode node = searchResult.getNode();
        MindMapController mmc = (MindMapController) node.getMap().getModeController();
        // Bring map to the top, in case it's hidden by another map
        mmc.showThisMap();
        mmc.centerNode(node);
    }

    void doPreviousAction() {
        int size = _listModel.getSize();
        if (size == 0) {
            return;
        }

        if (_jlist.isSelectionEmpty()) {
            _jlist.setSelectedIndex(size - 1);
        } else {
            _jlist.setSelectedIndex((_jlist.getSelectedIndex() - 1 + size) % size);
        }

        SearchResult searchResult = _jlist.getSelectedValue();
        doGotoSeachResult(searchResult);
    }

    void doNextAction() {
        int size = _listModel.getSize();
        if (size == 0) {
            return;
        }

        if (_jlist.isSelectionEmpty()) {
            _jlist.setSelectedIndex(0);
        } else {
            _jlist.setSelectedIndex((_jlist.getSelectedIndex() + 1) % size);
        }

        SearchResult searchResult = _jlist.getSelectedValue();
        doGotoSeachResult(searchResult);
    }

    //////////////////////
    private class MyListDataListener implements ListDataListener {
        public MyListDataListener() {
            // ...
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            doUpdateResultCount();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            doUpdateResultCount();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            doUpdateResultCount();
        }

    }

    //////////////////////
    private class MyMouseListener extends MouseAdapter {
        public MyMouseListener() {
            // ...
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                SearchResult searchResult = _jlist.getSelectedValue();
                doGotoSeachResult(searchResult);
            }
        }
    }

    //////////////////////
    private class MyWindowListener extends WindowAdapter {
        public MyWindowListener() {
            // ...
        }

        @Override
        public void windowClosing(WindowEvent e) {
            clearResults();
        }
    }

    //////////////////////
    private class PreviousAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public PreviousAction() {
            super("Previous");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doPreviousAction();
        }

    }

    //////////////////////
    private class NextAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public NextAction() {
            super("Next");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doNextAction();
        }

    }

    //////////////////////
    private class ClearAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public ClearAction() {
            super("Clear");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doClearAction();
        }

    }

    //////////////////////
    private class CloseAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public CloseAction() {
            super("Close");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCloseAction();
        }

    }

}
