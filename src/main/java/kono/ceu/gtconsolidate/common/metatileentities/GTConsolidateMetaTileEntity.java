package kono.ceu.gtconsolidate.common.metatileentities;

import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.modId;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityRotorHolder;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.api.util.Mods;
import kono.ceu.gtconsolidate.common.metatileentities.multi.electric.*;
import kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart.MetaTileEntityFilteredItemBus;
import kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart.MetaTileEntityMoreParallelHatch;
import kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart.MetaTileEntityPowerEnhancedRotorHolder;
import kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart.MetaTileEntitySpeedEnhancedRotorHolder;
import kono.ceu.gtconsolidate.common.metatileentities.multi.primitive.MetaTileEntityIndustrialBrickedBlastFurnace;
import kono.ceu.gtconsolidate.common.metatileentities.multi.primitive.MetaTileEntityIndustrialCokeOven;
import kono.ceu.gtconsolidate.common.metatileentities.multi.tank.*;

public class GTConsolidateMetaTileEntity {

    // Multiblock
    public static final MetaTileEntityParallelizedFusionReactor[] PARALLELIZED_FUSION_REACTOR = new MetaTileEntityParallelizedFusionReactor[3];
    public static final MetaTileEntityParallelizedEBF[] PARALLELIZED_EBF = new MetaTileEntityParallelizedEBF[2];
    public static final MetaTileEntityParallelizedVF[] PARALLELIZED_VF = new MetaTileEntityParallelizedVF[2];
    public static final MetaTileEntityParallelizedAssemblyLine[] PARALLELIZED_ASSEMBLY_LINE = new MetaTileEntityParallelizedAssemblyLine[3];
    public static MetaTileLargeGreenHouse LARGE_GREENHOUSE;
    public static MetaTileEntityComponentAssemblyLine COMPONENT_ASSEMBLY_LINE;
    public static MetaTileEntityMegaFurnace MEGA_FURNACE;
    public static MetaTileEntityParallelizedSawmill PARALLELIZED_SAWMILL;
    public static MetaTileEntityTurboBlastFurnace TURBO_BLAST_FURNACE;
    public static MetaTileEntityGigaVF ABSOLUTE_FREEZER;
    public static MetaTileEntityCircuitFactory CIRCUIT_FACTORY;
    public static MetaTileEntityExtendedProcessingArray[] EXTENDED_PROCESSING_ARRAY = new MetaTileEntityExtendedProcessingArray[3];
    public static MetaTileEntityOreFactory[] ORE_FACTORY = new MetaTileEntityOreFactory[2];
    public static MetaTileEntityMultiblockLargeTank[] MULTIBLOCK_LARGE_TANK = new MetaTileEntityMultiblockLargeTank[10];

    // Primitive
    public static MetaTileEntityIndustrialBrickedBlastFurnace INDUSTRIAL_BBF;
    public static MetaTileEntityIndustrialCokeOven INDUSTRIAL_COKE_OVEN;

    // Multiblock Part
    public static final MetaTileEntityFilteredItemBus[] FILTERED_ITEM_INPUT = new MetaTileEntityFilteredItemBus[GTValues.UHV +
            1];
    public static final MetaTileEntityMoreParallelHatch[] MORE_PARALLEL_HATCHES = new MetaTileEntityMoreParallelHatch[8];
    public static final MetaTileEntityRotorHolder[] ROTOR_HOLDERS_LOW = new MetaTileEntityRotorHolder[2]; // LV and MV
    public static final MetaTileEntityRotorHolder[] ROTOR_HOLDERS_HI = new MetaTileEntityRotorHolder[7]; // UHV - MAX
    public static final MetaTileEntityPowerEnhancedRotorHolder[] ROTOR_HOLDER_POWERED = new MetaTileEntityPowerEnhancedRotorHolder[GTValues.V.length -
            1];
    public static final MetaTileEntitySpeedEnhancedRotorHolder[] ROTOR_HOLDER_SPEEDED = new MetaTileEntitySpeedEnhancedRotorHolder[GTValues.V.length -
            1];
    public static MetaTileEntityAdvancedTankValve ADVANCED_TANK_VALVE;

    public static void init() {
        registerMultiMachine();
        registerMultiblockPart();
    }

