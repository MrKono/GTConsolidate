package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.unification.ore.OrePrefix.ingot;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.common.items.MetaItems;

import gregicality.multiblocks.api.fluids.GCYMFluidStorageKeys;
import gregicality.multiblocks.api.recipes.GCYMRecipeMaps;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class AbsoluteFreezerLoader {

    public static void coolingABS() {
        Map<Fluid, Material> liquidStack = new HashMap<>();
        for (Material material : GregTechAPI.materialManager.getRegisteredMaterials()) {
            if (material.hasFluid() && material.getFluid(FluidStorageKeys.LIQUID) != null &&
                    material.hasProperty(PropertyKey.INGOT) && material.getFluid(GCYMFluidStorageKeys.MOLTEN) == null) {
                liquidStack.put(material.getFluid(FluidStorageKeys.LIQUID), material);
            }
        }
        GCYMRecipeMaps.ALLOY_BLAST_RECIPES.getRecipeList().forEach(recipe -> {
            for (FluidStack stack : recipe.getFluidOutputs()) {
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
