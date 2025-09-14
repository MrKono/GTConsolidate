package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.energyHatchLimit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;

import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;
import kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class MetaTileEntityGigaVF extends GCYMRecipeMapMultiblockController {

    public MetaTileEntityGigaVF(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.ABSOLUTE_VACUUM_RECIPE);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityGigaVF(metaTileEntityId);
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        List<IEnergyContainer> inputEnergy = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputEnergy.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
        this.energyContainer = new EnergyContainerList(inputEnergy);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("KKK#XXXXXXX#KKK", "KVK#XXXXXXX#KVK", "KVK#XXXXXXX#KVK", "KVK#XXXXXXX#KVK", "KVK#XXXXXXX#KVK",
                        "KVK#XXXXXXX#KVK", "KKK#XXXXXXX#KKK")
                .aisle("KVK#XXXXXXX#KVK", "VPPPPPPPPPPPPPV", "VPV#XPAPAPX#VPV", "VPPPPPPPPPPPPPV", "VPV#XPAPAPX#VPV",
                        "VPPPPPPPPPPPPPV", "KVK#XXXXXXX#KVK")
                .aisle("KVK#XXXXXXX#KVK", "VPV#XPAPAPX#VPV", "VPV#XAAAAAX#VPV", "VPV#XPAAAPX#VPV", "VPV#XAAAAAX#VPV",
                        "VPV#XPAPAPX#VPV", "KVK#XXXXXXX#KVK")
                .aisle("KVK#XXXXXXX#KVK", "VPPPPPAPAPPPPPV", "VPV#XAAAAAX#VPV", "VPPPPPAAAPPPPPV", "VPV#XAAAAAX#VPV",
                        "VPPPPPAPAPPPPPV", "KVK#XXXXXXX#KVK")
                .aisle("KKK#XXXXXXX#KKK", "KVK#XPPPPPX#KVK", "KVK#XPAAAPX#KVK", "KVK#XPAAAPX#KVK", "KVK#XPAAAPX#KVK",
                        "KVK#XPPPPPX#KVK", "KKK#XXXXXXX#KKK")
                .aisle("#####XXSXX#####", "#####XGGGX#####", "#####XGGGX#####", "#####XGGGX#####", "#####XGGGX#####",
                        "#####XGGGX#####", "#####XXXXX#####")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(140)
                        .or(autoAbilities(false, true, true, true, true, true, false))
                        .or(energyHatchLimit(true, true, true, true)))
                .where('G', states(getGlassState()))
                .where('K', states(getCasingState2()))
                .where('V', states(getVentState()))
                .where('P', states(getPipeState()))
                .where('A', states(getCoolantState()))
                .where('#', any())
                .build();
    }

    private static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.ALUMINIUM_FROSTPROOF);
    }

    private static IBlockState getGlassState() {
        return GTConsolidateMetaBlocks.COOLANT_CASING.getState(BlockCoolantCasing.CasingType.CRYSTAL_QUARTZ_GLASS);
    }

    private static IBlockState getCasingState2() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN);
    }

    private static IBlockState getVentState() {
        return GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT);
    }

    private static IBlockState getPipeState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
    }

    private static IBlockState getCoolantState() {
        return GTConsolidateMetaBlocks.COOLANT_CASING.getState(BlockCoolantCasing.CasingType.HELIUM_3);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.FROST_PROOF_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return GCYMTextures.MEGA_VACUUM_FREEZER_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public boolean isTiered() {
        return false;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()));
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        builder.addWorkingStatusLine();
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.absolute_freezer.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.multiblock.accept_64a"));
    }
}
