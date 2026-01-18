package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.*;

import com.github.gtexpert.gtwp.common.metatileentities.GTWPMetaTileEntities;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.ModHandler;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.blocks.BlockFusionCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.items.MetaItems;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtechfoodoption.machines.GTFOTileEntities;

import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

import kono.ceu.gtconsolidate.api.util.Mods;
import kono.ceu.gtconsolidate.common.blocks.BlockParallelizedAssemblyLineCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;
import kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity;

public class MultiMachineLoader {

    private static final int sec = 20;
    private static final int min = 60 * sec;

    public static void init() {
        CEuMultiBlock();
        if (Mods.GregTechFoodOption.isModLoaded()) {
            GTFOMultiblock();
        }
        if (Mods.GTWoodProcessing.isModLoaded()) {
            GTWPMultiblock();
        }
    }

    public static void CEuMultiBlock() {
        // Adv. Fusion Reactors
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[0], 16)
                .input(circuit, MarkerMaterials.Tier.LuV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_FUSION_REACTOR[0])
                .EUt(VA[UV]).duration(30 * sec).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[1], 16)
                .input(circuit, MarkerMaterials.Tier.ZPM, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_FUSION_REACTOR[1])
                .EUt(VA[UV]).duration(1 * min).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.FUSION_REACTOR[2], 16)
                .input(circuit, MarkerMaterials.Tier.UV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_FUSION_REACTOR[2])
                .EUt(VA[UV]).duration(2 * min).buildAndRegister();

        // Adv. EBF
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ELECTRIC_BLAST_FURNACE, 4)
                .input(circuit, MarkerMaterials.Tier.IV, 8)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[0])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[0])
                .EUt(VA[LuV]).duration(1 * min).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ELECTRIC_BLAST_FURNACE, 16)
                .input(circuit, MarkerMaterials.Tier.LuV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[1])
                .EUt(VA[LuV]).duration(2 * min).buildAndRegister();
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .input(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[0], 4)
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_EBF[1])
                .EUt(VA[ZPM]).duration(1 * min + 30 * sec).buildAndRegister();

        // Adv. VF
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.VACUUM_FREEZER, 4)
                .input(circuit, MarkerMaterials.Tier.IV, 8)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[0])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_VF[0])
                .EUt(VA[LuV]).duration(1 * min).buildAndRegister();
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.VACUUM_FREEZER, 16)
                .input(circuit, MarkerMaterials.Tier.LuV, 32)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_VF[1])
                .EUt(VA[LuV]).duration(2 * min).buildAndRegister();
        RecipeMaps.COMPRESSOR_RECIPES.recipeBuilder()
                .input(GTConsolidateMetaTileEntity.PARALLELIZED_VF[0], 4)
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_VF[1])
                .EUt(VA[ZPM]).duration(1 * min + 30 * sec).buildAndRegister();

        // Adv, Assembly Line
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ASSEMBLY_LINE, 4)
                .inputs(GTConsolidateMetaBlocks.PARALLELIZED_ASSEMBLY_LINE_CASING
                        .getItemVariant(BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.CONTROL,
                                4))
                .input(circuit, MarkerMaterials.Tier.UV, 2)
                .input(ROBOT_ARM_LuV)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[0])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_ASSEMBLY_LINE[0])
                .EUt(VA[UV]).duration(1 * min).buildAndRegister();
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ASSEMBLY_LINE, 16)
                .inputs(GTConsolidateMetaBlocks.PARALLELIZED_ASSEMBLY_LINE_CASING
                        .getItemVariant(BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.CONTROL,
                                4))
                .input(circuit, MarkerMaterials.Tier.UHV, 2)
                .input(ROBOT_ARM_ZPM)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[1])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_ASSEMBLY_LINE[1])
                .EUt(VA[UHV]).duration(1 * min).buildAndRegister();
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ASSEMBLY_LINE, 64)
                .inputs(GTConsolidateMetaBlocks.PARALLELIZED_ASSEMBLY_LINE_CASING
                        .getItemVariant(BlockParallelizedAssemblyLineCasing.ParallelizedAssemblyLineCasingType.CONTROL,
                                4))
                .input(circuit, MarkerMaterials.Tier.UHV, 8)
                .input(ROBOT_ARM_UV)
                .input(GCYMMetaTileEntities.PARALLEL_HATCH[2])
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_ASSEMBLY_LINE[2])
                .EUt(VA[UEV]).duration(1 * min).buildAndRegister();

        // CoA
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ASSEMBLY_LINE)
                .input(TOOL_DATA_MODULE, 4)
                .input(ROBOT_ARM_LV, 4)
                .input(ROBOT_ARM_MV, 4)
                .input(ROBOT_ARM_HV, 4)
                .input(ROBOT_ARM_EV, 4)
                .input(ROBOT_ARM_IV, 4)
                .input(ROBOT_ARM_LuV, 4)
                .input(ROBOT_ARM_ZPM, 4)
                .input(ROBOT_ARM_UV, 4)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * 64))
                .fluidInputs(Materials.Lubricant.getFluid(10000))
                .output(GTConsolidateMetaTileEntity.COMPONENT_ASSEMBLY_LINE)
                .EUt(VA[LuV]).duration(2 * min).buildAndRegister();

        // Adv. Multi Smelter
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.ELECTRIC_FURNACE[IV], 3)
                .input(circuit, MarkerMaterials.Tier.LuV, 6)
                .inputs(MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.INVAR_HEATPROOF))
                .input(cableGtSingle, Materials.Platinum, 4)
                .scannerResearch(b -> b
                        .researchStack(MetaTileEntities.MULTI_FURNACE.getStackForm())
                        .EUt(VA[IV]).duration(1 * min))
                .output(GTConsolidateMetaTileEntity.MEGA_FURNACE)
                .EUt(VA[LuV]).duration((90 * sec)).buildAndRegister();

        // Rotary Hearth Blast Smelter
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(GCYMMetaTileEntities.ALLOY_BLAST_SMELTER)
                .input(GCYMMetaTileEntities.MEGA_BLAST_FURNACE)
                .input(wireGtOctal, Materials.RutheniumTriniumAmericiumNeutronate, 16)
                .inputs(MetaBlocks.FUSION_CASING.getItemVariant(BlockFusionCasing.CasingType.FUSION_COIL, 4))
                .input(circuit, MarkerMaterials.Tier.UHV, 8)
                .input(FIELD_GENERATOR_UV, 4)
                .input(plateDense, Materials.Neutronium, 4)
                .input(spring, Materials.Neutronium, 4)
                .output(GTConsolidateMetaTileEntity.TURBO_BLAST_FURNACE)
                .EUt(VA[UHV]).duration(2 * min).buildAndRegister();

        // Absolute Freezer
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(GCYMMetaTileEntities.MEGA_VACUUM_FREEZER)
                .input(pipeHugeFluid, Materials.Neutronium, 4)
                .input(ELECTRIC_PUMP_UV, 4)
                .input(wireGtOctal, Materials.RutheniumTriniumAmericiumNeutronate, 16)
                .input(circuit, MarkerMaterials.Tier.UHV, 8)
                .input(FIELD_GENERATOR_UV, 4)
                .input(plateDense, Materials.Neutronium, 4)
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 64000))
                .fluidInputs(Materials.Helium3.getFluid(FluidStorageKeys.LIQUID, 64000))
                .output(GTConsolidateMetaTileEntity.ABSOLUTE_FREEZER)
                .EUt(VA[UHV]).duration(2 * min).buildAndRegister();

        // Circuit Factory
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(GCYMMetaTileEntities.LARGE_CIRCUIT_ASSEMBLER)
                .input(ROBOT_ARM_LuV, 4)
                .input(ROBOT_ARM_ZPM, 4)
                .input(ROBOT_ARM_UV, 4)
                .input(MICROPROCESSOR_LV, 16)
                .input(PROCESSOR_MV, 16)
                .input(NANO_PROCESSOR_HV, 16)
                .input(QUANTUM_PROCESSOR_EV, 16)
                .input(CRYSTAL_PROCESSOR_IV, 16)
                .input(WETWARE_PROCESSOR_LUV, 16)
                .input(WETWARE_PROCESSOR_ASSEMBLY_ZPM, 8)
                .input(WETWARE_SUPER_COMPUTER_UV, 4)
                .input(WETWARE_MAINFRAME_UHV, 1)
                .fluidInputs(Materials.DistilledWater.getFluid(64000))
                .fluidInputs(Materials.Lubricant.getFluid(32000))
                .fluidInputs(Materials.PCBCoolant.getFluid(16000))
                .output(GTConsolidateMetaTileEntity.CIRCUIT_FACTORY)
                .EUt(VA[UHV]).duration(2 * min + 30 * sec).buildAndRegister();

        // Elite Processing Array
        ModHandler.addShapedRecipe(true, "elite_processing_array",
                GTConsolidateMetaTileEntity.EXTENDED_PROCESSING_ARRAY[0].getStackForm(),
                "RCR", "SAE", "PFP",
                'R', ROBOT_ARM_ZPM,
                'C', new UnificationEntry(circuit, MarkerMaterials.Tier.UV),
                'S', SENSOR_ZPM,
                'A', MetaTileEntities.ADVANCED_PROCESSING_ARRAY.getStackForm(),
                'E', EMITTER_ZPM,
                'P', new UnificationEntry(plate, Materials.Osmiridium),
                'F', new UnificationEntry(pipeLargeFluid, Materials.Europium));

        // Master Processing Array
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(ROBOT_ARM_UV, 2)
                .input(circuit, MarkerMaterials.Tier.UHV)
                .input(SENSOR_UV)
                .input(EMITTER_UV)
                .input(plate, Materials.Darmstadtium, 2)
                .input(pipeLargeFluid, Materials.Duranium)
                .input(GTConsolidateMetaTileEntity.EXTENDED_PROCESSING_ARRAY[0])
                .stationResearch(b -> b
                        .researchStack(GTConsolidateMetaTileEntity.EXTENDED_PROCESSING_ARRAY[0].getStackForm())
                        .CWUt(64, 12800)
                        .EUt(VA[LuV]))
                .output(GTConsolidateMetaTileEntity.EXTENDED_PROCESSING_ARRAY[1])
                .duration(90 * sec).EUt(VA[UHV]).buildAndRegister();

        // Ultimate Processing Array
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(ROBOT_ARM_UV, 8)
                .input(circuit, MarkerMaterials.Tier.UHV, 4)
                .input(SENSOR_UV, 4)
                .input(EMITTER_UV, 4)
                .input(plate, Materials.Tritanium, 2)
                .input(pipeLargeFluid, Materials.Neutronium)
                .input(GTConsolidateMetaTileEntity.EXTENDED_PROCESSING_ARRAY[1])
                .stationResearch(b -> b
                        .researchStack(GTConsolidateMetaTileEntity.EXTENDED_PROCESSING_ARRAY[1].getStackForm())
                        .CWUt(128, 102400)
                        .EUt(VA[UV]))
                .output(GTConsolidateMetaTileEntity.EXTENDED_PROCESSING_ARRAY[2])
                .duration(3 * min).EUt(VA[UHV]).buildAndRegister();

        // Industrial Bricked Blast Furnace
        ModHandler.addShapedRecipe(true, "industrial_bricked_blast_furnace",
                GTConsolidateMetaTileEntity.INDUSTRIAL_BBF.getStackForm(), "PBP", "FCF", "PBP",
                'P', new UnificationEntry(plate, Materials.Steel),
                'B', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS),
                'F', MetaTileEntities.PRIMITIVE_BLAST_FURNACE.getStackForm(),
                'C', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));

        // Industrial Coke Oven
        ModHandler.addShapedRecipe(true, "industrial_coke_oven",
                GTConsolidateMetaTileEntity.INDUSTRIAL_COKE_OVEN.getStackForm(), "PBP", "FCF", "PBP",
                'P', new UnificationEntry(gem, Materials.Coke),
                'B', MetaBlocks.METAL_CASING.getItemVariant(BlockMetalCasing.MetalCasingType.COKE_BRICKS),
                'F', MetaTileEntities.COKE_OVEN.getStackForm(),
                'C', new UnificationEntry(circuit, MarkerMaterials.Tier.ULV));

        // Ore Processing Factory
        RecipeMaps.ASSEMBLER_RECIPES.recipeBuilder()
                .input(MetaTileEntities.MACERATOR[IV])
                .input(MetaTileEntities.ORE_WASHER[IV])
                .input(MetaTileEntities.CENTRIFUGE[IV])
                .input(MetaTileEntities.SIFTER[IV])
                .input(MetaTileEntities.CHEMICAL_BATH[IV])
                .input(MetaTileEntities.THERMAL_CENTRIFUGE[IV])
                .input(OrePrefix.circuit, MarkerMaterials.Tier.LuV, 4)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * 8))
                .output(GTConsolidateMetaTileEntity.ORE_FACTORY[0])
                .duration(10 * 20).EUt(VA[IV]).buildAndRegister();

        // Industrial Ore Processing Factory
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(GTConsolidateMetaTileEntity.ORE_FACTORY[0])
                .input(OrePrefix.gearSmall, Materials.TungstenCarbide, 8)
                .input(OrePrefix.gear, Materials.Tritanium, 4)
                .input(MetaItems.ELECTRIC_MOTOR_UV, 2)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UHV, 1)
                .input(OrePrefix.circuit, MarkerMaterials.Tier.UV, 4)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * 16))
                .fluidInputs(Materials.Lubricant.getFluid(4000))
                .output(GTConsolidateMetaTileEntity.ORE_FACTORY[1])
                .stationResearch(b -> b.researchStack(GTConsolidateMetaTileEntity.ORE_FACTORY[0].getStackForm())
                        .CWUt(144).EUt(VA[ZPM]))
                .duration(60 * 20).EUt(VA[UHV]).buildAndRegister();
    }

    public static void GTFOMultiblock() {
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(MetaTileEntities.HULL[IV])
                .input(ELECTRIC_PUMP_IV, 2)
                .input(circuit, MarkerMaterials.Tier.LuV, 4)
                .input(frameGt, Materials.Steel)
                .input(plate, Materials.VanadiumGallium, 6)
                .scannerResearch(b -> b
                        .researchStack(GTFOTileEntities.GREENHOUSE.getStackForm())
                        .duration(30 * sec).EUt(VA[IV]))
                .output(GTConsolidateMetaTileEntity.LARGE_GREENHOUSE)
                .duration(90 * sec).EUt(VA[IV]).buildAndRegister();
    }

    public static void GTWPMultiblock() {
        RecipeMaps.ASSEMBLY_LINE_RECIPES.recipeBuilder()
                .input(screw, Materials.TungstenCarbide, 4)
                .input(toolHeadBuzzSaw, Materials.TungstenCarbide)
                .input(ELECTRIC_MOTOR_IV, 4)
                .input(MetaTileEntities.HULL[IV])
                .input(circuit, MarkerMaterials.Tier.IV, 4)
                .input(CONVEYOR_MODULE_IV, 2)
                .scannerResearch(b -> b
                        .researchStack(GTWPMetaTileEntities.SAWMILL.getStackForm())
                        .EUt(VA[IV]).duration(30 * sec))
                .output(GTConsolidateMetaTileEntity.PARALLELIZED_SAWMILL)
                .EUt(VA[IV]).duration(90 * sec).buildAndRegister();
    }
}
