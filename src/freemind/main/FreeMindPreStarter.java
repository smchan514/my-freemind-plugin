package freemind.main;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * [SMC 2024-12-01]
 * 
 * Install additional Java Swing "look and feels" before starting FreeMind
 */
public class FreeMindPreStarter {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(FreeMindPreStarter.class.getName());

    public static void main(String[] args) {
        // Install additional Java Swing "look and feels"
        installMoreLookAndFeels();

        // Call the nominal starter
        FreeMindStarter.main(args);
    }

    private static void installMoreLookAndFeels() {
        // Install L&Fs from "FlatLaf" if they are on the class path
        // Otherwise, log some warnings and get on with life
        safeInvokeStaticMethod("com.formdev.flatlaf.FlatLightLaf", "installLafInfo");
        safeInvokeStaticMethod("com.formdev.flatlaf.FlatDarculaLaf", "installLafInfo");
        safeInvokeStaticMethod("com.formdev.flatlaf.FlatDarkLaf", "installLafInfo");
    }

    private static void safeInvokeStaticMethod(String className, String methodName) {
        Class<?> cls;
        try {
            cls = Class.forName(className);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to find class " + className);
            return;
        }

        try {
            Method mtd = cls.getDeclaredMethod(methodName);
            mtd.invoke(null);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to invoke static method " + methodName + " on class " + className);
        }
    }
}
