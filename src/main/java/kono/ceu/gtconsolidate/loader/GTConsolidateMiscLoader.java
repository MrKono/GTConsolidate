package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;

public class GTConsolidateMiscLoader {

    public static void init() {
        materials();
    }

    public static void materials() {
        RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                .fluidInputs(Materials.Helium3.getFluid(FluidStorageKeys.GAS, 1000))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 100))
                .fluidOutputs(Materials.Helium3.getFluid(FluidStorageKeys.LIQUID, 1000))
                .duration(10 * 20).EUt(VA[ZPM]).buildAndRegister();
    }
}
