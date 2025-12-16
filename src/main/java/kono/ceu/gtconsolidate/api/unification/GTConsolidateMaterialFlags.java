package kono.ceu.gtconsolidate.api.unification;

import static gregtech.api.unification.material.info.MaterialFlags.*;
import static kono.ceu.gtconsolidate.loader.Components.scMaterial;

import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlag;
import gregtech.api.unification.material.properties.IMaterialProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.material.properties.WireProperties;

public class GTConsolidateMaterialFlags {

    public static void add() {
        // Flags
        addFlags(Materials.Chrome, GENERATE_LONG_ROD);
        addFlags(Materials.Duranium, GENERATE_FRAME);
        addFlags(Materials.Trinium, GENERATE_FRAME, GENERATE_DENSE, GENERATE_SMALL_GEAR);
        addFlags(Materials.IronMagnetic, GENERATE_LONG_ROD);
        addFlags(Materials.SteelMagnetic, GENERATE_LONG_ROD);
        addFlags(Materials.NeodymiumMagnetic, GENERATE_LONG_ROD);
        addFlags(Materials.Palladium, GENERATE_DENSE);
        addFlags(Materials.Naquadria, GENERATE_DENSE);
        addFlags(Materials.Americium, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_FRAME);
        addFlags(Materials.Neutronium, GENERATE_SMALL_GEAR);
        addFlags(Materials.Ruthenium, GENERATE_SMALL_GEAR);
        addFlags(Materials.Darmstadtium, GENERATE_GEAR);
        addFlags(Materials.RhodiumPlatedPalladium, GENERATE_GEAR);
        addFlags(Materials.BlackSteel, GENERATE_SMALL_GEAR);
        addFlags(Materials.Ultimet, GENERATE_SMALL_GEAR);
        for (int i = 0; i < GTValues.LuV; i++) {
            addFlags(scMaterial(i), GENERATE_FINE_WIRE);
        }
        // Properties
        addProperty(Materials.Ruridit, PropertyKey.WIRE, new WireProperties((int) GTValues.V[GTValues.ZPM], 8, 4));
        addProperty(Materials.Americium, PropertyKey.WIRE, new WireProperties((int) GTValues.V[GTValues.UV], 12, 8));
        // Fluids
        if (Materials.Helium3.getFluid(FluidStorageKeys.LIQUID) == null) {
            Materials.Helium3.getProperty(PropertyKey.FLUID).enqueueRegistration(FluidStorageKeys.LIQUID,
                    new FluidBuilder()
                            .temperature(3)
                            .color(0xFCFCC9)
                            .name("liquid_helium_3")
                            .translation("gregtech.fluid.liquid_generic"));
            Materials.Helium3.getProperty(PropertyKey.FLUID).setPrimaryKey(FluidStorageKeys.GAS);
        }
    }

    private static void addFlags(Material mat, MaterialFlag... flags) {
        for (MaterialFlag flag : flags) {
            if (!mat.hasFlag(flag)) {
                mat.addFlags(flag);
            }
        }
    }

    private static void addProperty(Material mat, PropertyKey<?> key, IMaterialProperty prop) {
        if (!mat.hasProperty(key)) {
            mat.setProperty(key, prop);
        }
    }
}
