package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.*;
import static kono.ceu.gtconsolidate.loader.Components.*;

import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

import kono.ceu.gtconsolidate.common.blocks.BlockCoACasing;
import kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing;
import kono.ceu.gtconsolidate.common.blocks.BlockParallelizedAssemblyLineCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class CasingLoader {

    private static final int sec = 20;
    private static final int min = 60 * sec;

    public static void init() {
        // Advanced Assembly Line Casing
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(ROBOT_ARM_ZPM, 4)
                .input(frameGt, Materials.Tritanium)
                .input(plate, Materials.Trinium, 8)
                .input(gearSmall, Materials.Darmstadtium, 2)
                .input(gear, Materials.Tritanium, 2)
                .outputs(GTConsolidateMetaBlocks.PARALLELIZED_ASSEMBLY_LINE_CASING
                        .getItemVariant(BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.CASING,
                                ConfigHolder.recipes.casingsPerCraft))
                .EUt(VA[UV]).duration(30 * sec)
                .stationResearch(b -> b
                        .researchStack(MetaBlocks.MULTIBLOCK_CASING
                                .getItemVariant(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_LINE_CASING))
                        .CWUt(64)
                        .EUt(VA[ZPM]))
                .buildAndRegister();
        // Advanced Assembly Control Casing
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(circuit, MarkerMaterials.Tier.UV, 4)
                .input(SENSOR_ZPM)
                .input(ELECTRIC_MOTOR_ZPM)
                .input(EMITTER_ZPM)
                .input(frameGt, Materials.Tritanium)
                .input(ULTRA_HIGH_POWER_INTEGRATED_CIRCUIT, 2)
                .outputs(GTConsolidateMetaBlocks.PARALLELIZED_ASSEMBLY_LINE_CASING
                        .getItemVariant(BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.CONTROL,
                                ConfigHolder.recipes.casingsPerCraft))
                .EUt(VA[UV]).duration(30 * sec)
                .stationResearch(b -> b
                        .researchStack(MetaBlocks.MULTIBLOCK_CASING
                                .getItemVariant(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_CONTROL))
                        .CWUt(64).EUt(VA[ZPM]))
                .buildAndRegister();
        // Coolant Casing (Empty)
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaBlocks.TRANSPARENT_CASING.getItemVariant(BlockGlassCasing.CasingType.FUSION_GLASS))
                .input(frameGt, Materials.Aluminium)
                .input(screw, Materials.Aluminium, 4)
                .outputs(GTConsolidateMetaBlocks.COOLANT_CASING.getItemVariant(BlockCoolantCasing.CasingType.EMPTY))
                .EUt(VA[EV]).duration(30 * sec).buildAndRegister();
        // Basic Helium Coolant Casing
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .inputs(GTConsolidateMetaBlocks.COOLANT_CASING.getItemVariant(BlockCoolantCasing.CasingType.EMPTY))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 4000))
                .outputs(GTConsolidateMetaBlocks.COOLANT_CASING
                        .getItemVariant(BlockCoolantCasing.CasingType.HELIUM_BASIC))
                .EUt(VA[LuV]).duration(10 * sec).buildAndRegister();
        // Advanced Helium Coolant Casing
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .inputs(GTConsolidateMetaBlocks.COOLANT_CASING
                        .getItemVariant(BlockCoolantCasing.CasingType.HELIUM_BASIC))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 4000))
                .outputs(GTConsolidateMetaBlocks.COOLANT_CASING
                        .getItemVariant(BlockCoolantCasing.CasingType.HELIUM_ADVANCED))
                .EUt(VA[ZPM]).duration(10 * sec).buildAndRegister();
        // Elite Helium Coolant Casing
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .inputs(GTConsolidateMetaBlocks.COOLANT_CASING
                        .getItemVariant(BlockCoolantCasing.CasingType.HELIUM_ADVANCED))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 8000))
                .outputs(GTConsolidateMetaBlocks.COOLANT_CASING
                        .getItemVariant(BlockCoolantCasing.CasingType.HELIUM_ELITE))
                .EUt(VA[UV]).duration(10 * sec).buildAndRegister();
        // CoA Casing
        // LV
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(ELECTRIC_MOTOR_LV)
                .input(ELECTRIC_PISTON_LV)
                .input(ELECTRIC_PUMP_LV)
                .input(CONVEYOR_MODULE_LV)
                .input(ROBOT_ARM_LV)
                .input(FIELD_GENERATOR_LV)
                .input(EMITTER_LV)
                .input(bestCircuit(LV), 2)
                .inputs(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                        .getItemVariant(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING))
                .fluidInputs(Materials.SolderingAlloy.getFluid(144))
                .outputs(GTConsolidateMetaBlocks.COA_CASING.getItemVariant(BlockCoACasing.CoACasingType.LV))
                .stationResearch(b -> b
                        .researchStack(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getItemVariant(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING))
                        .CWUt(16, 3200)
                        .EUt(VA[LV]))
                .EUt(VA[UV]).duration(2 * min)
                .buildAndRegister();
        // MV-UV
        for (int i = 2; i < MAX; i++) {
            if (!GregTechAPI.isHighTier() && i >= UHV) break;
            int j = i;
            RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                    .input(motor(i))
                    .input(piston(i))
                    .input(pump(i))
                    .input(conveyor(i))
                    .input(robotArm(i))
                    .input(fieldGenerator(i))
                    .input(emitter(i))
                    .input(bestCircuit(i), 2)
                    .inputs(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                            .getItemVariant(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING))
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .outputs(GTConsolidateMetaBlocks.COA_CASING
                            .getItemVariant(BlockCoACasing.CoACasingType.valueOf(VN[i])))
                    .stationResearch(b -> b
                            .researchStack(GTConsolidateMetaBlocks.COA_CASING
                                    .getItemVariant(BlockCoACasing.CoACasingType.valueOf(VN[j - 1])))
                            .CWUt(16 * j, VH[j] * 200)
                            .EUt(VA[j]))
                    .EUt(VA[UV]).duration(2 * min)
                    .buildAndRegister();
        }

        // Crystal Quartz Glass
        RecipeMaps.BLAST_RECIPES.recipeBuilder()
                .input(gemExquisite, Materials.CertusQuartz, 8)
                .input(gemExquisite, Materials.NetherQuartz, 8)
                .input(dust, Materials.BorosilicateGlass)
                .fluidInputs(Materials.Krypton.getFluid(100))
                .outputs(GTConsolidateMetaBlocks.COOLANT_CASING
                        .getItemVariant(BlockCoolantCasing.CasingType.CRYSTAL_QUARTZ_GLASS))
                .blastFurnaceTemp(2800)
                .EUt(VA[IV]).duration(30 * sec).buildAndRegister();
    }
}
