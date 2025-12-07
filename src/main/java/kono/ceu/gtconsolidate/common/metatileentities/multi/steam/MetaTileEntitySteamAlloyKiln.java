package kono.ceu.gtconsolidate.common.metatileentities.multi.steam;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.SteamMultiWorkable;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapSteamMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.particle.VanillaParticleEffects;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;

import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;

public class MetaTileEntitySteamAlloyKiln extends RecipeMapSteamMultiblockController {

    private static final int MAX_PARALLELS = 8;

    public MetaTileEntitySteamAlloyKiln(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.ALLOY_SMELTER_RECIPES, CONVERSION_RATE);
        this.recipeMapWorkable = new ExtendedSteamMultiWorkable(this, CONVERSION_RATE);
        this.recipeMapWorkable.setParallelLimit(MAX_PARALLELS);
        this.recipeMapWorkable.setEUDiscount(1.5);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySteamAlloyKiln(metaTileEntityId);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXX", "CCC", "CCC")
                .aisle("XXX", "C#C", "CCC")
                .aisle("XXX", "CSC", "CCC")
                .where('S', selfPredicate())
                .where('X', states(getFireboxState())
                        .or(autoAbilities(true, false, false, false, false).setMinGlobalLimited(1)
                                .setMaxGlobalLimited(3)))
                .where('C', states(getCasingState()).setMinGlobalLimited(6)
                        .or(autoAbilities(false, false, true, true, false)))
                .where('#', any())
                .build();
    }

    public IBlockState getCasingState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID) :
                MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    public IBlockState getFireboxState() {
        return ConfigHolder.machines.steelSteamMultiblocks ?
                MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.STEEL_FIREBOX) :
                MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX);
    }

    private boolean isFireboxPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() < getPos().getY());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (ConfigHolder.machines.steelSteamMultiblocks) {
            if (sourcePart != null && isFireboxPart(sourcePart)) {
                return lastActive ? Textures.STEEL_FIREBOX_ACTIVE : Textures.STEEL_FIREBOX;
            }
            return Textures.SOLID_STEEL_CASING;

        } else {
            if (sourcePart != null && isFireboxPart(sourcePart)) {
                return lastActive ? Textures.BRONZE_FIREBOX_ACTIVE : Textures.BRONZE_FIREBOX;
            }
            return Textures.BRONZE_PLATED_BRICKS;
        }
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ALLOY_SMELTER_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public int getItemOutputLimit() {
        return 1;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive());
        builder.addCustom(tl -> {
            // custom steam tank line
            IFluidTank steamFluidTank = recipeMapWorkable.getSteamFluidTankCombined();
            if (steamFluidTank != null && steamFluidTank.getCapacity() > 0) {
                String stored = TextFormattingUtil.formatNumbers(steamFluidTank.getFluidAmount());
                String capacity = TextFormattingUtil.formatNumbers(steamFluidTank.getCapacity());

                ITextComponent steamInfo = TextComponentUtil.stringWithColor(
                        TextFormatting.BLUE,
                        stored + " / " + capacity + " L");

                tl.add(TextComponentUtil.translationWithColor(
                        TextFormatting.GRAY,
                        "gregtech.multiblock.steam.steam_stored",
                        steamInfo));
            }
        });
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        builder.addWorkingStatusLine();
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.multiblock.steam_.duration_modifier"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.parallel", MAX_PARALLELS));
        tooltip.add(TooltipHelper.BLINKING_ORANGE + I18n.format("gregtech.multiblock.require_steam_parts"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick() {
        if (isActive()) {
            VanillaParticleEffects.defaultFrontEffect(this, EnumParticleTypes.SMOKE_LARGE, EnumParticleTypes.FLAME);
            if (GTValues.RNG.nextBoolean()) {
                VanillaParticleEffects.defaultFrontEffect(this, 0.5F, EnumParticleTypes.SMOKE_NORMAL);
            }
        }
    }

    public static class ExtendedSteamMultiWorkable extends SteamMultiWorkable {

        public ExtendedSteamMultiWorkable(RecipeMapSteamMultiblockController tileEntity, double conversionRate) {
            super(tileEntity, conversionRate);
        }

        @Override
        public @NotNull ParallelLogicType getParallelLogicType() {
            return ParallelLogicType.MULTIPLY;
        }

        /*
         * @Override
         * public long getMaxVoltage() {
         * return GTValues.HV;
         * }
         * 
         * @Override
         * protected long getMaxParallelVoltage() {
         * return GTValues.HV;
         * }
         */

        @Override
        public void applyParallelBonus(@NotNull RecipeBuilder<?> builder) {
            int currentRecipeEU = builder.getEUt();
            int currentRecipeDuration = builder.getDuration() / getParallelLimit();
            builder.EUt((int) Math.min(32.0, Math.ceil(currentRecipeEU * 1.33)))
                    .duration((int) (currentRecipeDuration * 1.5));
        }
    }
}
