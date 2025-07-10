package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.unification.material.info.MaterialFlags.IS_MAGNETIC;
import static gregtech.api.unification.ore.OrePrefix.*;

import java.util.*;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.builders.BlastRecipeBuilder;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;

import gregicality.multiblocks.api.fluids.GCYMFluidStorageKeys;
import gregicality.multiblocks.api.recipes.GCYMRecipeMaps;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class AbsoluteFreezerLoader {

    public static void register() {
        coolingEBF();
        coolingABS();
    }

    public static void coolingEBF() {
        for (Material mat : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (ingotHot.doGenerateItem(mat)) {
                GTConsolidateRecipeMaps.ABSOLUTE_VACUUM_RECIPE.recipeBuilder()
                        .input(ingotHot, mat)
                        .output(ingot, mat)
                        .buildAndRegister();
            }
        }
    }

    public static void coolingABS() {
        Map<Fluid, Material> liquidStack = new HashMap<>();
        for (Material mat : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (!mat.hasFlag(IS_MAGNETIC) && mat.hasProperty(PropertyKey.INGOT) && mat.hasFluid()) {
                if (mat.getFluid(GCYMFluidStorageKeys.MOLTEN) != null) {
                    liquidStack.put(mat.getFluid(GCYMFluidStorageKeys.MOLTEN), mat);
                } else if (mat.getFluid(FluidStorageKeys.LIQUID) != null)
                    liquidStack.put(mat.getFluid(FluidStorageKeys.LIQUID), mat);
            }
        }
        GCYMRecipeMaps.ALLOY_BLAST_RECIPES.onRecipeBuild(blastRecipeBuilder -> {
            for (FluidStack stack : blastRecipeBuilder.getFluidOutputs()) {
                Fluid fluid = stack.getFluid();
                if (liquidStack.containsKey(fluid)) {
                    GTConsolidateRecipeMaps.ABSOLUTE_VACUUM_RECIPE.recipeBuilder()
                            .fluidInputs(new FluidStack(fluid, 144))
                            .output(ingot, liquidStack.get(fluid))
                            .buildAndRegister();
                }
            }
        });
    }
}
