package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;
import static gregtech.common.items.MetaItems.*;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;

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
                .input(OrePrefix.frameGt, Materials.Tritanium)
                .input(OrePrefix.plate, Materials.Trinium, 8)
                .input(OrePrefix.gearSmall, Materials.Darmstadtium, 2)
                .input(OrePrefix.gear, Materials.Tritanium, 2)
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
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UV, 4)
                .input(SENSOR_ZPM)
                .input(ELECTRIC_MOTOR_ZPM)
                .input(EMITTER_ZPM)
                .input(OrePrefix.frameGt, Materials.Tritanium)
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
                .input(OrePrefix.frameGt, Materials.Aluminium)
                .input(OrePrefix.screw, Materials.Aluminium, 4)
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
    }
}
