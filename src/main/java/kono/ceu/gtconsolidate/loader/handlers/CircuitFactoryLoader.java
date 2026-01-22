package kono.ceu.gtconsolidate.loader.handlers;

import static gregtech.api.GTValues.*;
import static gregtech.api.unification.ore.OrePrefix.*;
import static gregtech.common.items.MetaItems.*;

import gregtech.api.GregTechAPI;
import gregtech.api.items.metaitem.MetaItem;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.common.ConfigHolder;

import kono.ceu.gtconsolidate.GTConsolidateConfig;
import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;

public class CircuitFactoryLoader {

    private static final int outputAmount = ConfigHolder.recipes.harderCircuitRecipes ? 1 : 2;
    private static final int voltage = GregTechAPI.isHighTier() ? UEV : UHV;
    private static final int factor = GregTechAPI.isHighTier() ? 1 : 4;

    public static void register() {
        processor();
        if (GTConsolidateConfig.mode.generateLowTierCircuitRecipe) {
            lowTier();
        }
    }

    public static void processor() {
        // NAND Chip
        addProcessorRecipe(GOOD_CIRCUIT_BOARD, SIMPLE_SYSTEM_ON_CHIP, Materials.Tin, 2, Materials.RedAlloy, 2,
                NAND_CHIP_ULV, outputAmount * 4, MV, 300);
        addProcessorRecipe(PLASTIC_CIRCUIT_BOARD, SIMPLE_SYSTEM_ON_CHIP, Materials.Tin, 2, Materials.RedAlloy, 2,
                NAND_CHIP_ULV, outputAmount * 6, MV, 300);
        // Microprocessor
        addProcessorRecipe(PLASTIC_CIRCUIT_BOARD, SYSTEM_ON_CHIP, Materials.Copper, 2, Materials.Tin, 2,
                MICROPROCESSOR_LV, outputAmount * 3, EV, 50);
        // Integrated Processor
        addProcessorRecipe(PLASTIC_CIRCUIT_BOARD, SYSTEM_ON_CHIP, Materials.RedAlloy, 4, Materials.AnnealedCopper, 4,
                PROCESSOR_MV, outputAmount * 2, IV, 50);
        // Nanoprocessor
        addProcessorRecipe(ADVANCED_CIRCUIT_BOARD, ADVANCED_SYSTEM_ON_CHIP, Materials.Electrum, 4, Materials.Platinum,
                4, NANO_PROCESSOR_HV, outputAmount * 2, LuV, 50);
        // Quantumprocessor
        addProcessorRecipe(EXTREME_CIRCUIT_BOARD, ADVANCED_SYSTEM_ON_CHIP, Materials.Platinum, 12,
                Materials.NiobiumTitanium, 8, QUANTUM_PROCESSOR_EV, outputAmount * 2, ZPM, 50);
        // Crystal Processor
        addProcessorRecipe(ELITE_CIRCUIT_BOARD, CRYSTAL_SYSTEM_ON_CHIP, Materials.NiobiumTitanium, 8,
                Materials.YttriumBariumCuprate, 8, CRYSTAL_PROCESSOR_IV, outputAmount * 2, ZPM, 100);
        // Wetware Processor
        addProcessorRecipe(NEURO_PROCESSOR, HIGHLY_ADVANCED_SOC, Materials.YttriumBariumCuprate, 8, Materials.Naquadah,
                8, WETWARE_PROCESSOR_LUV, outputAmount * 2, UV, 100);
    }

