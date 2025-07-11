package kono.ceu.gtconsolidate.common;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.ingredients.GTRecipeInput;

import gregicality.multiblocks.api.recipes.GCYMRecipeMaps;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class RecipeMapModify {

    public static void modifyRecipeMap() {
        // Compressor
        if (RecipeMaps.COMPRESSOR_RECIPES.getMaxFluidInputs() < 1) {
            RecipeMaps.COMPRESSOR_RECIPES.setMaxFluidInputs(1);
        }
    }

    public static void modifyRecipeBuild() {
        RecipeMaps.BLAST_RECIPES.onRecipeBuild(
                recipeBuilder -> {
                    GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                            .inputs(recipeBuilder.getInputs().toArray(new GTRecipeInput[0]))
                            .fluidInputs(recipeBuilder.getFluidInputs())
                            .outputs(recipeBuilder.getOutputs())
                            .fluidOutputs(recipeBuilder.getFluidOutputs())
                            .chancedOutputs(recipeBuilder.getChancedOutputs())
                            .duration(recipeBuilder.getDuration())
                            .EUt(recipeBuilder.getEUt())
                            .buildAndRegister();
                });
        GCYMRecipeMaps.ALLOY_BLAST_RECIPES.onRecipeBuild(
                recipeBuilder -> {
                    GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE.recipeBuilder()
                            .inputs(recipeBuilder.getInputs().toArray(new GTRecipeInput[0]))
                            .fluidInputs(recipeBuilder.getFluidInputs())
                            .outputs(recipeBuilder.getOutputs())
                            .fluidOutputs(recipeBuilder.getFluidOutputs())
                            .chancedOutputs(recipeBuilder.getChancedOutputs())
                            .duration(recipeBuilder.getDuration())
                            .EUt(recipeBuilder.getEUt())
                            .buildAndRegister();
                });
    }
}
