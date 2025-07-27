package smchan.freemind_my_plugin.timeline;

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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Configuration dialog for timeline data ingest
 */
class DialogDataIngest extends JDialog {

    public enum IngestScope {
        CurrentMindMap,
        AllOpenMaps,
        None
    }

    public enum TimestampSource {
        LastModified,
        CreatedAt,
    }

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private static final Insets CHECKBOX_INSETS = new Insets(1, 5, 1, 5);

    private static final String DEFAULT_JDBC_CNX_STRING = "jdbc:h2:~/.freemind/timeline";

    private static final char MNEMONIC_ALL_OPEN_MAPS = 'A';
    private static final char MNEMONIC_CREATED = 'C';
    private static final char MNEMONIC_CURRENT_MIND_MAP = 'E';
    private static final char MNEMONIC_LAST_MODIFIED = 'L';
    private static final char MNEMONIC_NONE = 'N';
    private static final char MNEMONIC_JDBC_CNX_STRING = 'S';

    private String _lastJdbcConnectionString = DEFAULT_JDBC_CNX_STRING;
    private IngestScope _lastIngestScope = IngestScope.CurrentMindMap;
    private TimestampSource _lastTimestampSource = TimestampSource.LastModified;

    private JButton _jbOK;
    private JTextField _jtfJdbcCnxString;
    private JRadioButton _jrbIngestCurrentMap;
    private JRadioButton _jrbIngestAllOpenMaps;
    private JRadioButton _jrbIngestNone;
    private JRadioButton _jrbTimestampSourceLastModified;
    private JRadioButton _jrbTimestampSourceCreated;

    private boolean _confirmed;

    public DialogDataIngest(Frame owner) {
        super(owner, true);

        initComponents();
    }

    /**
     * @return true if the dialog was closed after the user clicked on OK, false
     *         otherwise
     */
    public boolean showDialog(Component parent) {
        _confirmed = false;

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);

        return _confirmed;
    }

    public String getJdbcConnectionString() {
        return _lastJdbcConnectionString = _jtfJdbcCnxString.getText();
    }

    public IngestScope getIngestScope() {
        if (_jrbIngestCurrentMap.isSelected()) {
            _lastIngestScope = IngestScope.CurrentMindMap;
        } else if (_jrbIngestAllOpenMaps.isSelected()) {
            _lastIngestScope = IngestScope.AllOpenMaps;
        } else if (_jrbIngestNone.isSelected()) {
            _lastIngestScope = IngestScope.None;
        } else {
            // Default
            _lastIngestScope = IngestScope.CurrentMindMap;
        }

        return _lastIngestScope;
    }

    public TimestampSource getTimestampSource() {
        if (_jrbTimestampSourceLastModified.isSelected()) {
            _lastTimestampSource = TimestampSource.LastModified;
        } else if (_jrbTimestampSourceCreated.isSelected()) {
            _lastTimestampSource = TimestampSource.CreatedAt;
        } else {
            // Default
            _lastTimestampSource = TimestampSource.LastModified;
        }

        return _lastTimestampSource;
    }

    private void initComponents() {
        setTitle("Timeline (v1)");
        getContentPane().add(createCenterPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new CancelAction(), keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(_jbOK);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private Component createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Data ingest options:"));
        JComponent comp;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        comp = createPanelJdbcCnxString();
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        comp = createPanelTimestampSource();
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        comp = createPanelIngestScope();
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

    private JComponent createPanelJdbcCnxString() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        GridBagConstraints gbc;
        JLabel jlabel;
        JTextField jtf;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = jlabel = new JLabel("JDBC Connection String:");
        panel.add(comp, gbc);
        
        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jtfJdbcCnxString = jtf = new JTextField(_lastJdbcConnectionString);
        jtf.selectAll();
        jtf.addFocusListener(new MyTextfieldFocusListener(jtf));
        jlabel.setDisplayedMnemonic(MNEMONIC_JDBC_CNX_STRING);
        jlabel.setLabelFor(jtf);
        panel.add(comp, gbc);

        return panel;
    }

    private JComponent createPanelIngestScope() {
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
        comp = _jrbIngestCurrentMap = jrb = new JRadioButton("Current mind map",
                _lastIngestScope == IngestScope.CurrentMindMap);
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
        comp = _jrbIngestAllOpenMaps = jrb = new JRadioButton("All open maps",
                _lastIngestScope == IngestScope.AllOpenMaps);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_ALL_OPEN_MAPS);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbIngestNone = jrb = new JRadioButton("None (use existing data)",
                _lastIngestScope == IngestScope.None);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_NONE);
        panel.add(comp, gbc);

        ///////////////////////
        // Filler
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        comp = new JLabel();
        panel.add(comp, gbc);

        return panel;
    }

    private JComponent createPanelTimestampSource() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Timestamp source:"));
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
        comp = _jrbTimestampSourceLastModified = jrb = new JRadioButton("Last modified time",
                _lastTimestampSource == TimestampSource.LastModified);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_LAST_MODIFIED);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = CHECKBOX_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jrbTimestampSourceCreated = jrb = new JRadioButton("Created time",
                _lastTimestampSource == TimestampSource.CreatedAt);
        buttonGroup.add(jrb);
        jrb.setMnemonic(MNEMONIC_CREATED);
        panel.add(comp, gbc);

        ///////////////////////
        // Filler
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        comp = new JLabel();
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
        comp = new JButton(new CancelAction());
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = DEFAULT_INSETS;
        comp = _jbOK = new JButton(new OkAction());
        panel.add(comp, gbc);

        return panel;
    }

    void doOkAction() {
        try {
            // validateSearchTerm();
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
