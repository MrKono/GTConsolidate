package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static gregtech.api.recipes.logic.OverclockingLogic.heatingCoilOverclockingLogic;
import static gregtech.client.utils.TooltipHelper.isCtrlDown;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.energyHatchLimit;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateUtil.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.GTValues;
import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IHeatingCoil;
import gregtech.api.capability.IMufflerHatch;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.ImageCycleButtonWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.recipes.recipeproperties.TemperatureProperty;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.*;

import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;
import kono.ceu.gtconsolidate.client.GTConsolidateTextures;
import kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class MetaTileEntityTurboBlastFurnace extends GCYMRecipeMapMultiblockController implements IHeatingCoil {

    private int baseTemperature;
    private int defaultTemperature;
    private int blastFurnaceTemperature = defaultTemperature;
    private int initialTemperature;
    private boolean preHeating = false;
    private final long preHeatingCost = GTValues.V[GTValues.UV];
    private boolean hasEnoughEnergy;

    public MetaTileEntityTurboBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.TURBO_BLAST_RECIPE);
        this.recipeMapWorkable = new GigaBlastFurnaceRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityTurboBlastFurnace(metaTileEntityId);
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addEnergyUsageExactLine(recipeMapWorkable.getInfoProviderEUt())
                .addCustom(tl -> {
                    // Coil heat capacity line
                    if (isStructureFormed()) {
                        ITextComponent heatString = TextComponentUtil.stringWithColor(
                                TextFormatting.RED,
                                TextFormattingUtil.formatNumbers(blastFurnaceTemperature) + "K");
                        ITextComponent body = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.blast_furnace.max_temperature",
                                heatString);

                        ITextComponent initialHeatString = TextComponentUtil.stringWithColor(
                                TextFormatting.RED,
                                TextFormattingUtil.formatNumbers(initialTemperature) + "K");
                        ITextComponent hover1 = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.initial_temperature",
                                initialHeatString);

                        ITextComponent tempIncreaseString = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                "+" + TextFormattingUtil.formatNumbers(getBonus() * 5) + "K/s");
                        ITextComponent hover_active = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.temperature_change",
                                tempIncreaseString);
                        ITextComponent tempDecreaseString = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                "-" + TextFormattingUtil.formatNumbers(getBonus() * 10) + "K/s");
                        ITextComponent hover_not_active = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.temperature_change",
                                tempDecreaseString);

                        ITextComponent hoverString = recipeMapWorkable.isActive() && isActive() ?
                                hover1.appendText("\n").appendSibling(hover_active) :
                                preHeating ? hover1 : hover1.appendText("\n").appendSibling(hover_not_active);
                        tl.add(TextComponentUtil.setHover(body, hoverString));
                    }
                });
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        builder.addWorkingStatusLine()
                .addCustom(tl -> {
                    if (!isActive()) {
                        ITextComponent status = TextComponentUtil.translationWithColor(
                                preHeating ? TextFormatting.GREEN : TextFormatting.RED,
                                preHeating ? "gtconsolidate.universal.enabled" : "gtconsolidate.universal.disabled");

                        ITextComponent body = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.pre_heating", status);

                        ITextComponent heatChangeString = TextComponentUtil.stringWithColor(
                                TextFormatting.RED,
                                TextFormattingUtil.formatNumbers(getBonus()) + "K");
                        ITextComponent hover = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.pre_heating.hover", heatChangeString);

                        tl.add(TextComponentUtil.setHover(body, hover));
                    }
                });
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object type = context.get("CoilType");
        if (type instanceof IHeatingCoilBlockStats stats) {
            this.defaultTemperature = stats.getCoilTemperature();
        } else {
            this.defaultTemperature = BlockWireCoil.CoilType.CUPRONICKEL.getCoilTemperature();
        }
        this.baseTemperature = this.defaultTemperature;
        this.defaultTemperature += 100 *
                Math.max(0, GTUtility.getTierByVoltage(getEnergyContainer().getInputVoltage()) - GTValues.MV);
        this.initialTemperature = this.defaultTemperature;
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.blastFurnaceTemperature = 0;
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe, boolean consumeIfSuccess) {
        return this.blastFurnaceTemperature >= recipe.getProperty(TemperatureProperty.getInstance(), 0);
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
        TraceabilityPredicate casing = states(getCasingState()).setMinGlobalLimited(300);
        return FactoryBlockPattern.start()
                .aisle("##XXXXXXXXX##", "##XXXXXXXXX##", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "##TTTTTTTTT##")
                .aisle("#XXXXXXXXXXX#", "#XXXXXXXXXXX#", "###FJJJJJF###", "###FJJJJJF###", "###FFVVVFF###",
                        "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###",
                        "###FFVVVFF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###",
                        "###FGGGGGF###", "###FFVVVFF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###",
                        "###FGGGGGF###", "###FGGGGGF###", "###FFVVVFF###", "###FJJJJJF###", "###FJJJJJF###",
                        "#TTTTTTTTTTT#")
                .aisle("XXXXXXXXXXXXX", "XXXXVVVVVXXXX", "##F#######F##", "##F#######F##", "##FFFHPHFFF##",
                        "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##FFFHPHFFF##", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##F#######F##", "##FFFHPHFFF##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##F#######F##", "##F#######F##", "##FFFHPHFFF##", "##F#######F##", "##F#######F##",
                        "TTTTTTTTTTTTT")
                .aisle("XXXXXXXXXXXXX", "XXXXXXDXXXXXX", "#F####P####F#", "#F####P####F#", "#FFHHHPHHHFF#",
                        "#F####P####F#", "#F####P####F#", "#F####P####F#", "#F####P####F#", "#F####P####F#",
                        "#FFHHHPHHHFF#", "#F####P####F#", "#F####P####F#", "#F####P####F#", "#F####P####F#",
                        "#F####P####F#", "#FFHHHPHHHFF#", "#F####P####F#", "#F####P####F#", "#F####P####F#",
                        "#F####P####F#", "#F####P####F#", "#FFHHHPHHHFF#", "#F####P####F#", "#F####P####F#",
                        "TTTTTTPTTTTTT")
                .aisle("XXXXXXXXXXXXX", "XXVXXXDXXXVXX", "#J##BBPBB##J#", "#J##TITIT##J#", "#FFHJJJJJHFF#",
                        "#G##BITIB##G#", "#G##CCCCC##G#", "#G##CCCCC##G#", "#G##CCCCC##G#", "#G##BITIB##G#",
                        "#FFHHHHHHHFF#", "#G##BITIB##G#", "#G##CCCCC##G#", "#G##CCCCC##G#", "#G##CCCCC##G#",
                        "#G##BITIB##G#", "#FFHHHHHHHFF#", "#G##BITIB##G#", "#G##CCCCC##G#", "#G##CCCCC##G#",
                        "#G##CCCCC##G#", "#G##BITIB##G#", "#FFHJJJJJHFF#", "#J##TITIT##J#", "#J##BBPBB##J#",
                        "TTTTTTPTTTTTT")
                .aisle("XXXXXXXXXXXXX", "XXVXXXDXXXVXX", "#J##BAAAB##J#", "#J##IAAAI##J#", "#VHHJAAAJHHV#",
                        "#G##IAAAI##G#", "#G##CAAAC##G#", "#G##CAAAC##G#", "#G##CAAAC##G#", "#G##IAAAI##G#",
                        "#VHHHAAAHHHV#", "#G##IAAAI##G#", "#G##CAAAC##G#", "#G##CAAAC##G#", "#G##CAAAC##G#",
                        "#G##IAAAI##G#", "#VHHHAAAHHHV#", "#G##IAAAI##G#", "#G##CAAAC##G#", "#G##CAAAC##G#",
                        "#G##CAAAC##G#", "#G##IAAAI##G#", "#VHHJAAAJHHV#", "#J##IAAAI##J#", "#J##BAAAB##J#",
                        "TTTTTMPMTTTTT")
                .aisle("XXXXXXXXXXXXX", "XXVDDDDDDDVXX", "#J#PPADAPP#J#", "#J#PTADATP#J#", "#VPPJADAJPPV#",
                        "#G#PTADATP#G#", "#G#PCADACP#G#", "#G#PCADACP#G#", "#G#PCADACP#G#", "#G#PTADATP#G#",
                        "#VPPHADAHPPV#", "#G#PTADATP#G#", "#G#PCADACP#G#", "#G#PCADACP#G#", "#G#PCADACP#G#",
                        "#G#PTADATP#G#", "#VPPHADAHPPV#", "#G#PTADATP#G#", "#G#PCADACP#G#", "#G#PCADACP#G#",
                        "#G#PCADACP#G#", "#G#PTADATP#G#", "#VPPJADAJPPV#", "#J#PTADATP#J#", "#J#PPADAPP#J#",
                        "TTTPPPMPPPTTT")
                .aisle("XXXXXXXXXXXXX", "XXVXXXDXXXVXX", "#J##BAAAB##J#", "#J##IAAAI##J#", "#VHHJAAAJHHV#",
                        "#G##IAAAI##G#", "#G##CAAAC##G#", "#G##CAAAC##G#", "#G##CAAAC##G#", "#G##IAAAI##G#",
                        "#VHHHAAAHHHV#", "#G##IAAAI##G#", "#G##CAAAC##G#", "#G##CAAAC##G#", "#G##CAAAC##G#",
                        "#G##IAAAI##G#", "#VHHHAAAHHHV#", "#G##IAAAI##G#", "#G##CAAAC##G#", "#G##CAAAC##G#",
                        "#G##CAAAC##G#", "#G##IAAAI##G#", "#VHHJAAAJHHV#", "#J##IAAAI##J#", "#J##BAAAB##J#",
                        "TTTTTMPMTTTTT")
                .aisle("XXXXXXXXXXXXX", "XXVXXXDXXXVXX", "#J##BBPBB##J#", "#J##TITIT##J#", "#FFHJJJJJHFF#",
                        "#G##BITIB##G#", "#G##CCCCC##G#", "#G##CCCCC##G#", "#G##CCCCC##G#", "#G##BITIB##G#",
                        "#FFHHHHHHHFF#", "#G##BITIB##G#", "#G##CCCCC##G#", "#G##CCCCC##G#", "#G##CCCCC##G#",
                        "#G##BITIB##G#", "#FFHHHHHHHFF#", "#G##BITIB##G#", "#G##CCCCC##G#", "#G##CCCCC##G#",
                        "#G##CCCCC##G#", "#G##BITIB##G#", "#FFHJJJJJHFF#", "#J##TITIT##J#", "#J##BBPBB##J#",
                        "TTTTTTPTTTTTT")
                .aisle("XXXXXXXXXXXXX", "XXXXXXDXXXXXX", "#F####P####F#", "#F####P####F#", "#FFHHHPHHHFF#",
                        "#F####P####F#", "#F####P####F#", "#F####P####F#", "#F####P####F#", "#F####P####F#",
                        "#FFHHHPHHHFF#", "#F####P####F#", "#F####P####F#", "#F####P####F#", "#F####P####F#",
                        "#F####P####F#", "#FFHHHPHHHFF#", "#F####P####F#", "#F####P####F#", "#F####P####F#",
                        "#F####P####F#", "#F####P####F#", "#FFHHHPHHHFF#", "#F####P####F#", "#F####P####F#",
                        "TTTTTTPTTTTTT")
                .aisle("XXXXXXXXXXXXX", "XXXXVVVVVXXXX", "##F#######F##", "##F#######F##", "##FFFHPHFFF##",
                        "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##FFFHPHFFF##", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##F#######F##", "##FFFHPHFFF##", "##F#######F##", "##F#######F##", "##F#######F##",
                        "##F#######F##", "##F#######F##", "##FFFHPHFFF##", "##F#######F##", "##F#######F##",
                        "TTTTTTTTTTTTT")
                .aisle("#XXXXXXXXXXX#", "#XXXXXXXXXXX#", "###FJJJJJF###", "###FJJJJJF###", "###FFVVVFF###",
                        "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###",
                        "###FFVVVFF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###",
                        "###FGGGGGF###", "###FFVVVFF###", "###FGGGGGF###", "###FGGGGGF###", "###FGGGGGF###",
                        "###FGGGGGF###", "###FGGGGGF###", "###FFVVVFF###", "###FJJJJJF###", "###FJJJJJF###",
                        "#TTTTTTTTTTT#")
                .aisle("##XXXXXXXXX##", "##XXXXSXXXX##", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "#############", "#############", "#############", "#############", "#############",
                        "##TTTTTTTTT##")
                .where('S', selfPredicate())
                .where('X', casing.or(autoAbilities(false, true, true, true, true, true, false))
                        .or(energyHatchLimit(true, true, true, true)))
                .where('F', frames(Materials.NaquadahAlloy))
                .where('H', casing)
                .where('P', states(getPipeState()))
                .where('B', states(getFireboxState()))
                .where('I', states(getIntakeState()))
                .where('T', states(getCasingState2()))
                .where('V', states(getVentState()))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('C', heatingCoils())
                .where('D', states(getFusionCoil()))
                .where('G', states(getGlassState()))
                .where('J', states(getCoolantState()))
                .where('A', air())
                .where('#', any())
                .build();
    }

    private static IBlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                .getState(BlockLargeMultiblockCasing.CasingType.HIGH_TEMPERATURE_CASING);
    }

    private static IBlockState getCasingState2() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.TUNGSTENSTEEL_ROBUST);
    }

    private static IBlockState getFireboxState() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.TUNGSTENSTEEL_FIREBOX);
    }

    private static IBlockState getIntakeState() {
        return MetaBlocks.MULTIBLOCK_CASING
                .getState(BlockMultiblockCasing.MultiblockCasingType.EXTREME_ENGINE_INTAKE_CASING);
    }

    private static IBlockState getPipeState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
    }

    private static IBlockState getVentState() {
        return GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.HEAT_VENT);
    }

    private static IBlockState getFusionCoil() {
        return MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
    }

    private static IBlockState getGlassState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS);
    }

    private static IBlockState getCoolantState() {
        return GTConsolidateMetaBlocks.COOLANT_CASING.getState(BlockCoolantCasing.CasingType.HELIUM_3);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.3"));
        if (isCtrlDown()) {
            tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.1"));
            tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.2"));
            tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.3"));
        } else {
            tooltip.add((I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.ctrl")));
        }
        if (isAltDown()) {
            tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.5"));
            tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.6"));
        } else {
            tooltip.add((I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.alt")));
        }
        tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.4"));
        tooltip.add(I18n.format("gtconsolidate.multiblock.accept_64a"));
        if (isTABDown()) {
            tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.pattern_info.not_shown"));
            tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.pattern_info.not_shown.detail",
                    I18n.format("tile.boiler_casing.tungstensteel_firebox.name"), 48));
            tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.pattern_info.not_shown.detail",
                    I18n.format("tile.boiler_casing.tungstensteel_pipe.name"), 128));
        } else {
            tooltip.add((I18n.format("gtconsolidate.multiblock.tooltip.universal.tab.build")));
        }
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return iMultiblockPart instanceof IMufflerHatch ? Textures.ROBUST_TUNGSTENSTEEL_CASING :
                GCYMTextures.BLAST_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return GCYMTextures.MEGA_BLAST_FURNACE_OVERLAY;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
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
    protected void updateFormedValid() {
        super.updateFormedValid();
        int bounce = getBonus();
        long maxTemperature = Integer.MAX_VALUE;
        if (!getWorld().isRemote) {
            if (isActive() && getOffsetTimer() % 20 == 0L) {
                if (blastFurnaceTemperature < maxTemperature) {
                    blastFurnaceTemperature += bounce * 5;
                }
            } else {
                if (preHeating && blastFurnaceTemperature < maxTemperature && getOffsetTimer() % 20 == 0L) {
                    hasEnoughEnergy = drainEnergy();
                    if (getOffsetTimer() % 100 == 0L) {
                        if (drainEnergy()) {
                            blastFurnaceTemperature += bounce;
                        } else {
                            blastFurnaceTemperature -= bounce;
                        }
                    }
                } else {
                    if (getOffsetTimer() % 20 == 0L) {
                        this.blastFurnaceTemperature = Math.max(this.defaultTemperature,
                                this.blastFurnaceTemperature - bounce * 10);
                    }
                }
            }
        }
        setTemperatureBonus(initialTemperature, blastFurnaceTemperature);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setInteger("temp", blastFurnaceTemperature);
        data.setInteger("initialTemp", initialTemperature);
        data.setBoolean("preHeating", preHeating);
        data.setBoolean("hasEnoughEnergy", hasEnoughEnergy);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        blastFurnaceTemperature = data.getInteger("temp");
        initialTemperature = data.getInteger("initialTemp");
        preHeating = data.getBoolean("preHeating");
        hasEnoughEnergy = data.getBoolean("hasEnoughEnergy");
        super.readFromNBT(data);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(blastFurnaceTemperature);
        buf.writeInt(initialTemperature);
        buf.writeBoolean(preHeating);
        buf.writeBoolean(hasEnoughEnergy);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        blastFurnaceTemperature = buf.readInt();
        initialTemperature = buf.readInt();
        preHeating = buf.readBoolean();
        hasEnoughEnergy = buf.readBoolean();
    }

    @Override
    public int getCurrentTemperature() {
        return blastFurnaceTemperature;
    }

    private void setTemperatureBonus(int baseTemp, int currentTemp) {
        double ratio = (double) currentTemp / baseTemp;

        // For every doubling, multiply duration by 0.95.
        double durationBonus = Math.pow(0.9, (int) Math.floor(Math.log(ratio) / Math.log(2)));
        // For every 5 times, multiply EUt by 0.5
        double eutBonus = Math.pow(0.5, (int) Math.floor(Math.log(ratio) / Math.log(5)));

        recipeMapWorkable.setSpeedBonus(durationBonus);
        recipeMapWorkable.setEUDiscount(eutBonus);
    }

    private int getBonus() {
        return baseTemperature / 100;
    }

    private boolean drainEnergy() {
        if (energyContainer.getEnergyStored() >= preHeatingCost) {
            energyContainer.removeEnergy(preHeatingCost * 20);
            return true;
        }
        return false;
    }

    public int getPreHeatingMode() {
        return preHeating ? 0 : 1;
    }

    public void setPreHeatingMode(int mode) {
        preHeating = mode == 0;
    }

    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        return (new ImageCycleButtonWidget(x, y, width, height, GTConsolidateTextures.BUTTON_PRE_HEATING, 2,
                this::getPreHeatingMode, this::setPreHeatingMode)).setTooltipHoverString((mode) -> {
                    String tooltip = switch (mode) {
                        case 0 -> "gtconsolidate.machine.turbo_blast_furnace.pre_heating.off";
                        case 1 -> "gtconsolidate.machine.turbo_blast_furnace.pre_heating.on";
                        default -> "";
                    };

                    return tooltip;
                });
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class GigaBlastFurnaceRecipeLogic extends GCYMMultiblockRecipeLogic {

        public GigaBlastFurnaceRecipeLogic(RecipeMapMultiblockController metaTileEntity) {
            super(metaTileEntity);
        }

        @Override
        protected int @NotNull [] runOverclockingLogic(@NotNull IRecipePropertyStorage propertyStorage, int recipeEUt,
                                                       long maxVoltage, int duration, int maxOverclocks) {
            return heatingCoilOverclockingLogic(Math.abs(recipeEUt),
                    maxVoltage,
                    duration,
                    maxOverclocks,
                    ((IHeatingCoil) metaTileEntity).getCurrentTemperature(),
                    propertyStorage.getRecipePropertyValue(TemperatureProperty.getInstance(), 0));
        }
    }
}
