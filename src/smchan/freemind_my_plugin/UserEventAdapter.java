package smchan.freemind_my_plugin;

import java.awt.Desktop;
import java.awt.desktop.UserSessionEvent;
import java.awt.desktop.UserSessionListener;

/**
 * Desktop user event adapter to handle automatic encryption secret reset when
 * on user lock screen.
 * 
 * Note that the {@link UserSessionListener} interface is introduced in JDK 9.
 */
public class UserEventAdapter implements UserSessionListener {
    public UserEventAdapter() {
        // Register for desktop event
        Desktop.getDesktop().addAppEventListener(this);
    }

    @Override
    public void userSessionDeactivated(UserSessionEvent e) {
        // Clear encryption secrets
        EncryptUtil.getInstance().reset();
    }

    @Override
    public void userSessionActivated(UserSessionEvent e) {
        // ...
    }

}
