package kono.ceu.gtconsolidate.api.util;

import org.lwjgl.input.Keyboard;

import gregtech.api.GTValues;

public class GTConsolidateUtil {

    public static boolean isTABDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_TAB);
    }

    public static byte getOCTierByVoltage(long voltage) {
        if (voltage <= GTValues.V[GTValues.ULV]) {
            return GTValues.ULV;
        }
        return (byte) ((62 - Long.numberOfLeadingZeros(voltage - 1)) >> 1);
    }
}
