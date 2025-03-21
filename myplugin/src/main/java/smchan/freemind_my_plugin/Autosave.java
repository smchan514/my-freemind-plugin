package smchan.freemind_my_plugin;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import freemind.extensions.HookRegistration;
import freemind.modes.MindMap;
import freemind.modes.ModeController;
import freemind.view.MapModule;

/**
 * Autosave all open maps on Freemind main window focus lost
 */
public class Autosave implements HookRegistration {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Autosave.class.getName());

    private static boolean _firstTime = true;

    public Autosave(ModeController controller, MindMap map) {
        if (_firstTime) {
            LOGGER.fine("Register autosave on windows focus lost...");
            controller.getFrame().getJFrame().addWindowFocusListener(new AutosaveAdapter(controller));
            _firstTime = false;
        }
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
    private static class AutosaveAdapter extends WindowAdapter {
        private final ModeController _controller;

        public AutosaveAdapter(ModeController controller) {
            _controller = controller;
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
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

                LOGGER.fine("Autosave: " + mapModule);

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
