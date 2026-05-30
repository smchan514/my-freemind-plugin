package smchan.freemind_my_plugin;

import java.awt.Point;

import javax.swing.JFrame;

import freemind.extensions.ModeControllerHookAdapter;
import smchan.freemind_my_plugin.mru.MRUNodesModel;
import smchan.freemind_my_plugin.mru.MRUNodesModelAccessor;
import smchan.freemind_my_plugin.mru.MRUNodesView;

/**
 * Show the most recently used mindmap nodes.
 */
public class ShowMRUNodes extends ModeControllerHookAdapter {
    // Shared global instances of MRU nodes view
    private static MRUNodesView _mruView;

    public ShowMRUNodes() {
        // ...
    }

    @Override
    public void startupMapHook() {
        super.startupMapHook();

        // Lazy init MRUNodesView
        if (_mruView == null || !_mruView.isVisible()) {
            // Get access to the global instance of MRUNodesModel
            // which is shared with other HookAdapter
            MRUNodesModel mruModel = MRUNodesModelAccessor.getMRUNodesModel(getController().getFrame().getController());

            JFrame owner = getController().getFrame().getJFrame();
            _mruView = new MRUNodesView(owner);
            _mruView.setMRUNodesModel(mruModel);

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
}
