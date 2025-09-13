package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.GTValues;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.*;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;

import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

import kono.ceu.gtconsolidate.api.recipes.GTConsolidateRecipeMaps;
import kono.ceu.gtconsolidate.api.recipes.properties.CoAProperty;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;
import kono.ceu.gtconsolidate.common.blocks.BlockCoACasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;
import kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity;

public class MetaTileEntityComponentAssemblyLine extends RecipeMapMultiblockController {

    private int workTier;

    public MetaTileEntityComponentAssemblyLine(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.COA_RECIPES);
        this.recipeMapWorkable = new CoALRecipeLogic(this);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity mte) {
        return new MetaTileEntityComponentAssemblyLine(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("IIIIIIIII", "A##F#F##A", "A##OOO##A", "A##OOO##A", "A#######A", "A#######A", "AA#MMM#AA",
                        "$AAMMMAA$", "$$$MMM$$$", "$$$$$$$$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(3)
                .aisle("IIIIIIIII", "GP#F#F#PG", "GP#AAA#PG", "GP#AAA#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("IIIIIIIII", "AP#F#F#PA", "AP#AAA#PA", "AP#AAA#PA", "AP#####PA", "AP#####PA", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("IIIIIIIII", "GP#F#F#PG", "GP#AAA#PG", "GP#AAA#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(3)
                .aisle("IIIIIIIII", "A##F#F##A", "A##OOO##A", "A##OOO##A", "A#######A", "A#######A", "AA#MMM#AA",
                        "$AAMSMAA$", "$$$MMM$$$", "$$$$$$$$$")
                .where('A', states(getCasingState1()))
                .where('B',
                        states(MetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_LINE_CASING)))
                .where('C', states(MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING)))
                .where('E', energyHatchLimit(true, true, true, true).or(states(getCasingState1())))
                .where('F', frames(Materials.TungstenSteel))
                .where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS)))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setMaxGlobalLimited(30, 5)
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(10, 4))
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
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder()
                .aisle("AAAAAAAAA", "A##F#F##A", "A##AAA##A", "A##AOA##A", "A#######A", "A#######A", "AA#AAA#AA",
                        "$AAAMAAA$", "$$$AAA$$$", "$$$$$$$$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "GP#F#F#PG", "GP#AAA#PG", "GP#AAA#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "AP#F#F#PA", "AP#AAA#PA", "AP#AAA#PA", "AP#####PA", "AP#####PA", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "GP#F#F#PG", "GP#AAA#PG", "GP#AAA#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##AAA##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("IIIIAiiii", "A##F#F##A", "A##AAA##A", "A##AAA##A", "A#######A", "A#######A", "AA#AAA#AA",
                        "$AAASAAA$", "$$$AAA$$$", "$$$$$$$$$")
                .where('A', getCasingState1())
                .where('B',
                        MetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_LINE_CASING))
                .where('C', MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING))
                .where('E', MetaTileEntities.ENERGY_INPUT_HATCH[GTValues.UV], EnumFacing.UP)
                .where('F', MetaBlocks.FRAMES.get(Materials.TungstenSteel).getBlock(Materials.TungstenSteel))
                .where('G', MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS))
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.UV], EnumFacing.SOUTH)
                .where('i', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.UV], EnumFacing.SOUTH)
                .where('M',
                        () -> ConfigHolder.machines.enableMaintenance ? MetaTileEntities.MAINTENANCE_HATCH :
                                getCasingState1(),
                        EnumFacing.NORTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.UV], EnumFacing.NORTH)
                .where('P',
                        MetaBlocks.BOILER_CASING
                                .getState(BlockBoilerCasing.BoilerCasingType.POLYTETRAFLUOROETHYLENE_PIPE))
                .where('S', GTConsolidateMetaTileEntity.COMPONENT_ASSEMBLY_LINE, EnumFacing.SOUTH)
                .where('#', Blocks.AIR.getDefaultState())
                .where('$', Blocks.AIR.getDefaultState());

        Arrays.stream(BlockCoACasing.CoACasingType.values())
                .forEach(casingType -> shapeInfo
                        .add(builder.where('T', GTConsolidateMetaBlocks.COA_CASING.getState(casingType)).build()));
        return shapeInfo;
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        List<IEnergyContainer> inputEnergy = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputEnergy.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
        this.energyContainer = new EnergyContainerList(inputEnergy);
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
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.coa.recipe_tier",
                                GTValues.VNF[getWorkTier()]));
                    }
                })
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine();
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable.getProgress(),
                recipeMapWorkable.getMaxProgress(), recipeMapWorkable.getProgressPercent());
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable.getPreviousRecipe());
    }

    @Override
    public boolean canBeDistinct() {
        return true;
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

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.component_assembly_line.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.multiblock.accept_64a"));
    }

    private class CoALRecipeLogic extends MultiblockRecipeLogic {

        public CoALRecipeLogic(MetaTileEntityComponentAssemblyLine mte) {
            super(mte);
        }

        @Override
        public boolean checkRecipe(@NotNull Recipe recipe) {
            if (!super.checkRecipe(recipe)) return false;

            int recipeCasingTier = recipe.getProperty(CoAProperty.getInstance(), 0);
            return recipeCasingTier <= workTier;
        }
    }
}
