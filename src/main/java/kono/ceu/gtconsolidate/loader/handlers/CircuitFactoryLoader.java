package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.*;

import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.metatileentity.multiblock.CleanroomType;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.common.ConfigHolder;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class CircuitFactoryLoader {

    private static final int outputAmount = ConfigHolder.recipes.harderCircuitRecipes ? 1 : 2;

    public static void register() {
        processor();
    }

    public static void processor() {
        // NAND Chip
        addProcessorRecipe(GOOD_CIRCUIT_BOARD, SIMPLE_SYSTEM_ON_CHIP, Materials.RedAlloy, 2, Materials.Tin, 2,
                NAND_CHIP_ULV, outputAmount * 2, MV);
        // Microprocessor
        addProcessorRecipe(PLASTIC_CIRCUIT_BOARD, SYSTEM_ON_CHIP, Materials.Copper, 2, Materials.Tin, 2,
                MICROPROCESSOR_LV, ConfigHolder.recipes.harderCircuitRecipes ? 3 : 6, EV);
        // Integrated Processor
        addProcessorRecipe(PLASTIC_CIRCUIT_BOARD, SYSTEM_ON_CHIP, Materials.RedAlloy, 4, Materials.AnnealedCopper, 4,
                PROCESSOR_MV, outputAmount * 2, IV);
        // Nanoprocessor
        addProcessorRecipe(ADVANCED_CIRCUIT_BOARD, ADVANCED_SYSTEM_ON_CHIP, Materials.Electrum, 4, Materials.Platinum,
                4, NANO_PROCESSOR_HV, outputAmount * 2, LuV);
        // Quantumprocessor
        addProcessorRecipe(EXTREME_CIRCUIT_BOARD, ADVANCED_SYSTEM_ON_CHIP, Materials.Platinum, 12,
                Materials.NiobiumTitanium, 8, QUANTUM_PROCESSOR_EV, outputAmount * 2, ZPM);
        // Crystal Processor
        addProcessorRecipe(ELITE_CIRCUIT_BOARD, CRYSTAL_SYSTEM_ON_CHIP, Materials.NiobiumTitanium, 8,
                Materials.YttriumBariumCuprate, 8, CRYSTAL_PROCESSOR_IV, outputAmount * 2, ZPM);
        // Wetware Processor
        addProcessorRecipe(NEURO_PROCESSOR, HIGHLY_ADVANCED_SOC, Materials.YttriumBariumCuprate, 8, Materials.Naquadah,
                8, WETWARE_PROCESSOR_LUV, outputAmount * 2, UV);
    }

    public static void addProcessorRecipe(MetaItem<?>.MetaValueItem board, MetaItem<?>.MetaValueItem SoCStack,
                                          Material wireFineMaterial, int wireFineAmount, Material boltMaterial,
                                          int boltAmount, MetaItem<?>.MetaValueItem output, int outputAmount,
                                          int tier) {
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(board, 48)
                .input(SoCStack, 48)
                .input(wireFine, wireFineMaterial, wireFineAmount * 48)
                .input(bolt, boltMaterial, boltAmount * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000 * tier))
                .output(output, outputAmount * 64)
                .casingTier(tier)
                .cleanroom(CleanroomType.CLEANROOM)
                .duration(60 * 20 * 5).EUt((int) V[UEV]).buildAndRegister();
    }
}
