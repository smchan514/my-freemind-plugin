package smchan.freemind_my_plugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.Timer;

import freemind.controller.Controller;
import freemind.extensions.HookRegistration;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.view.MapModule;

/**
 * Autosave all open maps on Freemind main window focus lost
 */
public class Autosave implements HookRegistration {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Autosave.class.getName());

    private static final String PROP_KEY_AUTOSAVE_ENABLED = "autosave.enabled";
    private static final String PROP_KEY_AUTOSAVE_DELAY_MILLIS = "autosave.delay_millis";
    private static final int DEFAULT_DELAY_MILLIS = 5000;

    private static boolean _firstTime = true;

    public Autosave(ModeController controller, MindMap map) {
        if (_firstTime) {
            activateAutosave(controller);
            _firstTime = false;
        }
    }

    private void activateAutosave(ModeController controller) {
        String str = controller.getController().getProperty(PROP_KEY_AUTOSAVE_ENABLED);

        if (Boolean.valueOf(str)) {
            int delayMillis = getDelayMillis(controller.getController());
            LOGGER.info("Register autosave on application window iconified...");
            controller.getFrame().getJFrame().addWindowListener(new AutosaveAdapter(controller, delayMillis));
        } else {
            LOGGER.info("Autosave not enabled");
        }
    }

    private int getDelayMillis(Controller controller) {
        int value = DEFAULT_DELAY_MILLIS;
        String str = controller.getProperty(PROP_KEY_AUTOSAVE_DELAY_MILLIS);

        try {
            if (str != null && !str.isEmpty()) {
                value = Integer.parseInt(str);
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to parse autosave delay: '" + str + "'");
        }

        return value;
    }

    @Override
    public void register() {
        // ...
    }

    @Override
    public void deRegister() {
        // ...
    }

    //////////////////
    private static class AutosaveAdapter extends WindowAdapter implements ActionListener {
        private final ModeController _controller;
        private Timer _timer;

        public AutosaveAdapter(ModeController controller, int delayMillis) {
            _timer = new Timer(delayMillis, this);
            _timer.setRepeats(false);
            _controller = controller;
        }

        @Override
        public void windowIconified(WindowEvent e) {
            LOGGER.info("Start timer");
            _timer.start();
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            LOGGER.info("Stop timer");
            _timer.stop();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            LOGGER.info("## Autosave triggered");

            // Remember initial map
            MapModule initial = _controller.getController().getMapModule();

            // Iterate through all the open maps
            @SuppressWarnings("unchecked")
            List<MapModule> list = _controller.getController().getMapModuleManager().getMapModuleVector();
            for (MapModule mapModule : list) {
                if (mapModule.getModel().getFile() == null) {
                    // Previously unsaved map, skip for now
                    continue;
                }

                LOGGER.info("Autosave: " + mapModule);

                // Change to map so the UI reflects saved state
                _controller.getController().getMapModuleManager().changeToMapModule(mapModule);
                mapModule.getModeController().save();
            }

            // Switch back to the initial map
            if (initial != null) {
                _controller.getController().getMapModuleManager().changeToMapModule(initial);
            }
        }

    }

}
