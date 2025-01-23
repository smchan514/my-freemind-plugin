package smchan.freemind_my_plugin.adv_search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import accessories.plugins.time.JSpinField;

/**
 * Advanced search user parameter input dialog
 */
class AdvancedSearchDialog extends JDialog {

    public enum SearchScope {
        SearchEntireMindMap,
        SearchSelectedNodes,
        SearchAllOpenMaps
    }

    public enum SearchOrientation {
        DepthFirst,
        BreadthFirst
    }

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    private static String _lastSearchTerm = "";
    private static boolean _lastCaseSensitive = false;
    private static boolean _lastRegexSearch = false;
    private static int _lastMaxResults = 50;
    private static SearchScope _lastSearchScope = SearchScope.SearchEntireMindMap;
    private static SearchOrientation _lastSearchOrientation = SearchOrientation.DepthFirst;

    private JButton _jbOK;
    private JTextField _jtfSearchTerm;
    private JCheckBox _jcbRegexSearch;
    private JCheckBox _jcbCaseSensitive;
    private JRadioButton _jrbSearchEntireMap;
    private JRadioButton _jrbSearchSelectedNodes;
    private JRadioButton _jrbSearchAllOpenMaps;
    private JRadioButton _jrbDepthFirst;
    private JRadioButton _jrbBreadthFirst;
    private JSpinField _jsfMaxResults;

    private boolean _confirmed;

    public AdvancedSearchDialog(Frame owner) {
        super(owner, true);

        initComponents();
    }

    private void initComponents() {
        setTitle("Advanced Search (v1)");
        getContentPane().add(createTopPanel(), BorderLayout.NORTH);
        getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new CancelAction(), keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(_jbOK);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private Component createButtonsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = DEFAULT_INSETS;
        comp = new JButton(new CancelAction());
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = DEFAULT_INSETS;
        comp = _jbOK = new JButton(new OkAction());
        panel.add(comp, gbc);

        return panel;
    }

    private Component createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        GridBagConstraints gbc;
        JLabel jlabel;
        JTextField jtf;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = jlabel = new JLabel("Search:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jtfSearchTerm = jtf = new JTextField(_lastSearchTerm);
        jtf.selectAll();
        jtf.addFocusListener(new MyTextfieldFocusListener(jtf));
        jlabel.setDisplayedMnemonic('S');
        jlabel.setLabelFor(jtf);
        panel.add(comp, gbc);

        return panel;
    }

    private Component createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        comp = createMainOptionsPanel();
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        comp = createPanelSearchScope();
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        comp = createPanelSearchOrientation();
        panel.add(comp, gbc);

        ///////////////////////
        // Filler
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        comp = new JLabel();
        panel.add(comp, gbc);

