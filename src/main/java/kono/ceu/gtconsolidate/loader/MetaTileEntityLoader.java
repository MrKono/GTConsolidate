package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

import kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity;

public class MetaTileEntityLoader {

    public static void init() {
        for (int i = 0; i < GTConsolidateMetaTileEntity.FILTERED_ITEM_INPUT.length; i++) {
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(MetaTileEntities.ITEM_IMPORT_BUS[i])
                    .input(MetaItems.ITEM_FILTER)
                    .input(OrePrefix.circuit, MarkerMaterials.Tier.HV, 2)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144))
                    .output(GTConsolidateMetaTileEntity.FILTERED_ITEM_INPUT[i])
                    .duration(200).EUt(VA[EV]).buildAndRegister();
        }
    }
}
