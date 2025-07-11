package kono.ceu.gtconsolidate.common;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.MODID;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import gregtech.api.unification.material.event.MaterialEvent;

import kono.ceu.gtconsolidate.api.unification.GTConsolidateMaterialFlags;

@Mod.EventBusSubscriber(modid = MODID)
public class GTConsolidateEventHandler {

    public GTConsolidateEventHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterialsHigh(MaterialEvent event) {
        GTConsolidateMaterialFlags.add();
    }
}
