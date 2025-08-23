package kono.ceu.gtconsolidate.common;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.api.recipes.ingredients.GTRecipeInput;
import gregtech.api.unification.material.Materials;

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
                            .blastFurnaceTemp(recipeBuilder.getBlastFurnaceTemp())
                            .duration(recipeBuilder.getDuration())
                            .EUt(recipeBuilder.getEUt())
                            .buildAndRegister();
                });

        RecipeMaps.VACUUM_RECIPES.onRecipeBuild(builder -> {
            RecipeBuilder<SimpleRecipeBuilder> vfBuilder = GTConsolidateRecipeMaps.ABSOLUTE_VACUUM_RECIPE
                    .recipeBuilder()
                    .inputs(builder.getInputs().toArray(new GTRecipeInput[0]))
                    .outputs(builder.getOutputs())
                    .chancedOutputs(builder.getChancedOutputs());
            // Remove Liquid Helium from input
            if (!builder.getFluidInputs().isEmpty()) {
                Map<Fluid, Integer> inputFluidMap = new HashMap<>();
                builder.getFluidInputs().forEach(fluid -> {
                    inputFluidMap.put(fluid.getInputFluidStack().getFluid(), fluid.getAmount());
                });
                for (Fluid fluid : inputFluidMap.keySet()) {
                    if (!fluid.equals(Materials.Helium.getFluid(FluidStorageKeys.LIQUID))) {
                        vfBuilder.fluidInputs(new FluidStack(fluid, inputFluidMap.get(fluid)));
                    }
                }
            }
            // Remove Helium from output
            if (!builder.getFluidOutputs().isEmpty()) {
                Map<Fluid, Integer> outputFluidMap = new HashMap<>();
                builder.getFluidOutputs().forEach(fluid -> {
                    outputFluidMap.put(fluid.getFluid(), fluid.amount);
                });
                for (Fluid fluid : outputFluidMap.keySet()) {
                    if (!fluid.equals(Materials.Helium.getFluid(FluidStorageKeys.GAS))) {
                        vfBuilder.fluidOutputs(new FluidStack(fluid, outputFluidMap.get(fluid)));
                    }
                }
            }
            vfBuilder.buildAndRegister();
        });
    }
}
