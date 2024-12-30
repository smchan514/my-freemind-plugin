package smchan.freemind_my_plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

public class PasswordDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private final String _message;
    private boolean _confirmed;
    private char[] _password;
    private JPasswordField _jPwd;
    private JButton _jbOK;

    /**
     * @param prompt Textual prompt to show to the user
     * @return char array containing the user entered password
     * @throws UserCancelledException on cancel
     */
    public static char[] getPassword(String prompt) {
        PasswordDialog dlg = new PasswordDialog("Password", prompt);
        dlg.setVisible(true);

        if (dlg._confirmed) {
            return dlg._password;
        }

        throw new UserCancelledException("User cancelled");
    }

    private PasswordDialog(String title, String message) {
        _message = message;

        setModal(true);
        setTitle(title);
        initComponents();
    }

    private void initComponents() {
        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        setDefaultKeys();
        _jPwd.grabFocus();
    }

    private void setDefaultKeys() {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new CancelAction(), keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setDefaultButton(_jbOK);
    }

    private Component createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;
        JComponent comp;

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = DEFAULT_INSETS;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        comp = new JLabel(_message);

        panel.add(comp, gbc);
        gbc = new GridBagConstraints();
        gbc.insets = DEFAULT_INSETS;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        comp = _jPwd = new JPasswordField(20);
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.insets = DEFAULT_INSETS;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        return panel;
    }

    private Component createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        panel.add(_jbOK = new JButton(new OkAction()));
        panel.add(new JButton(new CancelAction()));

        return panel;
    }

    private void doOk() {
        _confirmed = true;
        _password = _jPwd.getPassword();

        // Hide the dialog to unblock the call to getPassword()
        setVisible(false);
    }

    private void doCancel() {
        _confirmed = false;

        // Hide the dialog to unblock the call to getPassword()
        setVisible(false);
    }

    ///////
    private class OkAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public OkAction() {
            super("OK");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doOk();
        }

    }

    ///////
    private class CancelAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public CancelAction() {
            super("Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            doCancel();
        }

    }

}
