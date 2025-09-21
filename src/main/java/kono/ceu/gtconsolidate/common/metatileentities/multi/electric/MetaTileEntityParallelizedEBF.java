package kono.ceu.gtconsolidate.common.metatileentities.multi.electric;

import static gregtech.api.recipes.logic.OverclockingLogic.heatingCoilOverclockingLogic;
import static gregtech.api.util.RelativeDirection.*;
import static gregtech.client.utils.TooltipHelper.isCtrlDown;
import static gregtech.client.utils.TooltipHelper.isShiftDown;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateTraceabilityPredicate.*;
import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.*;

import java.util.*;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
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
import gregtech.api.GregTechAPI;
import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.capability.IHeatingCoil;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.*;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.recipes.recipeproperties.TemperatureProperty;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.ConfigHolder;
import gregtech.common.blocks.*;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.core.sound.GTSoundEvents;

import gregicality.multiblocks.api.capability.IParallelMultiblock;
import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;

import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;
import kono.ceu.gtconsolidate.client.GTConsolidateTextures;
import kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity;

public class MetaTileEntityParallelizedEBF extends RecipeMapMultiblockController
                                           implements IHeatingCoil, IParallelMultiblock {

    private final int maxParallel;
    private int blastFurnaceTemperature;
    public int height;

    public MetaTileEntityParallelizedEBF(ResourceLocation metaTileEntity, int maxParallel) {
        super(metaTileEntity, RecipeMaps.BLAST_RECIPES);
        this.recipeMapWorkable = new AdvancedEBFRecipeLogic(this);
        this.maxParallel = maxParallel;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityParallelizedEBF(metaTileEntityId, maxParallel);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RIGHT, FRONT, UP)
                .aisle("XSX", "XXX", "XXX")
                .aisle("CCC", "C#C", "CCC")
                .aisle("CCC", "CIC", "CCC").setRepeatable(1, 4)
                .aisle("XXX", "XMX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(modeCasingState()).setMinGlobalLimited(9)
                        .or(autoAbilities(false, false, true, true, true, true, false))
                        .or(energyHatchLimit(false, maxParallel == 4, true)
                                .setMinGlobalLimited(1).setMaxGlobalLimited(2))
                        .or(manualMaintenance()))
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('C', heatingCoils())
                .where('I', indicatorPredicate())
                .where('#', states(modeBlockState()))
                .build();
    }

    // This function is highly useful for detecting the length of this multiblock.
    public TraceabilityPredicate indicatorPredicate() {
        return new TraceabilityPredicate((blockWorldState) -> {
            if (states(modeBlockState()).test(blockWorldState)) {
                blockWorldState.getMatchContext().increment("coilLayer", 1);
                return true;
            } else
                return false;
        });
    }

    public IBlockState modeCasingState() {
        IBlockState state;
        state = switch (mode()) {
            case "NORMAL" -> maxParallel == 4 ?
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING) :
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK2);
            case "HARD" -> maxParallel == 4 ?
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK2) :
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING_MK3);
            default -> maxParallel == 4 ?
                    MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.INVAR_HEATPROOF) :
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_CASING);
        };
        return state;
    }

    public IBlockState modeBlockState() {
        IBlockState state;
        state = switch (mode()) {
            case "NORMAL" -> maxParallel == 4 ?
                    MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE) :
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.SUPERCONDUCTOR_COIL);
            case "HARD" -> maxParallel == 4 ?
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.SUPERCONDUCTOR_COIL) :
                    MetaBlocks.FUSION_CASING.getState(BlockFusionCasing.CasingType.FUSION_COIL);
            default -> maxParallel == 4 ? Blocks.AIR.getDefaultState() :
                    MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.TUNGSTENSTEEL_PIPE);
        };
        return state;
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder()
                .aisle("EEM", "CCC", "CCC", "XXX")
                .aisle("FXD", "C#C", "C#C", "XHX")
                .aisle("ISO", "CCC", "CCC", "XXX")
                .where('X', modeCasingState())
                .where('S', GTConsolidateMetaTileEntity.PARALLELIZED_EBF[maxParallel == 4 ? 0 : 1], EnumFacing.SOUTH)
                .where('#', modeBlockState())
                .where('E', maxParallel == 4 ? MetaTileEntities.ENERGY_INPUT_HATCH_4A[GTValues.LV] :
                        MetaTileEntities.ENERGY_INPUT_HATCH_16A[GTValues.ULV], EnumFacing.NORTH)
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.LV], EnumFacing.SOUTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.LV], EnumFacing.SOUTH)
                .where('F', MetaTileEntities.FLUID_IMPORT_HATCH[GTValues.LV], EnumFacing.WEST)
                .where('D', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.LV], EnumFacing.EAST)
                .where('H', MetaTileEntities.MUFFLER_HATCH[GTValues.LV], EnumFacing.UP)
                .where('M', () -> ConfigHolder.machines.enableMaintenance ? MetaTileEntities.MAINTENANCE_HATCH :
                        modeCasingState(), EnumFacing.NORTH);
        GregTechAPI.HEATING_COILS.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().getTier()))
                .forEach(entry -> shapeInfo.add(builder.where('C', entry.getKey()).build()));
        return shapeInfo;
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        this.height = context.getOrDefault("coilLayer", 1);
        Object type = context.get("CoilType");
        if (type instanceof IHeatingCoilBlockStats) {
            this.blastFurnaceTemperature = ((IHeatingCoilBlockStats) type).getCoilTemperature();
        } else {
            this.blastFurnaceTemperature = BlockWireCoil.CoilType.CUPRONICKEL.getCoilTemperature();
        }
        // the subtracted tier gives the starting level (exclusive) of the +100K heat bonus
        this.blastFurnaceTemperature += 100 *
                Math.max(0, GTUtility.getFloorTierByVoltage(getEnergyContainer().getInputVoltage()) - GTValues.MV);
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
    public boolean isParallel() {
        return true;
    }

    private int increaseFactor() {
        return maxParallel == 4 ? 1 : 4;
    }

    @Override
    public int getMaxParallel() {
        return height * increaseFactor();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("height", this.height);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.height = data.getInteger("height");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.height);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.height = buf.readInt();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());
        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addEnergyUsageExactLine(recipeMapWorkable.getInfoProviderEUt())
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
                });
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        builder.addWorkingStatusLine();
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        SimpleOverlayRenderer renderer;
        renderer = switch (mode()) {
            case "NORMAL" -> maxParallel == 4 ? GTConsolidateTextures.FUSION_CASING :
                    GTConsolidateTextures.FUSION_CASING_MK2;
            case "HARD" -> maxParallel == 4 ? GTConsolidateTextures.FUSION_CASING_MK2 :
                    GTConsolidateTextures.FUSION_CASING_MK3;
            default -> maxParallel == 4 ? Textures.HEAT_PROOF_CASING : GTConsolidateTextures.FUSION_CASING;
        };
        return renderer;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.parallelized_ebf.tooltip.1"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.parallel", maxParallel));
        tooltip.add(I18n.format("gtconsolidate.machine.parallelized_ebf.tooltip.2", increaseFactor()));
        tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                maxParallel == 4 ? I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.energy_in.4and16") :
                        I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.energy_in.16")));
        tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.limit",
                I18n.format("gtconsolidate.multiblock.tooltip.universal.limit.manual_maintenance")));
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.1"));
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.2"));
        tooltip.add(I18n.format("gregtech.machine.electric_blast_furnace.tooltip.3"));
        if (isShiftDown() && isCtrlDown()) {
            tooltip.add(I18n.format("gtconsolidate.machine.parallelized_ebf.tooltip.3"));
            for (int i = 1; i < 5; i++) {
                int j = i + 3;
                tooltip.add(I18n.format(("gtconsolidate.machine.parallelized_ebf.tooltip." + j), i + 1,
                        i * increaseFactor()));
            }
        } else {
            tooltip.add(I18n.format("gtconsolidate.multiblock.tooltip.universal.shift_ctrl"));
        }
    }

    @Override
    public int getCurrentTemperature() {
        return this.blastFurnaceTemperature;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.BLAST_FURNACE_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @NotNull
    @Override
    public List<ITextComponent> getDataInfo() {
        List<ITextComponent> list = super.getDataInfo();
        list.add(new TextComponentTranslation("gregtech.multiblock.blast_furnace.max_temperature",
                new TextComponentTranslation(TextFormattingUtil.formatNumbers(blastFurnaceTemperature) + "K")
                        .setStyle(new Style().setColor(TextFormatting.RED))));
        return list;
    }

    private static class AdvancedEBFRecipeLogic extends GCYMMultiblockRecipeLogic {

        public AdvancedEBFRecipeLogic(RecipeMapMultiblockController metaTileEntity) {
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
