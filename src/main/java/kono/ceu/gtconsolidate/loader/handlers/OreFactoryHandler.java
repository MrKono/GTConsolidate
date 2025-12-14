package kono.ceu.gtconsolidate.loader.handlers;

import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.properties.OreProperty;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gregtech.api.unification.material.info.MaterialFlags.HIGH_SIFTER_OUTPUT;

public class OreFactoryHandler {

    public static void init() {
        OrePrefix.ore.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
        OrePrefix.oreEndstone.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
        OrePrefix.oreNetherrack.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
        if (ConfigHolder.worldgen.allUniqueStoneTypes) {
            OrePrefix.oreGranite.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreDiorite.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreAndesite.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreBasalt.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreBlackgranite.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreMarble.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreRedgranite.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreSand.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
            OrePrefix.oreRedSand.addProcessingHandler(PropertyKey.ORE, OreFactoryHandler::registerOreFactoryProcess);
        }
    }

    public static void registerOreFactoryProcess(OrePrefix orePrefix, @NotNull Material material,
                                                 @NotNull OreProperty property) {
        registerProcess1(orePrefix, material, property);
        registerProcess2(orePrefix, material, property);
        registerProcess3(orePrefix, material, property);
        registerProcess4(orePrefix, material, property);
        registerProcess5(orePrefix, material, property);
        registerProcess6(orePrefix, material, property);
        registerProcess7(orePrefix, material, property);
        registerProcess8(orePrefix, material, property);
        registerProcess9(orePrefix, material, property);
        registerProcess10(orePrefix, material, property);
        registerProcess11(orePrefix, material, property);
    }

