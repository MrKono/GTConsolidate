package kono.ceu.gtconsolidate.loader.handlers;

import gregicality.multiblocks.api.fluids.GCYMFluidStorageKeys;
import gregicality.multiblocks.api.recipes.GCYMRecipeMaps;
import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.common.items.MetaItems;
import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.Map;

import static gregtech.api.unification.material.info.MaterialFlags.IS_MAGNETIC;
import static gregtech.api.unification.ore.OrePrefix.ingot;

public class AbsoluteFreezerLoader {

    public static void coolingABS() {
        Map<Fluid, Material> liquidStack = new HashMap<>();
        for (Material mat : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (!mat.hasFlag(IS_MAGNETIC) && mat.hasProperty(PropertyKey.INGOT) && mat.hasFluid()) {
                if (mat.getFluid(FluidStorageKeys.LIQUID) != null && mat.getFluid(GCYMFluidStorageKeys.MOLTEN) == null)
                    liquidStack.put(mat.getFluid(FluidStorageKeys.LIQUID), mat);
            }
        }
        GCYMRecipeMaps.ALLOY_BLAST_RECIPES.onRecipeBuild(blastRecipeBuilder -> {
            for (FluidStack stack : blastRecipeBuilder.getFluidOutputs()) {
                Fluid fluid = stack.getFluid();
                if (liquidStack.containsKey(fluid)) {
                    GTConsolidateRecipeMaps.ABSOLUTE_VACUUM_RECIPE.recipeBuilder()
                            .notConsumable(MetaItems.SHAPE_MOLD_INGOT)
                            .fluidInputs(new FluidStack(fluid, 144))
                            .output(ingot, liquidStack.get(fluid))
                            .buildAndRegister();
                }
            }
        });
    }
}
