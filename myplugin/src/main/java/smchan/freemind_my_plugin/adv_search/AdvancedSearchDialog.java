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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import accessories.plugins.time.JSpinField;

/**
 * Advanced search user parameter input dialog
 */
class AdvancedSearchDialog extends JDialog {

    public enum SearchScope {
        SearchCurrentMindMap,
        SearchSelectedNodes,
        SearchAllOpenMaps
    }

    public enum SearchScoring {
        Relevance,
        OldestFirst,
        NewestFirst
    }

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private static final Insets CHECKBOX_INSETS = new Insets(1, 5, 1, 5);

    private static final char MNEMONIC_ALL_OPEN_MAPS = 'A';
    private static final char MNEMONIC_CASE_SENSITIVE = 'C';
    private static final char MNEMONIC_CURRENT_MIND_MAP = 'E';
    private static final char MNEMONIC_FROM_SELECTED_NODES = 'F';
    private static final char MNEMONIC_SEARCH_IN_LINKS = 'K';
    private static final char MNEMONIC_NEWEST_FIRST = 'N';
    private static final char MNEMONIC_OLDEST_FIRST = 'O';
    private static final char MNEMONIC_REGULAR_EXPRESSION = 'R';
    private static final char MNEMONIC_SEARCH_TERM = 'S';
    private static final char MNEMONIC_RELEVANCE = 'V';
    private static final char MNEMONIC_EXACT_MATCH = 'X';

    private static String _lastSearchTerm = "";
    private static boolean _lastCaseSensitive = false;
    private static boolean _lastRegexSearch = false;
    private static boolean _lastExactMatch = false;
    private static boolean _lastSearchInLinks = false;
    private static int _lastMaxResults = 50;
    private static SearchScope _lastSearchScope = SearchScope.SearchCurrentMindMap;
    private static SearchScoring _lastSearchScoring = SearchScoring.Relevance;

    private JButton _jbOK;
    private JTextField _jtfSearchTerm;
    private JCheckBox _jcbRegexSearch;
    private JCheckBox _jcbCaseSensitive;
    private JCheckBox _jcbExactMatch;
    private JCheckBox _jcbSearchInLinks;
    private JRadioButton _jrbSearchEntireMap;
    private JRadioButton _jrbSearchSelectedNodes;
    private JRadioButton _jrbSearchAllOpenMaps;
    private JRadioButton _jrbRelevance;
    private JRadioButton _jrbNewestFirst;
    private JRadioButton _jrbOldestFirst;
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
        jlabel.setDisplayedMnemonic(MNEMONIC_SEARCH_TERM);
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
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = jcb = _jcbCaseSensitive = new JCheckBox("Case sensitive", _lastCaseSensitive);
        jcb.setMnemonic(MNEMONIC_CASE_SENSITIVE);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = jcb = _jcbRegexSearch = new JCheckBox("Regular expression", _lastRegexSearch);
        jcb.setMnemonic(MNEMONIC_REGULAR_EXPRESSION);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = jcb = _jcbExactMatch = new JCheckBox("Exact match", _lastExactMatch);
        jcb.setMnemonic(MNEMONIC_EXACT_MATCH);
        panel.add(comp, gbc);

        // "Exact match" applicable only when not doing regex search
        _jcbRegexSearch.addChangeListener(new EnabledStateSwticher(_jcbExactMatch));

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = jcb = _jcbSearchInLinks = new JCheckBox("Search in links", _lastSearchInLinks);
        jcb.setMnemonic(MNEMONIC_SEARCH_IN_LINKS);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
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
        panel.setBorder(BorderFactory.createTitledBorder("Scoring:"));
        ButtonGroup buttonGroup;
        JComponent comp;
        JRadioButton jrb;
        GridBagConstraints gbc;

        buttonGroup = new ButtonGroup();

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbRelevance = jrb = new JRadioButton("Relevance", _lastSearchScoring == SearchScoring.Relevance);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_RELEVANCE);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbOldestFirst = jrb = new JRadioButton("Oldest first",
                _lastSearchScoring == SearchScoring.OldestFirst);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_OLDEST_FIRST);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbNewestFirst = jrb = new JRadioButton("Newest first",
                _lastSearchScoring == SearchScoring.OldestFirst);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_NEWEST_FIRST);
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
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbSearchEntireMap = jrb = new JRadioButton("Current mind map",
                _lastSearchScope == SearchScope.SearchCurrentMindMap);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_CURRENT_MIND_MAP);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbSearchSelectedNodes = jrb = new JRadioButton("From selected nodes",
                _lastSearchScope == SearchScope.SearchSelectedNodes);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_FROM_SELECTED_NODES);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbSearchAllOpenMaps = jrb = new JRadioButton("All open maps",
                _lastSearchScope == SearchScope.SearchAllOpenMaps);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_ALL_OPEN_MAPS);
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

    public boolean isExactMatch() {
        return _lastExactMatch = _jcbExactMatch.isSelected();
    }

    public boolean isSearchInLinks() {
        return _lastSearchInLinks = _jcbSearchInLinks.isSelected();
    }

    public int getMaxResults() {
        return _lastMaxResults = _jsfMaxResults.getValue();
    }

    public SearchScope getSearchScope() {
        if (_jrbSearchEntireMap.isSelected()) {
            _lastSearchScope = SearchScope.SearchCurrentMindMap;
        } else if (_jrbSearchSelectedNodes.isSelected()) {
            _lastSearchScope = SearchScope.SearchSelectedNodes;
        } else if (_jrbSearchAllOpenMaps.isSelected()) {
            _lastSearchScope = SearchScope.SearchAllOpenMaps;
        } else {
            // Default search entire map
            _lastSearchScope = SearchScope.SearchCurrentMindMap;
        }

        return _lastSearchScope;
    }

    public SearchScoring getSearchScoring() {
        if (_jrbNewestFirst.isSelected()) {
            _lastSearchScoring = SearchScoring.NewestFirst;
        } else if (_jrbOldestFirst.isSelected()) {
            _lastSearchScoring = SearchScoring.OldestFirst;
        } else if (_jrbRelevance.isSelected()) {
            _lastSearchScoring = SearchScoring.Relevance;
        } else {
            // Default
            _lastSearchScoring = SearchScoring.Relevance;
        }

        return _lastSearchScoring;
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

    //////////////////////
    private static class EnabledStateSwticher implements ChangeListener {
        private final JComponent _other;

        public EnabledStateSwticher(JComponent other) {
            assert other != null;
            _other = other;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JCheckBox source = (JCheckBox) e.getSource();
            // Disable the other component if source is selected
            _other.setEnabled(!source.isSelected());
        }

    }
}
