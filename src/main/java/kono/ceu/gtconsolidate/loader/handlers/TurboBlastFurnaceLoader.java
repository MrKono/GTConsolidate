package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.recipes.ingredients.IntCircuitIngredient;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;

import gregicality.multiblocks.api.fluids.GCYMFluidStorageKeys;

import com.github.gtexpert.core.api.unification.material.GTEMaterials;

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

    // fix recipe conflict
    public static void resolveRecipeConflict() {
        // Steel dust -> Steel ingot
        // add circuit 1
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Steel));
        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.Steel)
                .output(ingot, Materials.Steel)
                .circuitMeta(1)
                .blastFurnaceTemp(1000)
                .duration(800).EUt(VA[MV]).buildAndRegister();

        // Iron dust + Carbon dust -> Steel ingot
        // add circuit 1
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Iron, 4), OreDictUnifier.get(dust, Materials.Carbon));
        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.Iron, 4)
                .input(dust, Materials.Carbon)
                .output(ingot, Materials.Steel, 4)
                .chancedOutput(dust, Materials.Ash, 3333, 0)
                .circuitMeta(1)
                .blastFurnaceTemp(2000)
                .duration(250).EUt(VA[EV]).buildAndRegister();

        // Wrought Iron dust + Carbone dust -> Steel ingot
        // add circuit 1
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.WroughtIron, 4), OreDictUnifier.get(dust, Materials.Carbon));
        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.WroughtIron, 4)
                .input(dust, Materials.Carbon)
                .output(ingot, Materials.Steel, 4)
                .chancedOutput(dust, Materials.Ash, 3333, 0)
                .circuitMeta(1)
                .blastFurnaceTemp(2000)
                .duration(50).EUt(VA[EV]).buildAndRegister();

        // Yttrium dust -> Yttrium ingot
        // add circuit 1
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Yttrium));
        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.Yttrium)
                .output(ingot, Materials.Yttrium)
                .circuitMeta(1)
                .blastFurnaceTemp(1799)
                .duration(3202).EUt(VA[MV]).buildAndRegister();

        // Iron dust + 200 mb of Oxygen -> Steel ingot + Ash
        // change circuit number 2 -> 7
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                new ItemStack[] { OreDictUnifier.get(dust, Materials.Iron),
                        IntCircuitIngredient.getIntegratedCircuit(2) },
                new FluidStack[] { Materials.Oxygen.getFluid(200) });
        GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                .input(dust, Materials.Iron)
                .fluidInputs(Materials.Oxygen.getFluid(200))
                .circuitMeta(7)
                .output(ingot, Materials.Steel)
                .chancedOutput(dust, Materials.Ash, 1111, 0)
                .blastFurnaceTemp(1000)
                .duration(20 * 20).EUt(VA[MV]).buildAndRegister();
    }
}
