package kono.ceu.gtconsolidate.integration;

import kono.ceu.gtconsolidate.integration.top.LaserPassthroughHatchProvider;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class TOPIntegration {

    public static void init() {
        ITheOneProbe probe = TheOneProbe.theOneProbeImp;
        probe.registerProvider(new LaserPassthroughHatchProvider());
    }
}
