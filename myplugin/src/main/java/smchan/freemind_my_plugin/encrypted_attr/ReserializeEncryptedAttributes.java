package smchan.freemind_my_plugin.encrypted_attr;

import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import freemind.extensions.ModeControllerHookAdapter;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.MindMapController;

/**
 * 
 */
public class ReserializeEncryptedAttributes extends ModeControllerHookAdapter {
    private static final String DEFAULT_ENCRYPTED_ATTR_NAME = "__ENC__";
    private static final String NEW_SECRET_KEY_ALIAS = "eed60018-3c42-4604-961a-cd3c20acbc3d";
    private int _countEncryptedAttributes;
    private int _countFailedOps;

    public ReserializeEncryptedAttributes() {
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        List<?> list = getController().getSelecteds();
        if (list == null || list.size() != 1) {
            JOptionPane.showMessageDialog(getController().getFrame().getJFrame(),
                    "Preconditions not met: get one node selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MindMapNode selected = (MindMapNode) list.get(0);
        MindMapController mmc = (MindMapController) getController();

        resetStats();
        processNode(mmc, selected);
        printStats();
    }

    private void printStats() {
        System.out.println("Stats:");
        System.out.println(">> _countEncryptedAttributes= " + _countEncryptedAttributes);
        System.out.println(">> _countFailedOps= " + _countFailedOps);
    }

    private void resetStats() {
        _countEncryptedAttributes = 0;
        _countFailedOps = 0;
    }

    private void processNode(MindMapController mmc, MindMapNode node) {
        String oldValue = node.getAttribute(DEFAULT_ENCRYPTED_ATTR_NAME);
        if (oldValue != null) {
            _countEncryptedAttributes++;
            try {
                String newValue = reserialize(oldValue);
                mmc.editAttribute(node, DEFAULT_ENCRYPTED_ATTR_NAME, newValue);
            } catch (IOException e) {
                _countFailedOps++;
                System.out.println("Failed to process encrypted attribute in node: " + node.getObjectId(mmc));
            }
        }

        for (Object child : node.getChildren()) {
            processNode(mmc, (MindMapNode) child);
        }
    }

    private String reserialize(String oldValue) throws IOException {
        EncryptedAttribute oldObj = EncryptedAttribute.decodeBase64(oldValue);
        EncryptedAttributeV1 v1Obj = new EncryptedAttributeV1();
        v1Obj.copyFrom(oldObj);
        v1Obj.setSecretKeyAlias(NEW_SECRET_KEY_ALIAS);
        return v1Obj.encodeBase64();
    }

    @Override
    public void shutdownMapHook() {
        // ...
    }
}
