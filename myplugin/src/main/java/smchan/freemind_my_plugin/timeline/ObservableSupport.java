/*
 * Created on 2011-08-05
 */
package smchan.freemind_my_plugin.timeline;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;

class ObservableSupport<L>
{
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(ObservableSupport.class.getName());

    private final ArrayList<L> _lstListeners;

    private final Method _methodUpdate;

    private Object[] _arrayListeners;

    public ObservableSupport(Class<L> classListner)
    {
        _lstListeners = new ArrayList<L>();
        _arrayListeners = _lstListeners.toArray();
        _methodUpdate = findMethod(classListner);
    }

    public void addListener(L listener)
    {
        _lstListeners.add(listener);
        _arrayListeners = _lstListeners.toArray();
    }

    public void removeListener(L listener)
    {
        _lstListeners.remove(listener);
        _arrayListeners = _lstListeners.toArray();
    }

    public void notifyListeners(Object args)
    {
        Object[] listeners = _arrayListeners;
        for (Object listener : listeners)
        {
            try
            {
                _methodUpdate.invoke(listener, args);
            }
            catch (Exception e)
            {
                LOGGER.log(Level.FINE, "Unhandled exception", e);
            }
        }
    }

    /**
     * Find the callback method in the argument listener class
     * 
     * @param listenerClass
     * @return a non-null instance of <tt>Method</tt>
     */
    public static Method findMethod(Class<?> listenerClass)
    {
        if (listenerClass == null) {
            throw new NullPointerException();
        }

        Method[] methods = listenerClass.getDeclaredMethods();
        if (methods.length != 1)
        {
            throw new IllegalArgumentException("listenerClass has more than one methods declared, "
                + "only one is supported for now");
        }

        return methods[0];
    }

}