    // Macerate -> Ore Washer -> Macerate -> Centrifuge
    public static void registerProcess1(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        int amount = 2 * outputAmount(orePrefix, property);
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(1)
                .duration(15 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(Materials.DistilledWater.getFluid(100 * amount))
                .chancedOutput(GTUtility.copy(amount, washingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, purifiedCrushingByproduct(material, property)), 1400, 850)
                .chancedOutput(GTUtility.copy(amount, pureByproduct(material, property)), 1111, 0)
                .buildAndRegister();
    }

    // Macerate -> Ore Washer -> Thermal Centrifuge -> Macerate
    public static void registerProcess2(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        ItemStack crushedCentrifugedStack = OreDictUnifier.get(OrePrefix.crushedCentrifuged, material);
        if (crushedCentrifugedStack.isEmpty()) return;
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        int amount = 2 * outputAmount(orePrefix, property);
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(2)
                .duration(30 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(Materials.DistilledWater.getFluid(100 * amount))
                .chancedOutput(GTUtility.copy(amount, washingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, purifiedCentrifugingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, centrifugedCrushingByproduct(material, property)), 1400, 850)
                .buildAndRegister();
    }

    // Macerate -> Ore Washer -> Sifter -> Centrifuge
    public static void registerProcess3(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        int amount = 2 * outputAmount(orePrefix, property);
        if (!material.hasProperty(PropertyKey.GEM)) return;
        ItemStack exquisiteStack = OreDictUnifier.get(OrePrefix.gemExquisite, material);
        ItemStack flawlessStack = OreDictUnifier.get(OrePrefix.gemFlawless, material);
        ItemStack gemStack = OreDictUnifier.get(OrePrefix.gem, material);
        ItemStack flawedStack = OreDictUnifier.get(OrePrefix.gemFlawed, material);
        ItemStack chippedStack = OreDictUnifier.get(OrePrefix.gemChipped, material);

        exquisiteStack.setCount(amount);
        flawlessStack.setCount(amount);
        gemStack.setCount(amount);
        flawedStack.setCount(amount);
        chippedStack.setCount(amount);

        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(3)
                .duration(25 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(Materials.DistilledWater.getFluid(100 * amount))
                .chancedOutput(GTUtility.copy(amount, washingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, output), 833, 167);
        // Shifting
        if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
            builder.chancedOutput(exquisiteStack, 500, 150)
                    .chancedOutput(flawlessStack, 1500, 200)
                    .chancedOutput(gemStack, 5000, 1000);
            if (!flawedStack.isEmpty())
                builder.chancedOutput(flawedStack, 2000, 500);
            if (!chippedStack.isEmpty())
                builder.chancedOutput(chippedStack, 3000, 350);
        } else {
            builder.chancedOutput(exquisiteStack, 300, 100)
                    .chancedOutput(flawlessStack, 1000, 150)
                    .chancedOutput(gemStack, 3500, 500);
            if (!flawedStack.isEmpty())
                builder.chancedOutput(flawedStack, 2500, 300);
            if (!exquisiteStack.isEmpty())
                builder.chancedOutput(chippedStack, 3500, 400);
        }
        builder.buildAndRegister();
    }

    // Macerate -> Macerate -> Centrifuge
    public static void registerProcess4(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        int amount = 2 * outputAmount(orePrefix, property);
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(4)
                .duration(10 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.chancedOutput(GTUtility.copy(amount, crushedCrushingByproduct(material, property)), 1400, 850)
                .chancedOutput(GTUtility.copy(amount, impureByproduct(material, property)), 1111, 0)
                .buildAndRegister();
    }

    // Macerate -> Thermal Centrifuge -> Macerate
    public static void registerProcess5(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        int amount = 2 * outputAmount(orePrefix, property);
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(5)
                .duration(25 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.chancedOutput(GTUtility.copy(amount, crushedCentrifugingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, crushedCrushingByproduct(material, property)), 1400, 850)
                .buildAndRegister();
    }

    // Macerate -> Chemical Bathing -> Macerate -> Centrifuge
    public static void registerProcess6(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        if (property.getWashedIn().getKey() == null) return;
        Pair<Material, Integer> washedInTuple = property.getWashedIn();
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        int amount = 2 * outputAmount(orePrefix, property);
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(6)
                .duration(17 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(washedInTuple.getKey().getFluid(washedInTuple.getValue() * amount))
                .chancedOutput(GTUtility.copy(amount, bathingByproduct(material, property)), 7000, 580)
                .chancedOutput(GTUtility.copy(amount, purifiedCrushingByproduct(material, property)), 1400, 850)
                .chancedOutput(GTUtility.copy(amount, pureByproduct(material, property)), 1111, 0)
                .buildAndRegister();
    }

    // Macerate -> Chemical Bathing -> Thermal Centrifuge -> Macerate
    public static void registerProcess7(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        if (property.getWashedIn().getKey() == null) return;
        Pair<Material, Integer> washedInTuple = property.getWashedIn();
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        int amount = 2 * outputAmount(orePrefix, property);
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(7)
                .duration(27 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(washedInTuple.getKey().getFluid(washedInTuple.getValue() * amount))
                .chancedOutput(GTUtility.copy(amount, bathingByproduct(material, property)), 7000, 580)
                .chancedOutput(GTUtility.copy(amount, purifiedCentrifugingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, centrifugedCrushingByproduct(material, property)), 1400, 850)
                .buildAndRegister();
    }

    // Macerate -> Ore Washer -> Sifter -> Centrifuge / Macerate (All Gems)
    public static void registerProcess8(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        int amount = 2 * outputAmount(orePrefix, property);
        if (!material.hasProperty(PropertyKey.GEM)) return;
        ItemStack exquisiteStack = OreDictUnifier.get(OrePrefix.dust, material);
        ItemStack flawlessStack = OreDictUnifier.get(OrePrefix.dust, material);
        ItemStack gemStack = OreDictUnifier.get(OrePrefix.dust, material);
        ItemStack flawedStack = OreDictUnifier.get(OrePrefix.dustSmall, material);
        ItemStack chippedStack = OreDictUnifier.get(OrePrefix.dustSmall, material);

        exquisiteStack.setCount(amount * 4);
        flawlessStack.setCount(amount * 2);
        gemStack.setCount(amount);
        flawedStack.setCount(amount * 2);
        chippedStack.setCount(amount);

        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(8)
                .duration(30 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(Materials.DistilledWater.getFluid(100 * amount))
                .chancedOutput(GTUtility.copy(amount, washingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, output), 833, 167);
        // Shifting
        if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
            builder.chancedOutput(exquisiteStack, 500, 150)
                    .chancedOutput(flawlessStack, 1500, 200)
                    .chancedOutput(gemStack, 5000, 1000);
            if (!flawedStack.isEmpty())
                builder.chancedOutput(flawedStack, 2000, 500);
            if (!chippedStack.isEmpty())
                builder.chancedOutput(chippedStack, 3000, 350);
        } else {
            builder.chancedOutput(exquisiteStack, 300, 100)
                    .chancedOutput(flawlessStack, 1000, 150)
                    .chancedOutput(gemStack, 3500, 500);
            if (!flawedStack.isEmpty())
                builder.chancedOutput(flawedStack, 2500, 300);
            if (!exquisiteStack.isEmpty())
                builder.chancedOutput(chippedStack, 3500, 400);
        }
        builder.buildAndRegister();
    }

    // Macerate -> Ore Washer -> Sifter -> Centrifuge / Macerate (Flawed and Chipped)
    public static void registerProcess9(OrePrefix orePrefix, @NotNull Material material,
                                        @NotNull OreProperty property) {
        int amount = 2 * outputAmount(orePrefix, property);
        if (!material.hasProperty(PropertyKey.GEM)) return;
        ItemStack exquisiteStack = OreDictUnifier.get(OrePrefix.gemExquisite, material);
        ItemStack flawlessStack = OreDictUnifier.get(OrePrefix.gemFlawless, material);
        ItemStack gemStack = OreDictUnifier.get(OrePrefix.gem, material);
        ItemStack flawedStack = OreDictUnifier.get(OrePrefix.dustSmall, material);
        ItemStack chippedStack = OreDictUnifier.get(OrePrefix.dustSmall, material);

        exquisiteStack.setCount(amount);
        flawlessStack.setCount(amount);
        gemStack.setCount(amount);
        flawedStack.setCount(amount * 2);
        chippedStack.setCount(amount);

        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(9)
                .duration(30 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(Materials.DistilledWater.getFluid(100 * amount))
                .chancedOutput(GTUtility.copy(amount, washingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, output), 833, 167);
        // Shifting
        if (material.hasFlag(HIGH_SIFTER_OUTPUT)) {
            builder.chancedOutput(exquisiteStack, 500, 150)
                    .chancedOutput(flawlessStack, 1500, 200)
                    .chancedOutput(gemStack, 5000, 1000);
            if (!flawedStack.isEmpty())
                builder.chancedOutput(flawedStack, 2000, 500);
            if (!chippedStack.isEmpty())
                builder.chancedOutput(chippedStack, 3000, 350);
        } else {
            builder.chancedOutput(exquisiteStack, 300, 100)
                    .chancedOutput(flawlessStack, 1000, 150)
                    .chancedOutput(gemStack, 3500, 500);
            if (!flawedStack.isEmpty())
                builder.chancedOutput(flawedStack, 2500, 300);
            if (!exquisiteStack.isEmpty())
                builder.chancedOutput(chippedStack, 3500, 400);
        }
        builder.buildAndRegister();
    }

    // Macerate -> Ore Washer -> Macerate -> Electromagnetic Separating
    public static void registerProcess10(OrePrefix orePrefix, @NotNull Material material,
                                         @NotNull OreProperty property) {
        if (property.getSeparatedInto() == null || property.getSeparatedInto().isEmpty()) return;
        int amount = 2 * outputAmount(orePrefix, property);
        List<Material> separatedMaterial = property.getSeparatedInto();
        ItemStack separateStack = OreDictUnifier.get(OrePrefix.dust, separatedMaterial.get(0));
        OrePrefix prefix = (separatedMaterial.get(separatedMaterial.size() - 1).getBlastTemperature() == 0 &&
                separatedMaterial.get(separatedMaterial.size() - 1).hasProperty(PropertyKey.INGOT)) ?
                OrePrefix.nugget : OrePrefix.dust;
        ItemStack separatedStack2 = OreDictUnifier.get(prefix, separatedMaterial.get(separatedMaterial.size() - 1),
                prefix == OrePrefix.nugget ? 2 : 1);
        separateStack.setCount(amount);
        separatedStack2.setCount(amount);
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(10)
                .duration(25 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(Materials.DistilledWater.getFluid(100 * amount))
                .chancedOutput(GTUtility.copy(amount, washingByproduct(material, property)), 3333, 0)
                .chancedOutput(GTUtility.copy(amount, purifiedCrushingByproduct(material, property)), 1400, 850)
                .chancedOutput(separateStack, 1000, 250)
                .chancedOutput(separatedStack2, prefix == OrePrefix.dust ? 500 : 2000,
                        prefix == OrePrefix.dust ? 150 : 600)
                .buildAndRegister();
    }

    // Macerate -> Chemical Bathing -> Macerate -> Electromagnetic Separating
    public static void registerProcess11(OrePrefix orePrefix, @NotNull Material material,
                                         @NotNull OreProperty property) {
        if (property.getWashedIn().getKey() == null) return;
        Pair<Material, Integer> washedInTuple = property.getWashedIn();
        if (property.getSeparatedInto() == null || property.getSeparatedInto().isEmpty()) return;
        int amount = 2 * outputAmount(orePrefix, property);
        List<Material> separatedMaterial = property.getSeparatedInto();
        ItemStack separateStack = OreDictUnifier.get(OrePrefix.dust, separatedMaterial.get(0));
        OrePrefix prefix = (separatedMaterial.get(separatedMaterial.size() - 1).getBlastTemperature() == 0 &&
                separatedMaterial.get(separatedMaterial.size() - 1).hasProperty(PropertyKey.INGOT)) ?
                OrePrefix.nugget : OrePrefix.dust;
        ItemStack separatedStack2 = OreDictUnifier.get(prefix, separatedMaterial.get(separatedMaterial.size() - 1),
                prefix == OrePrefix.nugget ? 2 : 1);
        separateStack.setCount(amount);
        separatedStack2.setCount(amount);
        ItemStack output = OreDictUnifier.get(OrePrefix.dust, material);
        if (output.isEmpty()) {
            // fallback for reduced & cleanGravel
            output = GTUtility.copyFirst(
                    OreDictUnifier.get(OrePrefix.reduced, material),
                    OreDictUnifier.get(OrePrefix.cleanGravel, material));
        }
        RecipeBuilder<?> builder = GTConsolidateRecipeMaps.ORE_FACTORY_RECIPES.recipeBuilder()
                .input(orePrefix, material)
                .outputs(GTUtility.copy(amount, output))
                .chancedOutput(crushingByproduct(material, property), 1400, 850)
                .fluidInputs(Materials.Lubricant.getFluid(10))
                .circuitMeta(11)
                .duration(27 * 20);
        for (MaterialStack secondaryMaterial : orePrefix.secondaryMaterials) {
            if (secondaryMaterial.material.hasProperty(PropertyKey.DUST)) {
                ItemStack dustStack = OreDictUnifier.getGem(secondaryMaterial);
                builder.chancedOutput(dustStack, 6700, 800);
            }
        }
        builder.fluidInputs(washedInTuple.getKey().getFluid(washedInTuple.getValue() * amount))
                .chancedOutput(GTUtility.copy(amount, bathingByproduct(material, property)), 7000, 580)
                .chancedOutput(GTUtility.copy(amount, purifiedCrushingByproduct(material, property)), 1400, 850)
                .chancedOutput(separateStack, 1000, 250)
                .chancedOutput(separatedStack2, prefix == OrePrefix.dust ? 500 : 2000,
                        prefix == OrePrefix.dust ? 150 : 600)
                .buildAndRegister();
    }

    public static int oreTypeMultiplier(OrePrefix prefix) {
        return prefix == OrePrefix.oreNetherrack || prefix == OrePrefix.oreEndstone ? 2 : 1;
    }

    public static int outputAmount(OrePrefix prefix, @NotNull OreProperty property) {
        double amountOfCrushedOre = property.getOreMultiplier();
        return (int) Math.ceil(amountOfCrushedOre) * oreTypeMultiplier(prefix);
    }

    /**
     * @return byproduct of ore -> crushed
     */
    public static ItemStack crushingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(0, material);
        ItemStack byproductStack = OreDictUnifier.get(OrePrefix.gem, byproductMaterial);
        if (byproductStack.isEmpty()) byproductStack = OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
        return byproductStack;
    }

    /**
     * @return byproduct of crushed -> crushedPurified (Ore Washer)
     */
    public static ItemStack washingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(0, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of crushed -> crushedPurified (Chemical Bath)
     */
    public static ItemStack bathingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(3, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of crushedPurified -> crushedCentrifuged
     */
    public static ItemStack purifiedCentrifugingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(1, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of crushed -> crushedCentrifuged
     */
    public static ItemStack crushedCentrifugingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(1, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of crushed -> dustImpure
     */
    public static ItemStack crushedCrushingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(0, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of crushedPurified -> dustPure
     */
    public static ItemStack purifiedCrushingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(1, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of crushedCentrifuged -> dust
     */
    public static ItemStack centrifugedCrushingByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(2, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of dustImpure -> dust
     */
    public static ItemStack impureByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(0, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }

    /**
     * @return byproduct of dustPure -> dust
     */
    public static ItemStack pureByproduct(@NotNull Material material, @NotNull OreProperty property) {
        Material byproductMaterial = property.getOreByProduct(1, material);
        return OreDictUnifier.get(OrePrefix.dust, byproductMaterial);
    }
}
