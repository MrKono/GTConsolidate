package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.CoATieredCasing;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.nonCleanMaintenance;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateUtil.isTABDown;

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
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
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

public class MetaTileEntityCircuitFactory extends RecipeMapMultiblockController {

    private int workTier;
    private EnergyContainerList inputHatches;

    public MetaTileEntityCircuitFactory(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, GTConsolidateRecipeMaps.CIRCUIT_FACTORY_RECIPES);
        this.recipeMapWorkable = new CircuitFactoryRecipeLogic(this);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity mte) {
        return new MetaTileEntityCircuitFactory(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("IIIIIIIII", "A##F#F##A", "A##OOO##A", "A##OOO##A", "A#######A", "A#######A", "AA#MMM#AA",
                        "$AAMMMAA$", "$$$MMM$$$", "$$$$$$$$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("IIIIIIIII", "GP#F#F#PG", "GP#DDD#PG", "GP#DDD#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("IIIIIIIII", "AP#F#F#PA", "AP#DDD#PA", "AP#DDD#PA", "AP#####PA", "AP#####PA", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("IIIIIIIII", "GP#F#F#PG", "GP#DDD#PG", "GP#DDD#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("IIIIIIIII", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .setRepeatable(2)
                .aisle("IIIIIIIII", "A##F#F##A", "A##OOO##A", "A##OOO##A", "A#######A", "A#######A", "AA#MMM#AA",
                        "$AAMSMAA$", "$$$MMM$$$", "$$$$$$$$$")
                .where('A', states(getCasingState1()))
                .where('B',
                        states(MetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_LINE_CASING)))
                .where('C', states(MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING)))
                .where('D', states(getCasingState2()))
                .where('E',
                        abilities(MultiblockAbility.INPUT_LASER).setMinGlobalLimited(1).setMaxGlobalLimited(8)
                                .or(states(getCasingState1())))
                .where('F', frames(Materials.TungstenSteel))
                .where('G', states(MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.FUSION_GLASS)))
                .where('I', abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setMaxGlobalLimited(30, 5)
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMaxGlobalLimited(10, 4))
                        .or(states(getCasingState1())))
                .where('M', nonCleanMaintenance().or(states(getCasingState1())))
                .where('O',
                        abilities(MultiblockAbility.EXPORT_ITEMS).setMaxGlobalLimited(1).or(states(getCasingState2())))
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

    public IBlockState getCasingState2() {
        return MetaBlocks.COMPUTER_CASING.getState(BlockComputerCasing.CasingType.ADVANCED_COMPUTER_CASING);
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder()
                .aisle("AAAAAAAAA", "A##F#F##A", "A##DDD##A", "A##DOD##A", "A#######A", "A#######A", "AA#AAA#AA",
                        "$AAAMAAA$", "$$$AAA$$$", "$$$$$$$$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABEBA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "GP#F#F#PG", "GP#DDD#PG", "GP#DDD#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "AP#F#F#PA", "AP#DDD#PA", "AP#DDD#PA", "AP#####PA", "AP#####PA", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "GP#F#F#PG", "GP#DDD#PG", "GP#DDD#PG", "GP#####PG", "GP#####PG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("AAAAAAAAA", "G#######G", "G#######G", "G##DDD##G", "GB#####BG", "GB#####BG", "AA#TTT#AA",
                        "CAAATAAAC", "$CATTTAC$", "$$ABABA$$")
                .aisle("IIIIAiiii", "A##F#F##A", "A##DDD##A", "A##DDD##A", "A#######A", "A#######A", "AA#AAA#AA",
                        "$AAASAAA$", "$$$AAA$$$", "$$$$$$$$$")
                .where('A', getCasingState1())
                .where('B',
                        MetaBlocks.MULTIBLOCK_CASING
                                .getState(BlockMultiblockCasing.MultiblockCasingType.ASSEMBLY_LINE_CASING))
                .where('C', MetaBlocks.CLEANROOM_CASING.getState(BlockCleanroomCasing.CasingType.FILTER_CASING))
                .where('D', getCasingState2())
                .where('E', MetaTileEntities.LASER_INPUT_HATCH_256[1], EnumFacing.UP)
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
                .where('S', GTConsolidateMetaTileEntity.CIRCUIT_FACTORY, EnumFacing.SOUTH)
                .where('#', Blocks.AIR.getDefaultState())
                .where('$', Blocks.AIR.getDefaultState());

        Arrays.stream(BlockCoACasing.CoACasingType.values())
                .forEach(casingType -> shapeInfo
                        .add(builder.where('T', GTConsolidateMetaBlocks.COA_CASING.getState(casingType)).build()));
        return shapeInfo;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        List<IEnergyContainer> inputs = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_LASER));
        this.inputHatches = new EnergyContainerList(inputs);

        Object type = context.get("CasingTier");
        if (type instanceof BlockCoACasing.CoACasingType) {
            this.workTier = ((BlockCoACasing.CoACasingType) type).ordinal();
        } else {
            this.workTier = BlockCoACasing.CoACasingType.ULV.ordinal();
        }
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        List<IEnergyContainer> inputEnergy = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputEnergy.addAll(getAbilities(MultiblockAbility.INPUT_LASER));
        this.energyContainer = new EnergyContainerList(inputEnergy);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart instanceof IMultiblockAbilityPart) {
            MultiblockAbility<?> ability = ((IMultiblockAbilityPart<?>) sourcePart).getAbility();
            if (ability == MultiblockAbility.EXPORT_ITEMS) {
                return Textures.ADVANCED_COMPUTER_CASING;
            }
        }
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
                .addEnergyUsageExactLine(recipeMapWorkable.getInfoProviderEUt())
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.multiblock.coa.recipe_tier",
                                GTValues.VNF[getWorkTier()]));
                    }
                });
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        builder.addWorkingStatusLine();
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
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
        tooltip.add(I18n.format("gtconsolidate.machine.circuit_factory.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.machine.circuit_factory.tooltip.2"));
        tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.energy_in.laser")));
        tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.maintenance_no_clean")));
        if (isTABDown()) {
            tooltip.add(I18n.format("gtconsolidate.machine.circuit_factory.tooltip.3"));
            tooltip.add(I18n.format("gtconsolidate.machine.circuit_factory.tooltip.4"));
            tooltip.add(I18n.format("gtconsolidate.machine.circuit_factory.tooltip.5"));
        } else {
            tooltip.add((I18n.format("gtconsolidate.multiblock.tooltip.universal.tab.build")));
        }
    }

    private class CircuitFactoryRecipeLogic extends MultiblockRecipeLogic {

        public CircuitFactoryRecipeLogic(MetaTileEntityCircuitFactory mte) {
            super(mte, true);
        }

        @Override
        public long getMaxVoltage() {
            IEnergyContainer energyContainer = getEnergyContainer();
            if (!consumesEnergy()) {
                // Generators Is it needed??
                long voltage = energyContainer.getOutputVoltage();
                long amperage = energyContainer.getOutputAmperage();
                if (energyContainer instanceof EnergyContainerList && amperage == 1) {
                    return GTValues.V[GTUtility.getFloorTierByVoltage(voltage)];
                }
                return voltage;
            } else {
                if (energyContainer instanceof EnergyContainerList energyList) {
                    long highestVoltage = energyList.getHighestInputVoltage();
                    if (energyList.getNumHighestInputContainers() > 1) {
                        int tier = GTUtility.getTierByVoltage(highestVoltage);
                        return GTValues.V[Math.min(tier + (energyList.getNumHighestInputContainers() - 1),
                                GTValues.MAX)];
                    } else {
                        return highestVoltage;
                    }
                } else {
                    return energyContainer.getInputVoltage();
                }
            }
        }

        @Override
        public boolean checkRecipe(@NotNull Recipe recipe) {
            if (!super.checkRecipe(recipe)) return false;

            int recipeCasingTier = recipe.getProperty(CoAProperty.getInstance(), 0);
            return recipeCasingTier <= workTier;
        }
    }
}