        return panel;
    }

    private JComponent createMainOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        JCheckBox jcb;
        JSpinField jsf;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = jcb = _jcbCaseSensitive = new JCheckBox("Case sensitive", _lastCaseSensitive);
        jcb.setMnemonic('C');
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = jcb = _jcbRegexSearch = new JCheckBox("Regular expression", _lastRegexSearch);
        jcb.setMnemonic('R');
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Max results:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = jsf = _jsfMaxResults = new JSpinField(10, 1000);
        jsf.setValue(_lastMaxResults);
        panel.add(comp, gbc);

        return panel;
    }

    private JComponent createPanelSearchOrientation() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Orentiation:"));
        ButtonGroup buttonGroup;
        JComponent comp;
        JRadioButton jrb;
        GridBagConstraints gbc;

        buttonGroup = new ButtonGroup();

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbDepthFirst = jrb = new JRadioButton("Depth first",
                _lastSearchOrientation == SearchOrientation.DepthFirst);
        buttonGroup.add(jrb);
        jrb.setMnemonic('D');
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbBreadthFirst = jrb = new JRadioButton("Breadth first",
                _lastSearchOrientation == SearchOrientation.BreadthFirst);
        buttonGroup.add(jrb);
        jrb.setMnemonic('B');
        panel.add(comp, gbc);

        return panel;
    }

    private JComponent createPanelSearchScope() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Scope:"));
        ButtonGroup buttonGroup;
        JComponent comp;
        JRadioButton jrb;
        GridBagConstraints gbc;

        buttonGroup = new ButtonGroup();

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbSearchEntireMap = jrb = new JRadioButton("Entire mind map",
                _lastSearchScope == SearchScope.SearchEntireMindMap);
        buttonGroup.add(jrb);
        jrb.setMnemonic('E');
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbSearchSelectedNodes = jrb = new JRadioButton("From selected nodes",
                _lastSearchScope == SearchScope.SearchSelectedNodes);
        buttonGroup.add(jrb);
        jrb.setMnemonic('F');
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbSearchAllOpenMaps = jrb = new JRadioButton("All open maps",
                _lastSearchScope == SearchScope.SearchAllOpenMaps);
        buttonGroup.add(jrb);
        jrb.setMnemonic('A');
        panel.add(comp, gbc);

        return panel;
    }

    /**
     * @return true if the dialog was closed after the user clicked on OK, false
     *         otherwise
     */
    public boolean showDialog(Component parent) {
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        return _confirmed;
    }

    public String getSearchTerm() {
        return _lastSearchTerm = _jtfSearchTerm.getText();
    }

    public boolean isCaseSensitive() {
        return _lastCaseSensitive = _jcbCaseSensitive.isSelected();
    }

    public boolean isRegexSearch() {
        return _lastRegexSearch = _jcbRegexSearch.isSelected();
    }

    public int getMaxResults() {
        return _lastMaxResults = _jsfMaxResults.getValue();
    }

    public SearchScope getSearchScope() {
        if (_jrbSearchEntireMap.isSelected()) {
            _lastSearchScope = SearchScope.SearchEntireMindMap;
        } else if (_jrbSearchSelectedNodes.isSelected()) {
            _lastSearchScope = SearchScope.SearchSelectedNodes;
        } else if (_jrbSearchAllOpenMaps.isSelected()) {
            _lastSearchScope = SearchScope.SearchAllOpenMaps;
        } else {
            // Default search entire map
            _lastSearchScope = SearchScope.SearchEntireMindMap;
        }

        return _lastSearchScope;
    }

    public SearchOrientation getSearchOrientation() {
        if (_jrbDepthFirst.isSelected()) {
            _lastSearchOrientation = SearchOrientation.DepthFirst;
        } else if (_jrbBreadthFirst.isSelected()) {
            _lastSearchOrientation = SearchOrientation.BreadthFirst;
        } else {
            // Default
            _lastSearchOrientation = SearchOrientation.DepthFirst;
        }

        return _lastSearchOrientation;
    }

    void doOkAction() {
        try {
            validateSearchTerm();
            setVisible(false);
            _confirmed = true;
        } catch (Exception e) {
            String title = "Validation failed";
            JOptionPane.showMessageDialog(this, e.getMessage(), title, JOptionPane.ERROR_MESSAGE);
        }
    }

    void doCancelAction() {
        setVisible(false);
        _confirmed = false;
    }

    private void validateSearchTerm() {
        String text = _jtfSearchTerm.getText();
        if (text == null || text.trim().length() == 0) {
            throw new RuntimeException("Search term cannot be empty");
        }
    }

    //////////////////////
    private class OkAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public OkAction() {
            super("OK");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doOkAction();
        }

    }

    //////////////////////
    private class CancelAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public CancelAction() {
            super("Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCancelAction();
        }

    }

    //////////////////////
    private static class MyTextfieldFocusListener extends FocusAdapter {
        private final JTextField _jtf;

        public MyTextfieldFocusListener(JTextField jtf) {
            assert jtf != null;
            _jtf = jtf;
        }

        @Override
        public void focusGained(FocusEvent e) {
            _jtf.selectAll();
        }
    }
}
