package smchan.freemind_my_plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class InsertRandomStringConfigDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    private static final int NUMBER_TOTAL_MIN = 8;
    private static final int NUMBER_TOTAL_MAX = 256;
    private static final int NUMBER_LETTERS_MIN = 8;
    private static final int NUMBER_LETTERS_MAX = 128;
    private static final int NUMBER_DIGITS_MIN = 0;
    private static final int NUMBER_DIGITS_MAX = 128;
    private static final int NUMBER_PUNCTS_MIN = 0;
    private static final int NUMBER_PUNCTS_MAX = 128;

    private final int _defaultMaxTotal;
    private final int _defaultMinLetters;
    private final int _defaultMinDigits;
    private final int _defaultMinPuncts;

    private JSpinner _jspMaxTotal;
    private JSpinner _jspMinLetters;
    private JSpinner _jspMinDigits;
    private JSpinner _jspMinPuncts;
    private JButton _jbOK;

    private boolean _confirmed;

    public InsertRandomStringConfigDialog(int defaultMaxTotal, int defaultMinLetters, int defaultMinDigits,
            int defaultMinPuncts) {

        _defaultMaxTotal = defaultMaxTotal;
        _defaultMinLetters = defaultMinLetters;
        _defaultMinDigits = defaultMinDigits;
        _defaultMinPuncts = defaultMinPuncts;

        initComponents();
    }

    private void initComponents() {
        setTitle("Generate Random String");
        getContentPane().add(createMainPanel());
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        String actionMapKey = "RandomStringConfigDialog:ESCAPE";
        getRootPane().getInputMap().put(keyStroke, actionMapKey);
        getRootPane().getActionMap().put(actionMapKey, new CancelAction());
        getRootPane().setDefaultButton(_jbOK);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
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

    private Component createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JComponent comp;
        SpinnerModel spinnerModel;
        GridBagConstraints gbc;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Max number of characters:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        spinnerModel = new SpinnerNumberModel(_defaultMaxTotal, NUMBER_TOTAL_MIN, NUMBER_TOTAL_MAX, 1);
        comp = _jspMaxTotal = new JSpinner(spinnerModel);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Min number of Letters:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        spinnerModel = new SpinnerNumberModel(_defaultMinLetters, NUMBER_LETTERS_MIN, NUMBER_LETTERS_MAX, 1);
        comp = _jspMinLetters = new JSpinner(spinnerModel);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Min number of Digits:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        spinnerModel = new SpinnerNumberModel(_defaultMinDigits, NUMBER_DIGITS_MIN, NUMBER_DIGITS_MAX, 1);
        comp = _jspMinDigits = new JSpinner(spinnerModel);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Min number of Punctuations:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        spinnerModel = new SpinnerNumberModel(_defaultMinPuncts, NUMBER_PUNCTS_MIN, NUMBER_PUNCTS_MAX, 1);
        comp = _jspMinPuncts = new JSpinner(spinnerModel);
        panel.add(comp, gbc);

        return panel;
    }

    /**
     * @return true if the dialog was closed after the user clicked on OK, false
     *         otherwise
     */
    public boolean showDialog() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        return _confirmed;
    }

    public int getMaxTotal() {
        return (int) _jspMaxTotal.getValue();
    }

    public int getNumberLetters() {
        return (int) _jspMinLetters.getValue();
    }

    public int getNumberDigits() {
        return (int) _jspMinDigits.getValue();
    }

    public int getNumberPuncts() {
        return (int) _jspMinPuncts.getValue();
    }

    void doOkAction() {
        // No need for validation (spinner are capped to proper min/max)
        setVisible(false);
        _confirmed = true;
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

}
