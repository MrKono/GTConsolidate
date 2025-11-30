package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;

import com.github.gtexpert.core.api.unification.material.GTEMaterials;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;

import gregicality.multiblocks.api.fluids.GCYMFluidStorageKeys;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import kono.ceu.gtconsolidate.api.util.Mods;

public class TurboBlastFurnaceLoader {

    public static void generate() {
        if (Mods.GregTechExpertCore.isModLoaded() && Mods.DraconicEvolution.isModLoaded() &&
                Mods.DraconicAdditions.isModLoaded()) {
            // Pyrotheum
            GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                    .input(dust, Materials.Redstone)
                    .input(dust, Materials.Sulfur)
                    .fluidInputs(Materials.Blaze.getFluid(2304))
                    .fluidInputs(Materials.Argon.getFluid(FluidStorageKeys.GAS, 200))
                    .circuitMeta(15)
                    .blastFurnaceTemp(7200)
                    .fluidOutputs(GTEMaterials.Pyrotheum.getFluid(GCYMFluidStorageKeys.MOLTEN, 1000))
                    .EUt(VA[LuV]).duration(10 * 20).buildAndRegister();

            GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                    .input(dust, Materials.Redstone)
                    .input(dust, Materials.Sulfur)
                    .fluidInputs(Materials.Blaze.getFluid(2304))
                    .circuitMeta(5)
                    .blastFurnaceTemp(7200)
                    .fluidOutputs(GTEMaterials.Pyrotheum.getFluid(GCYMFluidStorageKeys.MOLTEN, 1000))
                    .EUt(VA[LuV]).duration(60 * 20).buildAndRegister();
        }
    }

    // fix recipe confit: -remove-
    public static void removeConfitRecipe() {
        // Steel dust -> Steel Ingot
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Steel));
        // Iron & Carbone dust -> Steel ingot
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Iron, 4), OreDictUnifier.get(dust, Materials.Carbon));
        // WroughtIron & Carbone dust -> Steel ingot
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.WroughtIron, 4), OreDictUnifier.get(dust, Materials.Carbon));
        // Yttrium dust -> Yttrium ingot
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Yttrium));
    }

    // fix recipe confit: re-add
    public static void reAddRecipe() {
        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.Steel)
                .output(ingot, Materials.Steel)
                .circuitMeta(1)
                .blastFurnaceTemp(1000)
                .duration(800).EUt(VA[MV]).buildAndRegister();

        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.Iron, 4)
                .input(dust, Materials.Carbon)
                .output(ingot, Materials.Steel, 4)
                .chancedOutput(dust, Materials.Ash, 3333, 0)
                .circuitMeta(1)
                .blastFurnaceTemp(2000)
                .duration(250).EUt(VA[EV]).buildAndRegister();

        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.WroughtIron, 4)
                .input(dust, Materials.Carbon)
                .output(ingot, Materials.Steel, 4)
                .chancedOutput(dust, Materials.Ash, 3333, 0)
                .circuitMeta(1)
                .blastFurnaceTemp(2000)
                .duration(50).EUt(VA[EV]).buildAndRegister();

        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.Yttrium)
                .output(ingot, Materials.Yttrium)
                .circuitMeta(1)
                .blastFurnaceTemp(1799)
                .duration(3202).EUt(VA[MV]).buildAndRegister();
    }
}
