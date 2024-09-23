package smchan.freemind_my_plugin;

import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;
import freemind.view.mindmapview.EditNodeBase;
import freemind.view.mindmapview.EditNodeDialog;

/**
 * Edit the node attribute __ENC__
 */
public class EditEncryptedAttribute extends ModeControllerHookAdapter {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(EditEncryptedAttribute.class.getName());

    private static final String DEFAULT_ENCRYPTED_ATTR_NAME = "__ENC__";
    private static final String PROP_NAME_ENCRYPT_ATTR_KEYSTORE_PATH = "encrypt_attr.keystore_path";
    private static final String PROP_NAME_ENCRYPT_ATTR_DEFAULT_KEY_ALIAS = "encrypt_attr.default_key_alias";
    private static final String PROP_NAME_ENCRYPT_ATTR_DISBALE_MSG_NO_AUTO_RESET = "encrypt_attr.disable_msg_no_auto_reset";

    private boolean _initialized;

    public EditEncryptedAttribute() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        MindMapController mmc = (MindMapController) getController();
        if (!_initialized) {
            init(mmc);
            _initialized = true;
        }

        // Check for the preconditions
        List<?> selected = getController().getSelecteds();
        if (selected == null || selected.size() != 1) {
            JOptionPane.showMessageDialog(getController().getView(), "Preconditions not met: get one node selected",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the node reference
        MindMapNode node = (MindMapNode) selected.get(0);

        // Load the secret key explicitly so to avoid odd exception cases if the user
        // decides to cancel midway
        try {
            String path = mmc.getController().getProperty(PROP_NAME_ENCRYPT_ATTR_KEYSTORE_PATH);
            String alias = mmc.getController().getProperty(PROP_NAME_ENCRYPT_ATTR_DEFAULT_KEY_ALIAS);
            EncryptUtil.getInstance().setKeystorePath(path);
            EncryptUtil.getInstance().setDefaultKeyAlias(alias);
            EncryptUtil.getInstance().getDefaultSecretKey();
        } catch (UserCancelledException uce) {
            // Skip the rest on user cancellation
            return;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Uncaught exception", e);
            JOptionPane.showMessageDialog(getController().getView(),
                    "Failed to get default secret key in keystore:\n\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Decrypt attribute value, if present
        String cleartext = "";
        String encrypted = node.getAttribute(DEFAULT_ENCRYPTED_ATTR_NAME);
        if (encrypted != null) {
            try {
                cleartext = EncryptUtil.getInstance().decrypt(encrypted);
            } catch (Exception e) {
                // Failed to decrypt current value, ask the user what to do
                LOGGER.log(Level.WARNING, "Uncaught exception", e);

                int opt = JOptionPane.showConfirmDialog(getController().getView(),
                        "Failed to decrypt existing value!\nProceed to edit by discarding current value?\n\nError: "
                                + e.getMessage(),
                        "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                if (opt != JOptionPane.OK_OPTION) {
                    // Skip the rest if not OK
                    return;
                }

                // Discard current value
                cleartext = "";
            }
        }

        // Start the editing process
        showEditor(mmc, node, cleartext);
    }

    /**
     * Copied from freemind.modes.mindmapmode.actions.EditAction.edit(NodeView,
     * NodeView, KeyEvent, boolean, boolean, boolean)
     */
    private void showEditor(MindMapController mmc, MindMapNode node, String text) {
        mmc.setBlocked(true);

        EditNodeDialog nodeEditDialog = new EditNodeDialog(mmc.getNodeView(node), text, null, mmc,
                new EditNodeBase.EditControl() {

                    @Override
                    public void cancel() {
                        mmc.setBlocked(false);
                        mmc.getController().obtainFocusForSelected(); // focus fix
                    }

                    @Override
                    public void ok(String newText) {
                        saveAttributeText(mmc, node, newText);
                        cancel();
                    }

                    @Override
                    public void split(String newText, int position) {
                        JOptionPane.showMessageDialog(getController().getView(), "Split not supported", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        cancel();
                    }
                });
        nodeEditDialog.show();
    }

    private void saveAttributeText(MindMapController mmc, MindMapNode node, String newText) {
        if (newText == null || newText.length() == 0) {
            // Remove the attribute
            int pos = node.getAttributePosition(DEFAULT_ENCRYPTED_ATTR_NAME);
            mmc.removeAttribute(node, pos);
            return;
        }

        try {
            // Encrypt newText and store as a node attribute
            String encrypted = EncryptUtil.getInstance().encrypt(newText);
            mmc.editAttribute(node, DEFAULT_ENCRYPTED_ATTR_NAME, encrypted);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Uncaught exception", e);

            JOptionPane.showMessageDialog(getController().getView(),
                    "Failed to encrypt value! The attribute value will be lost!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void shutdownMapHook() {
        // ...
    }

    private void init(MindMapController mmc) {
        // Use this roundabout way to load the UserEventAdapter class so that in case
        // the JRE at runtime is still JRE 1.8, this class EditEncryptedAttribute can
        // still load
        try {
            Class<?> clz = Class.forName("smchan.freemind_my_plugin.UserEventAdapter");
            clz.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            // Show warning message once
            String value = mmc.getController().getProperty(PROP_NAME_ENCRYPT_ATTR_DISBALE_MSG_NO_AUTO_RESET);
            if (value == null || value.length() == 0) {
                mmc.getController().setProperty(PROP_NAME_ENCRYPT_ATTR_DISBALE_MSG_NO_AUTO_RESET, "true");
                JOptionPane.showMessageDialog(getController().getView(),
                        "Failed to register desktop screen event to reset encryption secrets on user lock screen.\n"
                                + "Use JRE 9 or later to leverage this feature",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
