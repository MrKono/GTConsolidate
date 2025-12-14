package kono.ceu.gtconsolidate.loader;

import static gregtech.api.GTValues.*;

import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.stack.ItemMaterialInfo;
import gregtech.api.unification.stack.MaterialStack;
import gregtech.common.ConfigHolder;

import kono.ceu.gtconsolidate.common.blocks.BlockGearBoxCasing;
import kono.ceu.gtconsolidate.common.blocks.BlockMultiblockCasing;
import kono.ceu.gtconsolidate.common.blocks.BlockPipeCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class GTConsolidateMiscLoader {

    public static void init() {
        materials();
        materialInfo();
    }

    public static void materials() {
        RecipeMaps.VACUUM_RECIPES.recipeBuilder()
                .fluidInputs(Materials.Helium3.getFluid(FluidStorageKeys.GAS, 1000))
                .fluidInputs(Materials.Helium.getFluid(FluidStorageKeys.LIQUID, 100))
                .fluidOutputs(Materials.Helium3.getFluid(FluidStorageKeys.LIQUID, 1000))
                .duration(10 * 20).EUt(VA[ZPM]).buildAndRegister();
    }

    public static void materialInfo() {
        int casingAmount = ConfigHolder.recipes.casingsPerCraft;

        // Iridium Plated Factory Casing (casing/config + 1 ingots)
        OreDictUnifier.registerOre(
                GTConsolidateMetaBlocks.MULTIBLOCK_CASING
                        .getItemVariant(BlockMultiblockCasing.MultiblockCasingType.IRIDIUM_PLATED),
                new ItemMaterialInfo(
                        new MaterialStack(Materials.StainlessSteel, (M * 8) / casingAmount),
                        new MaterialStack(Materials.Iridium, M)));
        // Americium Plated Factory Casing (casing/config + Ir * 1 + Am * 1)
        OreDictUnifier.registerOre(
                GTConsolidateMetaBlocks.MULTIBLOCK_CASING
                        .getItemVariant(BlockMultiblockCasing.MultiblockCasingType.AMERICIUM_PLATED),
                new ItemMaterialInfo(
                        new MaterialStack(Materials.StainlessSteel, (M * 8) / casingAmount),
                        new MaterialStack(Materials.Iridium, M),
                        new MaterialStack(Materials.Americium, M)));
        // Gear Box ((2 plates + 4 gears + frame) / config)
        OreDictUnifier.registerOre(
                GTConsolidateMetaBlocks.GEARBOX_CASING.getItemVariant(BlockGearBoxCasing.CasingType.IRIDIUM),
                new ItemMaterialInfo(
                        new MaterialStack(Materials.Iridium, ((M * 4) + (M * 4) * 2 + (M * 2)) / casingAmount)));
        OreDictUnifier.registerOre(
                GTConsolidateMetaBlocks.GEARBOX_CASING.getItemVariant(BlockGearBoxCasing.CasingType.AMERICIUM),
                new ItemMaterialInfo(
                        new MaterialStack(Materials.Americium, ((M * 4) + (M * 4) * 2 + (M * 2)) / casingAmount)));
        // Pipe Casing ((4 plates + 4 pipeNormals + frame) / config)
        OreDictUnifier.registerOre(
                GTConsolidateMetaBlocks.PIPE_CASING.getItemVariant(BlockPipeCasing.CasingType.IRIDIUM),
                new ItemMaterialInfo(
                        new MaterialStack(Materials.Iridium, ((M * 4) + (M * 3) * 4 + (M * 2)) / casingAmount)));
        OreDictUnifier.registerOre(
                GTConsolidateMetaBlocks.PIPE_CASING.getItemVariant(BlockPipeCasing.CasingType.AMERICIUM),
                new ItemMaterialInfo(
                        new MaterialStack(Materials.Americium, ((M * 4) + (M * 3) * 4 + (M * 2)) / casingAmount)));
    }
}
