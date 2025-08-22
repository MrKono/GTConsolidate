package kono.ceu.gtconsolidate.api.util;

import org.lwjgl.input.Keyboard;

public class GTConsolidateUtil {

    public static boolean isTABDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_TAB);
    }
}
