package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.metatileentities.MetaTileEntities;

import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

import kono.ceu.gtconsolidate.common.machines.GTConsolidateMetaTileEntity;

public class MultiMachineLoader {

    public static void init() {
        CEuMultiBlock();
    }

    public static void CEuMultiBlock() {
        // Adv. Fusion Reactors
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[0], 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.LuV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.ADVANCED_FUSION_REACTOR[0])
                .EUt(VA[UV]).duration(600).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[1], 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.ZPM, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.ADVANCED_FUSION_REACTOR[1])
                .EUt(VA[UV]).duration(1200).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[2], 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.ADVANCED_FUSION_REACTOR[2])
                .EUt(VA[UV]).duration(2400).buildAndRegister();
    }
}
