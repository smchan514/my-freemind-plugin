package smchan.freemind_my_plugin;

import java.awt.Point;

import javax.swing.JFrame;

import freemind.extensions.ModeControllerHookAdapter;
import smchan.freemind_my_plugin.mru.MRUNodesModel;
import smchan.freemind_my_plugin.mru.MRUNodesView;

/**
 * Show the most recently used mindmap nodes.
 */
public class ShowMRUNodes extends ModeControllerHookAdapter {
    private static final int DEFAULT_NBR_MRU_ELEMENTS = 25;

    // Shared global instances of MRU nodes model and view
    private static boolean _initialized = false;
    private static MRUNodesView _mruView;
    private static MRUNodesModel _mruModel;

    public ShowMRUNodes() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        if (!_initialized) {
            performInit();
        }

        if (_mruView == null || !_mruView.isVisible()) {
            JFrame owner = getController().getFrame().getJFrame();
            _mruView = new MRUNodesView(owner);
            _mruView.setMRUNodesModel(_mruModel);

            _mruView.pack();

            // Align the MRU dialog to the lower right corner of the owner frame
            Point pt = new Point();
            owner.getLocation(pt);
            pt.x += owner.getWidth() - _mruView.getWidth();
            pt.y += owner.getHeight() - _mruView.getHeight();
            _mruView.setLocation(pt);

            _mruView.setVisible(true);
        } else {
            _mruView.requestFocus();
        }
    }

    /**
     * Perform initialization of this action
     */
    private void performInit() {
        int nbrMruElements = DEFAULT_NBR_MRU_ELEMENTS;
        String str;

        if ((str = getResourceString("nbr_mru_elements")) != null)
            nbrMruElements = Integer.parseInt(str);

        // Create the model and connect it to the data sources
        _mruModel = new MRUNodesModel(nbrMruElements);
        _mruModel.setController(getController().getFrame().getController());

        _initialized = true;
    }
}
