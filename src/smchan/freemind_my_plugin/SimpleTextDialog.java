package smchan.freemind_my_plugin;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

/**
 * Simple non-editable text dialog for the display of message that can be copied
 * to the clipboard
 */
public class SimpleTextDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);
    private final String _message;
    private JButton _jbOK;
    private JTextArea _jta;

    public SimpleTextDialog(String title, String message) {
        _message = message;

        setTitle(title);
        initComponents();
    }

    private void initComponents() {
        getContentPane().add(createMainPanel());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        setDefaultKeys();
        _jta.grabFocus();
    }

    private void setDefaultKeys() {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().registerKeyboardAction(new OkAction(), keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
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
        comp = new JLabel("Stats of selected node(s):");

        panel.add(comp, gbc);
        gbc = new GridBagConstraints();
        gbc.insets = DEFAULT_INSETS;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        comp = new JScrollPane(_jta = new JTextArea(_message));
        _jta.setEditable(false);
        panel.add(comp, gbc);

        gbc = new GridBagConstraints();
        gbc.insets = DEFAULT_INSETS;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        comp = _jbOK = new JButton(new OkAction());
        panel.add(comp, gbc);

        return panel;
    }

    ///////
    private class OkAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        public OkAction() {
            super("OK");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SimpleTextDialog.this.setVisible(false);
        }

    }

}
