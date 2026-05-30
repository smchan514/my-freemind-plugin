package smchan.freemind_my_plugin.mru;

import freemind.controller.Controller;

/**
 * Accessor to the global instance of MRUNodesModel
 */
public class MRUNodesModelAccessor {
    private static final int DEFAULT_NBR_MRU_ELEMENTS = 25;

    // Shared global instances of MRU nodes model
    private static boolean _initialized = false;
    private static MRUNodesModel _mruModel;

    /**
     * @param  controller a non-null instance of {@link Controller}
     * @return            the global instance of {@link MRUNodesModel}
     */
    public synchronized static MRUNodesModel getMRUNodesModel(Controller controller) {
        if (!_initialized) {
            int nbrMruElements = DEFAULT_NBR_MRU_ELEMENTS;

            // Create the model and connect it to the data sources
            _mruModel = new MRUNodesModel(nbrMruElements);
            _mruModel.setController(controller);

            _initialized = true;
        }

        return _mruModel;
    }
}
