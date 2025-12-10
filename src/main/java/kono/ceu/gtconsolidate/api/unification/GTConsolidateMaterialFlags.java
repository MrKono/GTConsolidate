package kono.ceu.gtconsolidate.api.unification;

import static gregtech.api.unification.material.info.MaterialFlags.*;
import static kono.ceu.gtconsolidate.loader.Components.scMaterial;

import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.material.properties.WireProperties;
import net.minecraft.block.material.Material;

public class GTConsolidateMaterialFlags {

    public static void add() {
        // Chrome
        if (!Materials.Chrome.hasFlag(GENERATE_LONG_ROD)) Materials.Chrome.addFlags(GENERATE_LONG_ROD);
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
        if (!Materials.Duranium.hasFlag(GENERATE_FRAME)) Materials.Duranium.addFlags(GENERATE_FRAME);
        // Trinium
        if (!Materials.Trinium.hasFlag(GENERATE_FRAME)) Materials.Trinium.addFlags(GENERATE_FRAME);
        if (!Materials.Trinium.hasFlag(GENERATE_DENSE)) Materials.Trinium.addFlags(GENERATE_DENSE);
        if (!Materials.Trinium.hasFlag(GENERATE_SMALL_GEAR)) Materials.Trinium.addFlags(GENERATE_SMALL_GEAR);
        // IronMagnetic
        if (!Materials.IronMagnetic.hasFlag(GENERATE_LONG_ROD)) Materials.IronMagnetic.addFlags(GENERATE_LONG_ROD);
        // SteelMagnetic
        if (!Materials.SteelMagnetic.hasFlag(GENERATE_LONG_ROD)) Materials.SteelMagnetic.addFlags(GENERATE_LONG_ROD);
        // NeodymiumMagnetic
        if (!Materials.NeodymiumMagnetic.hasFlag(GENERATE_LONG_ROD))
            Materials.NeodymiumMagnetic.addFlags(GENERATE_LONG_ROD);
        // Palladium
        if (!Materials.Palladium.hasFlag(GENERATE_DENSE)) Materials.Palladium.addFlags(GENERATE_DENSE);
        // Naquadria
        if (!Materials.Naquadria.hasFlag(GENERATE_DENSE)) Materials.Naquadria.addFlags(GENERATE_DENSE);
        // ULV-IV scMaterials
        for (int i = 0; i < GTValues.LuV; i++) {
            if (!scMaterial(i).hasFlag(GENERATE_FINE_WIRE)) scMaterial(i).addFlags(GENERATE_FINE_WIRE);
        }
        // Ruridit
        if (!Materials.Ruridit.hasProperty(PropertyKey.WIRE)) {
            Materials.Ruridit.setProperty(PropertyKey.WIRE, new WireProperties((int) GTValues.V[GTValues.ZPM], 8, 4));
        }
        // Americium
        if (!Materials.Americium.hasProperty(PropertyKey.WIRE)) {
            Materials.Americium.setProperty(PropertyKey.WIRE, new WireProperties((int) GTValues.V[GTValues.UV], 12, 8));
        }
        if (!Materials.Americium.hasFlag(GENERATE_GEAR)) Materials.Americium.addFlags(GENERATE_GEAR);
        if (!Materials.Americium.hasFlag(GENERATE_SMALL_GEAR)) Materials.Americium.addFlags(GENERATE_SMALL_GEAR);
        // Neutronium
        if (!Materials.Neutronium.hasFlag(GENERATE_SMALL_GEAR)) Materials.Neutronium.addFlags(GENERATE_SMALL_GEAR);
        // Ruthenium
        if (!Materials.Ruthenium.hasFlag(GENERATE_SMALL_GEAR)) Materials.Ruthenium.addFlags(GENERATE_SMALL_GEAR);
    }
}
