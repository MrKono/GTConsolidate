package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.machines.RecipeMapFurnace;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;

import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;

import kono.ceu.gtconsolidate.api.util.mixinhelper.AbstractRecipeLogicMixinHelper;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;
import kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class MetaTileEntityMegaFurnace extends RecipeMapMultiblockController {

    protected int heatingCoilLevel;
    protected int heatingCoilDiscount;

    public MetaTileEntityMegaFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.FURNACE_RECIPES);
        this.recipeMapWorkable = new MegaSmelterWorkable(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMegaFurnace(metaTileEntityId);
    }

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXX", "GGGGG", "GGGGG", "GGGGG", "XXXXX")
                .aisle("XXXXX", "GCCCG", "GCCCG", "GCCCG", "XXXXX")
                .aisle("XXXXX", "GC#CG", "GC#CG", "GC#CG", "XXMXX")
                .aisle("XXXXX", "GCCCG", "GCCCG", "GCCCG", "XXXXX")
                .aisle("XXSXX", "GGGGG", "GGGGG", "GGGGG", "XXXXX")
                .where('S', selfPredicate())
                .where('X',
                        states(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.INVAR_HEATPROOF))
                                .setMinGlobalLimited(25)
                                .or(autoAbilities(true, true, true, true, true, true, false)))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('C', heatingCoils())
                .where('G',
                        states(GTConsolidateMetaBlocks.COOLANT_CASING
                                .getState(BlockCoolantCasing.CasingType.CRYSTAL_QUARTZ_GLASS)))
                .where('#', air())
                .build();
    }

    public int getParallelFactor() {
        return this.getAbilities(GCYMMultiblockAbility.PARALLEL_HATCH).isEmpty() ? 1 :
                this.getAbilities(GCYMMultiblockAbility.PARALLEL_HATCH).get(0).getCurrentParallel();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(recipeMapWorkable.getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        // Heating coil discount
                        if (heatingCoilDiscount > 1) {
                            ITextComponent coilDiscount = TextComponentUtil.stringWithColor(
                                    TextFormatting.AQUA,
                                    TextFormattingUtil.formatNumbers(100.0 / heatingCoilDiscount) + "%");

                            ITextComponent base = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gregtech.multiblock.multi_furnace.heating_coil_discount",
                                    coilDiscount);

                            ITextComponent hoverText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gregtech.multiblock.multi_furnace.heating_coil_discount_hover");

                            TextComponentUtil.setHover(base, hoverText);
                            tl.add(base);
                        }

                        // Custom parallels line so we can have a hover text
                        if (recipeMapWorkable.getParallelLimit() > 1) {
                            ITextComponent parallels = TextComponentUtil.stringWithColor(
                                    TextFormatting.DARK_PURPLE,
                                    TextFormattingUtil.formatNumbers(recipeMapWorkable.getParallelLimit()));
                            ITextComponent bodyText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gregtech.multiblock.parallel",
                                    parallels);
                            ITextComponent hoverText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gregtech.multiblock.multi_furnace.parallel_hover");
                            tl.add(TextComponentUtil.setHover(bodyText, hoverText));
                        }
                    }
                })
                .addWorkingStatusLine();
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable.getProgress(),
                recipeMapWorkable.getMaxProgress(), recipeMapWorkable.getProgressPercent());
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable.getPreviousRecipe(),
                ((AbstractRecipeLogicMixinHelper) recipeMapWorkable).getCurrentParallel());
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object coilType = context.get("CoilType");
        if (coilType instanceof IHeatingCoilBlockStats) {
            this.heatingCoilLevel = ((IHeatingCoilBlockStats) coilType).getLevel();
            this.heatingCoilDiscount = ((IHeatingCoilBlockStats) coilType).getEnergyDiscount();
        } else {
            this.heatingCoilLevel = BlockWireCoil.CoilType.CUPRONICKEL.getLevel();
            this.heatingCoilDiscount = BlockWireCoil.CoilType.CUPRONICKEL.getEnergyDiscount();
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.heatingCoilLevel = 0;
        this.heatingCoilDiscount = 0;
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn,
                                               boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut,
                                               boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkEnergyIn, checkMaintenance, checkItemIn,
                checkItemOut, checkFluidIn, checkFluidOut, checkMuffler);
        predicate = predicate
                .or(abilities(GCYMMultiblockAbility.PARALLEL_HATCH).setMaxGlobalLimited(1).setPreviewCount(1));
        return predicate;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.HEAT_PROOF_CASING;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.MULTI_FURNACE_OVERLAY;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    public static int getEUtForParallel(int parallel, int discount) {
        return RecipeMapFurnace.RECIPE_EUT * Math.max(1, (parallel / 8) / discount);
    }

    public static int getMaxParallel(int heatingCoilLevel) {
        return 32 * heatingCoilLevel;
    }

    public static int getDurationForParallel(int parallel, int parallelLimit) {
        return (int) Math.max(1.0, RecipeMapFurnace.RECIPE_DURATION * 2 * parallel / Math.max(1, parallelLimit * 1.0));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.mega_furnace.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.machine.mega_furnace.tooltip.2"));
    }

    protected class MegaSmelterWorkable extends MultiblockRecipeLogic {

        public MegaSmelterWorkable(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @NotNull
        @Override
        public ParallelLogicType getParallelLogicType() {
            return ParallelLogicType.APPEND_ITEMS;
        }

        @Override
        public void applyParallelBonus(@NotNull RecipeBuilder<?> builder) {
            builder.EUt(getEUtForParallel(builder.getParallel(), heatingCoilDiscount))
                    .duration(getDurationForParallel(builder.getParallel(), getParallelLimit()));
        }

        @Override
        public int getParallelLimit() {
            return getMaxParallel(heatingCoilLevel) * getParallelFactor();
        }
    }
}
