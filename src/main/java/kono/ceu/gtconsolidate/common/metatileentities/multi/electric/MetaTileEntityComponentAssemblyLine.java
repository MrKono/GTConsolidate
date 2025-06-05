package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.*;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import gregtech.api.GTValues;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.*;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.*;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import kono.ceu.gtconsolidate.common.blocks.BlockCoACasing;

public class MetaTileEntityComponentAssemblyLine extends RecipeMapMultiblockController {

    private int workTier;

    public MetaTileEntityComponentAssemblyLine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.COA_RECIPES);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity mte) {
        return new MetaTileEntityComponentAssemblyLine(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("iAAAIAAAi", "A##F#F##A", "A##OOO##A", "A##OOO##A", "A#######A", "A#######A", "AA#MMM#AA",
                        "$AAMMMAA$", "$$$MMM$$$", "$$$$$$$$$")
                .aisle("iAAAIAAAi", "G#######G", "G##AAA##G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(3)
                .aisle("iAAAIAAAi", "GP#F#F#PG", "GP#AAA#PG", "GP#AAA#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("iAAAIAAAi", "G#######G", "G##AAA##G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("iAAAIAAAi", "AP#F#F#PA", "AP#AAA#PA", "AP#AAA#PA", "AP#####PA", "AP#####PA", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("iAAAIAAAi", "G#######G", "G##AAA##G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("iAAAIAAAi", "GP#F#F#PG", "GP#AAA#PG", "GP#AAA#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("iAAAIAAAi", "G#######G", "G##AAA##G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(3)
                .aisle("iAAAIAAAi", "A##F#F##A", "A##AAA##A", "A##AAA##A", "A#######A", "A#######A", "AA#MMM#AA",
                        "$AAMSMAA$", "$$$MMM$$$", "$$$$$$$$$")
                .where('A', states(getCasingState1()))
                .where('B',
                        states(MetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_LINE_CASING)))
                .where('C', states(MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING)))
                .where('E',
                        abilities(MultiblockAbility.INPUT_ENERGY).setMinGlobalLimited(1).setMaxGlobalLimited(3)
                                .or(states(getCasingState1())))
                .where('F', frames(Materials.TungstenSteel))
                .where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS)))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS))
                .where('i',
                        abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(8)
                                .or(states(getCasingState1())))
                .where('M', autoAbilities(true, false).or(states(getCasingState1())))
                .where('O',
                        abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).or(states(getCasingState1())))
                .where('P',
                        states(MetaBlocks.BOILER_CASING
                                .getState(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE)))
                .where('S', selfPredicate())
                .where('T', CoATieredCasing())
                .where('#', air())
                .where('$', any())
                .build();
    }

    public IBlockState getCasingState1() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object type = context.get("CasingTier");
        if (type instanceof BlockCoACasing.CoACasingType) {
            this.workTier = ((BlockCoACasing.CoACasingType) type).ordinal();
        } else {
            this.workTier = BlockCoACasing.CoACasingType.ULV.ordinal();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GCYMTextures.ASSEMBLING_CASING;
    }

    public int getWorkTier() {
        return this.workTier;
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
                        ITextComponent tierString = TextComponentUtil.stringWithColor(
                                TextFormatting.RED,
                                TextFormattingUtil.formatNumbers(getWorkTier()) + TextFormatting.GRAY + " (" +
                                        GTValues.VNF[getWorkTier()] + TextFormatting.GRAY + ")");

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.coa.recipe_tier",
                                tierString));
                    }
                })
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgressPercent());
    }

    @NotNull
    @Override
    public List<ITextComponent> getDataInfo() {
        List<ITextComponent> list = super.getDataInfo();
        list.add(new TextComponentTranslation("gtconsolidate.multiblock.coa.recipe_tier",
                new TextComponentTranslation(TextFormattingUtil.formatNumbers(getWorkTier()))
                        .setStyle(new Style().setColor(TextFormatting.RED))));
        return list;
    }
}
