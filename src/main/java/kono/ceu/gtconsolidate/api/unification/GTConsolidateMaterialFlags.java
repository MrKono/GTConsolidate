package kono.ceu.gtconsolidate.api.unification;

import static gregtech.api.unification.material.info.MaterialFlags.*;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.PropertyKey;

public class GTConsolidateMaterialFlags {

    public static void add() {
        // Helium-3
        if (Materials.Helium3.getFluid(FluidStorageKeys.LIQUID) == null) {
            Materials.Helium3.getProperty(PropertyKey.FLUID).enqueueRegistration(FluidStorageKeys.LIQUID,
                    new FluidBuilder()
                            .temperature(3)
                            .color(0xFCFCC9)
                            .name("liquid_helium_3")
                            .translation("gregtech.fluid.liquid_generic"));
            Materials.Helium3.getProperty(PropertyKey.FLUID).setPrimaryKey(FluidStorageKeys.GAS);
        }
        // Duranium
        Materials.Duranium.addFlags(GENERATE_FRAME);
        // Trinium
        Materials.Trinium.addFlags(GENERATE_FRAME);
    }
}
