package smchan.freemind_my_plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class InsertWeekNumbersDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    private JButton _jbOK;
    private JTextField _jtfYear;
    private JTextField _jtfFirstWeek;
    private JCheckBox _jcbSeperateByQuarters;

    private boolean _confirmed;

    public InsertWeekNumbersDialog() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Generate week numbers");
        getContentPane().add(createMainPanel());
        getContentPane().add(createButtonsPanel(), BorderLayout.SOUTH);

        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new CancelAction(), keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
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
        GridBagConstraints gbc;

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int firstWeek = 1;

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("Year:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jtfYear = new JTextField(Integer.toString(year), 4);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        comp = new JLabel("First week:");
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        comp = _jtfFirstWeek = new JTextField(Integer.toString(firstWeek), 2);
        panel.add(comp, gbc);

        ///////////////////////
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        comp = _jcbSeperateByQuarters = new JCheckBox("Separate week numbers by quarters", true);
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

    public int getYear() {
        return Integer.parseInt(_jtfYear.getText());
    }

    public int getFirstWeek() {
        return Integer.parseInt(_jtfFirstWeek.getText());
    }

    public boolean getSeparateByQuarters() {
        return _jcbSeperateByQuarters.isSelected();
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
