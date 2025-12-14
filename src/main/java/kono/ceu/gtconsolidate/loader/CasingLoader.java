package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.*;
import static kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing.CasingType.*;
import static kono.ceu.gtconsolidate.common.blocks.BlockGearBoxCasing.CasingType.*;
import static kono.ceu.gtconsolidate.common.blocks.BlockMultiblockCasing.MultiblockCasingType.*;
import static kono.ceu.gtconsolidate.common.blocks.BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.*;
import static kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks.*;
import static kono.ceu.gtconsolidate.loader.Components.*;

import gregtech.api.GregTechAPI;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockMultiblockCasing;
import gregtech.common.blocks.MetaBlocks;

import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

import kono.ceu.gtconsolidate.common.blocks.BlockCoACasing;
import kono.ceu.gtconsolidate.common.blocks.BlockGearBoxCasing;
import kono.ceu.gtconsolidate.common.blocks.BlockPipeCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class CasingLoader {

    private static final int sec = 20;
    private static final int min = 60 * sec;
    private static final int amount = ConfigHolder.recipes.casingsPerCraft;

    public static void init() {
        // Advanced Assembly Line Casing
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(ROBOT_ARM_ZPM, 4)
                .input(frameGt, Materials.Tritanium)
                .input(plate, Materials.Trinium, 8)
                .input(gearSmall, Materials.Darmstadtium, 2)
                .input(gear, Materials.Tritanium, 2)
                .outputs(PARALLELIZED_ASSEMBLY_LINE_CASING.getItemVariant(CASING, amount))
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
                .outputs(PARALLELIZED_ASSEMBLY_LINE_CASING.getItemVariant(CONTROL, amount))
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
                .outputs(COOLANT_CASING.getItemVariant(EMPTY))
                .EUt(VA[EV]).duration(30 * sec).buildAndRegister();
        // Basic Helium Coolant Casing
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .inputs(COOLANT_CASING.getItemVariant(EMPTY))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 4000))
                .outputs(COOLANT_CASING.getItemVariant(HELIUM_BASIC))
                .EUt(VA[LuV]).duration(10 * sec).buildAndRegister();
        // Advanced Helium Coolant Casing
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .inputs(COOLANT_CASING.getItemVariant(HELIUM_BASIC))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 4000))
                .outputs(COOLANT_CASING.getItemVariant(HELIUM_ADVANCED))
                .EUt(VA[ZPM]).duration(10 * sec).buildAndRegister();
        // Elite Helium Coolant Casing
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .inputs(COOLANT_CASING.getItemVariant(HELIUM_ADVANCED))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 8000))
                .outputs(COOLANT_CASING.getItemVariant(HELIUM_ELITE))
                .EUt(VA[UV]).duration(10 * sec).buildAndRegister();
        // Helium-3 Coolant Casing
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .inputs(COOLANT_CASING.getItemVariant(EMPTY))
                .fluidInputs(Materials.Helium3.getFluid(FluidStorageKeys.LIQUID, 4000))
                .outputs(COOLANT_CASING.getItemVariant(HELIUM_3))
                .EUt(VA[ZPM]).duration(10 * sec).buildAndRegister();
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
                .outputs(COA_CASING.getItemVariant(BlockCoACasing.CoACasingType.LV))
                .stationResearch(b -> b
                        .researchStack(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                                .getItemVariant(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING))
                        .CWUt(16, 3200)
                        .EUt(VA[LV]))
                .EUt(VA[UV]).duration(2 * min)
                .buildAndRegister();
        // MV-UV
        for (int i = 2; i < 14; i++) {
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
                    .input(bestCircuit(i), i > UHV ? (i - UHV) * 4 : 2)
                    .inputs(GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                            .getItemVariant(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING))
                    .fluidInputs(Materials.SolderingAlloy.getFluid(144 * i))
                    .outputs(COA_CASING
                            .getItemVariant(BlockCoACasing.CoACasingType.valueOf(VN[i])))
                    .stationResearch(b -> b
                            .researchStack(COA_CASING
                                    .getItemVariant(BlockCoACasing.CoACasingType.valueOf(VN[j - 1])))
                            .CWUt(16 * j, (int) Math.min(VH[j] * 200L, V[MAX]))
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
                .outputs(COOLANT_CASING.getItemVariant(CRYSTAL_QUARTZ_GLASS))
                .blastFurnaceTemp(2800)
                .EUt(VA[IV]).duration(30 * sec).buildAndRegister();

        // Osmiridium Sturdy Casing
        ModHandler.addShapedRecipe(true, "osmiridium_sturdy",
                MULTIBLOCK_CASING.getItemVariant(OSMIRIDIUM_STURDY, amount), "PhP", "PFP", "PwP",
                'P', new UnificationEntry(plate, Materials.Osmiridium),
                'F', new UnificationEntry(frameGt, Materials.Trinium));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(plate, Materials.Osmiridium, 6)
                .input(frameGt, Materials.Trinium)
                .circuitMeta(6)
                .outputs(MULTIBLOCK_CASING.getItemVariant(OSMIRIDIUM_STURDY, amount))
                .duration(50).EUt(16).buildAndRegister();

        // Darmstadtium Sturdy Casing
        ModHandler.addShapedRecipe(true, "darmstadtium_sturdy",
                MULTIBLOCK_CASING.getItemVariant(DARMSTADTIUM_STURDY, amount), "PhP", "PFP", "PwP",
                'P', new UnificationEntry(plate, Materials.Darmstadtium),
                'F', new UnificationEntry(frameGt, Materials.Duranium));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(plate, Materials.Darmstadtium, 6)
                .input(frameGt, Materials.Duranium)
                .circuitMeta(6)
                .outputs(MULTIBLOCK_CASING.getItemVariant(DARMSTADTIUM_STURDY, amount))
                .duration(50).EUt(16).buildAndRegister();

        // Tritanium Sturdy Casing
        ModHandler.addShapedRecipe(true, "tritanium_sturdy",
                MULTIBLOCK_CASING.getItemVariant(TRITANIUM_STURDY, amount), "PhP", "PFP", "PwP",
                'P', new UnificationEntry(plate, Materials.Tritanium),
                'F', new UnificationEntry(frameGt, Materials.Neutronium));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(plate, Materials.Tritanium, 6)
                .input(frameGt, Materials.Neutronium)
                .circuitMeta(6)
                .outputs(MULTIBLOCK_CASING.getItemVariant(TRITANIUM_STURDY, amount))
                .duration(50).EUt(16).buildAndRegister();
        // Iridium Plated Casing
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN))
                .fluidInputs(Materials.Iridium.getFluid(144))
                .outputs(MULTIBLOCK_CASING.getItemVariant(IRIDIUM_PLATED))
                .duration(50).EUt(VA[LV]).buildAndRegister();
        // Americium Plated Casing
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .inputs(MULTIBLOCK_CASING.getItemVariant(IRIDIUM_PLATED))
                .fluidInputs(Materials.Americium.getFluid(144))
                .outputs(MULTIBLOCK_CASING.getItemVariant(AMERICIUM_PLATED))
                .duration(50).EUt(VA[LV]).buildAndRegister();

        // Gearbox Casing
        ModHandler.addShapedRecipe("gearbox_iridium",
                GEARBOX_CASING.getItemVariant(IRIDIUM, amount),
                "PhP", "GFG", "PwP",
                'P', new UnificationEntry(plate, Materials.Iridium),
                'G', new UnificationEntry(gear, Materials.Iridium),
                'F', new UnificationEntry(frameGt, Materials.Iridium));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(plate, Materials.Iridium, 6)
                .input(gear, Materials.Iridium, 2)
                .input(frameGt, Materials.Iridium)
                .circuitMeta(4)
                .outputs(GEARBOX_CASING.getItemVariant(IRIDIUM, amount))
                .duration(5 * 20).EUt(VA[LV]).buildAndRegister();
        ModHandler.addShapedRecipe("gearbox_americium", GEARBOX_CASING.getItemVariant(AMERICIUM, amount),
                "PhP", "GFG", "PwP",
                'P', new UnificationEntry(plate, Materials.Americium),
                'G', new UnificationEntry(gear, Materials.Americium),
                'F', new UnificationEntry(frameGt, Materials.Americium));
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(plate, Materials.Americium, 6)
                .input(gear, Materials.Americium, 2)
                .input(frameGt, Materials.Americium)
                .circuitMeta(4)
                .outputs(GEARBOX_CASING.getItemVariant(AMERICIUM, amount))
                .duration(5 * 20).EUt(VA[LV]).buildAndRegister();

        // Pipe Casing
        ModHandler.addShapedRecipe("pipe_casing_iridium", PIPE_CASING.getItemVariant(BlockPipeCasing.CasingType.IRIDIUM, amount),
                "PNP", "NFN", "PNP",
                'P', new UnificationEntry(plate, Materials.Iridium),
                'N', new UnificationEntry(pipeNormalFluid, Materials.Iridium),
                'F', new UnificationEntry(frameGt, Materials.Iridium));
        ModHandler.addShapedRecipe("pipe_casing_americium", PIPE_CASING.getItemVariant(BlockPipeCasing.CasingType.AMERICIUM, amount),
                "PNP", "NFN", "PNP",
                'P', new UnificationEntry(plate, Materials.Americium),
                'N', new UnificationEntry(pipeNormalItem, Materials.Americium),
                'F', new UnificationEntry(frameGt, Materials.Americium));
    }
}
