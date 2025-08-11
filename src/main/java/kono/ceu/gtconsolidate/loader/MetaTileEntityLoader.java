package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;
import static kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity.*;
import static kono.ceu.gtconsolidate.loader.Components.*;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;

import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

import kono.ceu.gtconsolidate.GTConsolidateConfig;

public class MetaTileEntityLoader {

    public static void init() {
        // Filtered Input Bus
        for (int i = 0; i < FILTERED_ITEM_INPUT.length; i++) {
            RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                    .input(MetaTileEntities.ITEM_IMPORT_BUS[i])
                    .input(MetaItems.ITEM_FILTER)
                    .input(OrePrefix.circuit, MarkerMaterials.Tier.HV, 2)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144))
                    .output(FILTERED_ITEM_INPUT[i])
                    .duration(200).EUt(VA[EV]).buildAndRegister();
        }
        // More Parallel Hatch
        if (GTConsolidateConfig.feature.addMoreParallel) {
            RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                    .input(MetaItems.ROBOT_ARM_LV, 64)
                    .input(MetaItems.EMITTER_LV, 64)
                    .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 64)
                    .input(MetaTileEntities.HULL[UHV])
                    .input(OrePrefix.wireGtHex, Materials.ManganesePhosphide, 64)
                    .input(OrePrefix.wireGtHex, Materials.ManganesePhosphide, 64)
                    .fluidInputs(Materials.SolderingAlloy.getFluid(9 * L))
                    .stationResearch(b -> b
                            .researchStack(GCYMMetaTileEntities.PARALLEL_HATCH[UV - IV].getStackForm())
                            .CWUt(32, 6400)
                            .EUt(VA[UEV]))
                    .output(MORE_PARALLEL_HATCHES[0])
                    .duration(20 * 120).EUt(VA[UEV]).buildAndRegister();
            for (int i = 1; i < MORE_PARALLEL_HATCHES.length; i++) {
                int j = i;
                RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                        .input(robotArm(i + 1), 64)
                        .input(emitter(i + 1), 64)
                        .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 64)
                        .input(MetaTileEntities.HULL[UHV])
                        .input(OrePrefix.wireGtHex, scMaterial(i + 1), 64)
                        .input(OrePrefix.wireGtHex, scMaterial(i + 1), 64)
                        .fluidInputs(Materials.SolderingAlloy.getFluid((i + 1) * 9 * L))
                        .stationResearch(b -> b
                                .researchStack(MORE_PARALLEL_HATCHES[j - 1].getStackForm())
                                .CWUt(32 * (j + 1), (int) Math.min(V[j + 1] * 200L, V[MAX]))
                                .EUt(VA[UEV]))
                        .output(MORE_PARALLEL_HATCHES[i])
                        .duration(20 * 120).EUt(VA[UEV]).buildAndRegister();
            }
        }
    }
}
