package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static gregtech.api.recipes.logic.OverclockingLogic.heatingCoilOverclockingLogic;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.energyHatchLimit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
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
import kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;

public class MetaTileEntityTurboBlastFurnace extends GCYMRecipeMapMultiblockController implements IHeatingCoil {

    private int blastFurnaceTemperature;
    private int baseTemperature;
    private int defaultTemperature;

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
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(tl -> {
                    // Coil heat capacity line
                    if (isStructureFormed()) {
                        ITextComponent heatString = TextComponentUtil.stringWithColor(
                                TextFormatting.RED,
                                TextFormattingUtil.formatNumbers(blastFurnaceTemperature) + "K");

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.blast_furnace.max_temperature",
                                heatString));
                    }
                })
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgressPercent());
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
        this.blastFurnaceTemperature = this.defaultTemperature;
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
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.1"));
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.3"));
        tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.3"));
        tooltip.add(I18n.format("gtconsolidate.machine.turbo_blast_furnace.tooltip.4"));
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
        int bounce = baseTemperature / 100;
        if (!getWorld().isRemote && getOffsetTimer() % 20 == 0L) {
            if (isActive()) {
                long maxTemperature = Integer.MAX_VALUE;
                this.blastFurnaceTemperature = (int) Math.min(maxTemperature,
                        (long) this.blastFurnaceTemperature + bounce);
            } else {
                this.blastFurnaceTemperature = Math.max(this.defaultTemperature,
                        this.blastFurnaceTemperature - bounce * 10);
            }
        }
    }

    @Override
    public int getCurrentTemperature() {
        return this.blastFurnaceTemperature;
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
