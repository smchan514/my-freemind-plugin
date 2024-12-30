package smchan.freemind_my_plugin;

import freemind.extensions.ModeControllerHookAdapter;

/**
 * Simple controller hook to reset attribute encyption utility
 */
public class ResetEncryptUtil extends ModeControllerHookAdapter {

    public ResetEncryptUtil() {
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        EncryptUtil.getInstance().reset();
    }

    @Override
    public void shutdownMapHook() {
        // ...
    }
}
