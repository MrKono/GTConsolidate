package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;

import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.metatileentities.MetaTileEntities;

import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

import kono.ceu.gtconsolidate.common.machines.GTConsolidateMetaTileEntity;

public class MultiMachineLoader {

    private static final int sec = 20;
    private static final int min = 60 * sec;

    public static void init() {
        CEuMultiBlock();
    }

    public static void CEuMultiBlock() {
        // Adv. Fusion Reactors
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[0], 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.LuV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_FUSION_REACTOR[0])
                .EUt(VA[UV]).duration(30 * sec).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[1], 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.ZPM, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_FUSION_REACTOR[1])
                .EUt(VA[UV]).duration(1 * min).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[2], 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_FUSION_REACTOR[2])
                .EUt(VA[UV]).duration(2 * min).buildAndRegister();

        // Adv. EBF
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ELECTRIC_BLAST_FURNACE, 4)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.IV, 8)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[0])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[0])
                .EUt(VA[LuV]).duration(1 * min).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ELECTRIC_BLAST_FURNACE, 16)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.LuV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[1])
                .EUt(VA[LuV]).duration(2 * min).buildAndRegister();
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .input(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[0], 4)
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[1])
                .EUt(VA[ZPM]).duration(1 * min + 30 * sec).buildAndRegister();
    }
}