    public static void registerMultiMachine() {
        int id = GTConsolidateConfig.id.startMulti;

        // Parallelized Fusion Reactor
        PARALLELIZED_FUSION_REACTOR[0] = registerMetaTileEntity(id, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_luv"), GTValues.UV));
        PARALLELIZED_FUSION_REACTOR[1] = registerMetaTileEntity(id + 1, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_zpm"), GTValues.UHV));
        PARALLELIZED_FUSION_REACTOR[2] = registerMetaTileEntity(id + 2, new MetaTileEntityParallelizedFusionReactor(
                modId("parallelized_fusion_reactor_uv"), GTValues.UEV));
        // Parallelized Electric Blast Furnace
        PARALLELIZED_EBF[0] = registerMetaTileEntity(id + 3, new MetaTileEntityParallelizedEBF(
                modId("advanced_electric_blast_furnace"), 4));
        PARALLELIZED_EBF[1] = registerMetaTileEntity(id + 4, new MetaTileEntityParallelizedEBF(
                modId("elite_electric_blast_furnace"), 16));
        // Parallelized Vacuum Freezer
        PARALLELIZED_VF[0] = registerMetaTileEntity(id + 5, new MetaTileEntityParallelizedVF(
                modId("advanced_vacuum_freezer"), 4));
        PARALLELIZED_VF[1] = registerMetaTileEntity(id + 6, new MetaTileEntityParallelizedVF(
                modId("elite_vacuum_freezer"), 16));
        // Parallelized Assembly Line
        PARALLELIZED_ASSEMBLY_LINE[0] = registerMetaTileEntity(id + 7, new MetaTileEntityParallelizedAssemblyLine(
                modId("parallelized_assembly_line_mk1"), 4));
        PARALLELIZED_ASSEMBLY_LINE[1] = registerMetaTileEntity(id + 8, new MetaTileEntityParallelizedAssemblyLine(
                modId("parallelized_assembly_line_mk2"), 16));
        PARALLELIZED_ASSEMBLY_LINE[2] = registerMetaTileEntity(id + 9, new MetaTileEntityParallelizedAssemblyLine(
                modId("parallelized_assembly_line_mk3"), 64));
        // GTFO
        if (Mods.GregTechFoodOption.isModLoaded()) {
            LARGE_GREENHOUSE = registerMetaTileEntity(id + 10, new MetaTileLargeGreenHouse(
                    modId("large_greenhouse")));
        }
        // CoA
        COMPONENT_ASSEMBLY_LINE = registerMetaTileEntity(id + 11, new MetaTileEntityComponentAssemblyLine(
                modId("component_assembly_line")));
        // Advanced Multi Smelter
        MEGA_FURNACE = registerMetaTileEntity(id + 12, new MetaTileEntityMegaFurnace(
                modId("multi_smelter_advanced")));
        // GTWP
        if (Mods.GTWoodProcessing.isModLoaded()) {
            PARALLELIZED_SAWMILL = registerMetaTileEntity(id + 13, new MetaTileEntityParallelizedSawmill(
                    modId("parallelized_sawmill")));
        }
        // Rotary Hearth Blast Smelter
        TURBO_BLAST_FURNACE = registerMetaTileEntity(id + 14, new MetaTileEntityTurboBlastFurnace(
                modId("turbo_blast_furnace")));
        // Absolute Freezer
        ABSOLUTE_FREEZER = registerMetaTileEntity(id + 15, new MetaTileEntityGigaVF(modId("absolute_freezer")));
        // Circuit Factory
        CIRCUIT_FACTORY = registerMetaTileEntity(id + 16, new MetaTileEntityCircuitFactory(modId("circuit_factory")));
        // Extended Processing Array
        EXTENDED_PROCESSING_ARRAY[0] = registerMetaTileEntity(id + 17, new MetaTileEntityExtendedProcessingArray(
                modId("elite_processing_array"), 0));
        EXTENDED_PROCESSING_ARRAY[1] = registerMetaTileEntity(id + 18, new MetaTileEntityExtendedProcessingArray(
                modId("master_processing_array"), 1));
        EXTENDED_PROCESSING_ARRAY[2] = registerMetaTileEntity(id + 19, new MetaTileEntityExtendedProcessingArray(
                modId("ultimate_processing_array"), 2));
        // Industrial Bricked Blast Furnace
        INDUSTRIAL_BBF = registerMetaTileEntity(id + 20, new MetaTileEntityIndustrialBrickedBlastFurnace(
                modId("industrial_bbf")));
        // Industrial Coke Oven
        INDUSTRIAL_COKE_OVEN = registerMetaTileEntity(id + 21, new MetaTileEntityIndustrialCokeOven(
                modId("industrial_coke_oven")));
        // Ore Factory
        ORE_FACTORY[0] = registerMetaTileEntity(id + 22, new MetaTileEntityOreFactory(
                modId("ore_factory"), false));
        ORE_FACTORY[1] = registerMetaTileEntity(id + 23, new MetaTileEntityOreFactory(
                modId("industrial_ore_factory"), true));
        // Multiblock Large Tank
        for (int i = 0; i < MULTIBLOCK_LARGE_TANK.length; i++) {
            String voltageName = GTValues.VN[i].toLowerCase();
            MULTIBLOCK_LARGE_TANK[i] = registerMetaTileEntity(id + 24 + i, new MetaTileEntityMultiblockLargeTank(
                    modId("multiblock_large_tank." + voltageName), i));
        }
        // next -> id + 34
    }

    public static void registerMultiblockPart() {
        int id = GTConsolidateConfig.id.startMulti + 100;
        // Filtered Input Bus
        for (int i = 0; i < FILTERED_ITEM_INPUT.length; i++) {
            String voltageName = GTValues.VN[i].toLowerCase();
            FILTERED_ITEM_INPUT[i] = registerMetaTileEntity(id + i, new MetaTileEntityFilteredItemBus(
                    modId("filter_input." + voltageName), i));
        }
        id = id + FILTERED_ITEM_INPUT.length;

        // More Parallel Hatch
        if (GTConsolidateConfig.feature.addMoreParallel) {
            for (int i = 0; i < MORE_PARALLEL_HATCHES.length; i++) {
                String name = GTValues.VN[i + 1].toLowerCase();
                MORE_PARALLEL_HATCHES[i] = registerMetaTileEntity(id + i, new MetaTileEntityMoreParallelHatch(
                        modId("more_parallel_hatch." + name), i + 1));
            }
        }
        id = id + MORE_PARALLEL_HATCHES.length;

        // Rotor Holders
        boolean addLowTier = GTConsolidateConfig.feature.addLowTierRotorHolders;
        boolean addHighTier = GTConsolidateConfig.feature.addHighTierRotorHolders;
        // LV and MV Rotor Holder
        if (addLowTier) {
            for (int i = 0; i < ROTOR_HOLDERS_LOW.length; i++) {
                ROTOR_HOLDERS_LOW[i] = registerMetaTileEntity(id + i,
                        new MetaTileEntityRotorHolder(modId("rotor_holder." + GTValues.VN[1 + i].toLowerCase()),
                                1 + i));
            }
        }
        id = id + 2;

        // UHV+ Rotor Holders
        if (addHighTier) {
            ROTOR_HOLDERS_HI[0] = registerMetaTileEntity(id, new MetaTileEntityRotorHolder(
                    modId("rotor_holder.uhv"), GTValues.UHV));
            if (GregTechAPI.isHighTier()) {
                for (int i = 1; i < ROTOR_HOLDERS_HI.length - 1; i++) {
                    ROTOR_HOLDERS_HI[i] = registerMetaTileEntity(id + i,
                            new MetaTileEntityRotorHolder(modId("rotor_holder." + GTValues.VN[i + 9].toLowerCase()),
                                    9 + i));
                }
            }
        }
        id = id + ROTOR_HOLDERS_HI.length - 1;

        int start = addLowTier ? GTValues.LV - 1 : GTValues.HV - 1;
        int end = addHighTier ? GregTechAPI.isHighTier() ? GTValues.MAX : GTValues.UHV : GTValues.UV;
        // Power Enhanced Rotor Holders
        if (GTConsolidateConfig.feature.addPowerEnhancedRotorHolders) {
            for (int i = start; i < end; i++) {
                ROTOR_HOLDER_POWERED[i] = registerMetaTileEntity(id + i,
                        new MetaTileEntityPowerEnhancedRotorHolder(
                                modId("power_enhanced_rotor_holder." + GTValues.VN[i + 1].toLowerCase()), i + 1));
            }
        }
        id = id + ROTOR_HOLDER_POWERED.length;

        // Speed Enhanced Rotor Holders
        if (GTConsolidateConfig.feature.addSpeedEnhancedRotorHolders) {
            for (int i = start; i < end; i++) {
                ROTOR_HOLDER_SPEEDED[i] = registerMetaTileEntity(id + i,
                        new MetaTileEntitySpeedEnhancedRotorHolder(
                                modId("speed_enhanced_rotor_holder." + GTValues.VN[i + 1].toLowerCase()), i + 1));
            }
        }

        id = id + ROTOR_HOLDER_SPEEDED.length;
        ADVANCED_TANK_VALVE = registerMetaTileEntity(id + 1, new MetaTileEntityAdvancedTankValve(
                modId("advanced_tank_valve")));
    }
}
