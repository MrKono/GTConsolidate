package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;

import gregtech.api.recipes.GTRecipeHandler;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class TurboBlastFurnaceLoader {

    // fix steel recipe confit -remove-
    public static void removeSteel() {
        // Steel dust -> Steel Ingot
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Steel));
        // Iron & Carbone dust -> Steel ingot
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.Iron, 4), OreDictUnifier.get(dust, Materials.Carbon));
        // WroughtIron & Carbone dust -> Steel ingot
        GTRecipeHandler.removeRecipesByInputs(GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE,
                OreDictUnifier.get(dust, Materials.WroughtIron, 4), OreDictUnifier.get(dust, Materials.Carbon));
    }

    // fix steel recipe confit -add-
    public static void addSteel() {
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
    }
}