    public static void lowTier() {
        // Electronic Circuit
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(BASIC_CIRCUIT_BOARD, 48)
                .input(component, MarkerMaterials.Component.Resistor, 2 * 48)
                .input(wireGtSingle, Materials.RedAlloy, 2 * 48)
                .input(circuit, MarkerMaterials.Tier.ULV, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(ELECTRONIC_CIRCUIT_LV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(200 * 640 * factor).buildAndRegister();
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(BASIC_CIRCUIT_BOARD, 48)
                .input(ADVANCED_SMD_RESISTOR, 48)
                .input(wireGtSingle, Materials.RedAlloy, 2 * 48)
                .input(circuit, MarkerMaterials.Tier.ULV, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(ELECTRONIC_CIRCUIT_LV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(200 * 640 * factor).buildAndRegister();

        // Good Electronic Circuit
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(GOOD_CIRCUIT_BOARD, 48)
                .input(circuit, MarkerMaterials.Tier.LV, 2 * 48)
                .input(component, MarkerMaterials.Component.Diode, 2 * 48)
                .input(wireGtSingle, Materials.Copper, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(ELECTRONIC_CIRCUIT_MV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(300 * 64 * factor).buildAndRegister();
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(GOOD_CIRCUIT_BOARD, 48)
                .input(circuit, MarkerMaterials.Tier.LV, 2 * 48)
                .input(ADVANCED_SMD_DIODE, 48)
                .input(wireGtSingle, Materials.Copper, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(ELECTRONIC_CIRCUIT_MV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(300 * 64 * factor).buildAndRegister();

        // Integrated Logic Circuit
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(BASIC_CIRCUIT_BOARD, 48)
                .input(INTEGRATED_LOGIC_CIRCUIT, 48)
                .input(component, MarkerMaterials.Component.Resistor, 2 * 48)
                .input(component, MarkerMaterials.Component.Diode, 2 * 48)
                .input(wireFine, Materials.Copper, 2 * 48)
                .input(bolt, Materials.Tin, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(INTEGRATED_CIRCUIT_LV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(200 * 64 * factor).buildAndRegister();
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(BASIC_CIRCUIT_BOARD, 48)
                .input(INTEGRATED_LOGIC_CIRCUIT, 48)
                .input(ADVANCED_SMD_RESISTOR, 48)
                .input(ADVANCED_SMD_DIODE, 48)
                .input(wireFine, Materials.Copper, 2 * 48)
                .input(bolt, Materials.Tin, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(INTEGRATED_CIRCUIT_LV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(200 * 64 * factor).buildAndRegister();

        // Good Integrated Circuit
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(GOOD_CIRCUIT_BOARD, 48)
                .input(INTEGRATED_LOGIC_CIRCUIT, 48)
                .input(component, MarkerMaterials.Component.Resistor, 2 * 48)
                .input(component, MarkerMaterials.Component.Diode, 2 * 48)
                .input(wireFine, Materials.Gold, 2 * 48)
                .input(bolt, Materials.Silver, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(INTEGRATED_CIRCUIT_MV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(400 * 64 * factor).buildAndRegister();
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(GOOD_CIRCUIT_BOARD, 48)
                .input(INTEGRATED_LOGIC_CIRCUIT, 48)
                .input(ADVANCED_SMD_RESISTOR, 48)
                .input(ADVANCED_SMD_DIODE, 48)
                .input(wireFine, Materials.Gold, 2 * 48)
                .input(bolt, Materials.Silver, 2 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(INTEGRATED_CIRCUIT_MV, outputAmount * 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(400 * 64 * factor).buildAndRegister();

        // Advanced Integrated Circuit
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(INTEGRATED_CIRCUIT_MV, outputAmount * 48)
                .input(INTEGRATED_LOGIC_CIRCUIT, 2 * 48)
                .input(RANDOM_ACCESS_MEMORY, 2 * 48)
                .input(component, MarkerMaterials.Component.Transistor, 4 * 48)
                .input(wireFine, Materials.Electrum, 8 * 48)
                .input(bolt, Materials.AnnealedCopper, 8 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(INTEGRATED_CIRCUIT_HV, 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(800 * 64 * factor)
                .buildAndRegister();
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(INTEGRATED_CIRCUIT_MV, outputAmount * 48)
                .input(INTEGRATED_LOGIC_CIRCUIT, 2 * 48)
                .input(RANDOM_ACCESS_MEMORY, 2 * 48)
                .input(ADVANCED_SMD_TRANSISTOR, 2 * 48)
                .input(wireFine, Materials.Electrum, 8 * 48)
                .input(bolt, Materials.AnnealedCopper, 8 * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000))
                .output(INTEGRATED_CIRCUIT_HV, 64)
                .casingTier(LV)
                .EUt((int) V[EV]).duration(800 * 64 * factor)
                .buildAndRegister();
    }

    public static void addProcessorRecipe(MetaItem<?>.MetaValueItem board, MetaItem<?>.MetaValueItem SoCStack,
                                          Material wireFineMaterial, int wireFineAmount, Material boltMaterial,
                                          int boltAmount, MetaItem<?>.MetaValueItem output, int outputAmount,
                                          int tier, int duration) {
        int voltageTier = Math.min(tier < IV ? tier + 3 : tier + 2, voltage);
        // x48 inputs, x64 outputs (3/4)
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(board, 48)
                .input(SoCStack, 48)
                .input(wireFine, wireFineMaterial, wireFineAmount * 48)
                .input(bolt, boltMaterial, boltAmount * 48)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 48))
                .fluidInputs(Materials.DistilledWater.getFluid(1000 * tier))
                .output(output, outputAmount * 64)
                .casingTier(tier)
                .duration(duration * 10 * factor).EUt((int) V[voltageTier]).buildAndRegister();

        // x80 inputs, x128 outputs (5/8)
        // 80x wireFine -> 5x wireGtQuadruple
        // 80x bolts -> 10x ingots
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(board, 80)
                .input(SoCStack, 80)
                .input(wireGtQuadruple, wireFineMaterial, wireFineAmount * 5)
                .input(ingot, boltMaterial, boltAmount * 10)
                .fluidInputs(Materials.SolderingAlloy.getFluid(72 * 80))
                .fluidInputs(Materials.DistilledWater.getFluid(5000 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(tier))
                .output(output, outputAmount * 128)
                .casingTier(tier)
                .duration(duration * 30 * factor).EUt((int) V[voltageTier + 1]).buildAndRegister();

        // x144 inputs, x256 outputs (9/16)
        // 144x bolts -> 2x blocks
        // 144x wireFine -> 2x wireGtHex + 1x wireGtQuadruple
        GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES.recipeBuilder()
                .input(board, 144)
                .input(SoCStack, 144)
                .input(wireGtHex, wireFineMaterial, wireFineAmount * 2)
                .input(wireGtQuadruple, wireFineMaterial, wireFineAmount)
                .input(block, boltMaterial, boltAmount * 2)
                .fluidInputs(Materials.SolderingAlloy.getFluid(144 * 72))
                .fluidInputs(Materials.DistilledWater.getFluid(10000 * tier))
                .fluidInputs(Materials.Lubricant.getFluid(500 * tier))
                .fluidInputs(Materials.Mutagen.getFluid(1000))
                .output(output, outputAmount * 256)
                .casingTier(tier)
                .duration(duration * 45 * factor).EUt((int) V[voltageTier + 2]).buildAndRegister();
    }
}
