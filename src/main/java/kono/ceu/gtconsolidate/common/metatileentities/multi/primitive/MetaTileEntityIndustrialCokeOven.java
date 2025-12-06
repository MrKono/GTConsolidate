package kono.ceu.gtconsolidate.common.metatileentities.multi.primitive;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.*;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockSteamCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import kono.ceu.gtconsolidate.api.capability.impl.ParallelizedPrimitiveRecipeLogic;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;
import kono.ceu.gtconsolidate.common.blocks.BlockCoolantCasing;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;
import kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetaTileEntityIndustrialCokeOven extends RecipeMapPrimitiveMultiblockController {

    private double efficiency;
    private int parallel;
    private static List<IBlockState> floors = new ArrayList<>();

    static {
        floors.add(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.COKE_BRICKS));
        floors.add(MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.BRONZE_BRICKS_HULL));
        floors.add(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS));
        floors.add(MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.BRONZE_HULL));
        floors.add(MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.STEEL_BRICKS_HULL));
        floors.add(MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID));
        floors.add(MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.STEEL_HULL));
    }

    public MetaTileEntityIndustrialCokeOven(ResourceLocation metaTileEntity) {
        super(metaTileEntity, RecipeMaps.COKE_OVEN_RECIPES);
        this.recipeMapWorkable = new ParallelizedPrimitiveRecipeLogic(this, RecipeMaps.COKE_OVEN_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityIndustrialCokeOven(metaTileEntityId);
    }

    @Override
    protected void initializeAbilities() {
        this.importItems = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.exportItems = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.exportFluids = new FluidTankList(allowSameFluidFillForOutputs(), getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    protected boolean allowSameFluidFillForOutputs() {
        return true;
    }

    @Override
    public void invalidateStructure() {
        this.importItems = new ItemStackHandler(0);
        this.exportItems = new ItemStackHandler(0);
        super.invalidateStructure();
    }

    @Override
    public void clearMachineInventory(NonNullList<ItemStack> itemBuffer) {}

    @NotNull
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XXXSXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX")
                .where('S', selfPredicate())
                .where('X',
                        states(getBrickState()).setMinGlobalLimited(50)
                                .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1, 2))
                                .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1, 2))
                                .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(1, 2)))
                .where('C', indicatorPredicate())
                .where('A', air())
                .build();
    }

    @Override
    public List<MultiblockShapeInfo> getMatchingShapes() {
        ArrayList<MultiblockShapeInfo> shapeInfo = new ArrayList<>();
        MultiblockShapeInfo.Builder builder = MultiblockShapeInfo.builder()
                .aisle("XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XCCCCCX", "XAAAAAX", "XAAAAAX", "XAAAAAX", "XXXXXXX")
                .aisle("XIXSOFX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX")
                .where('S', GTConsolidateMetaTileEntity.INDUSTRIAL_COKE_OVEN, EnumFacing.SOUTH)
                .where('X', MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.COKE_BRICKS))
                .where('I', MetaTileEntities.ITEM_IMPORT_BUS[GTValues.ULV], EnumFacing.SOUTH)
                .where('O', MetaTileEntities.ITEM_EXPORT_BUS[GTValues.ULV], EnumFacing.SOUTH)
                .where('F', MetaTileEntities.FLUID_EXPORT_HATCH[GTValues.ULV], EnumFacing.SOUTH)
                .where('A', Blocks.AIR.getDefaultState());
        for (IBlockState floor : floors) {
            shapeInfo.add(builder.where('C', floor).build());
        }
        return shapeInfo;
    }

    protected IBlockState getBrickState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.COKE_BRICKS);
    }

    private TraceabilityPredicate indicatorPredicate() {
        return new TraceabilityPredicate((blockWorldState) -> {
                if (states(floors.get(0)).test(blockWorldState)) {
                    // Coke Bricks -> x4 speed, 2 parallels
                    blockWorldState.getMatchContext().set("efficiency", 4);
                    blockWorldState.getMatchContext().set("parallel", 2);
                    return true;
                } else if (states(floors.get(1)).test(blockWorldState)) {
                    // Bronze Brick Hull -> x2 speed, 8 parallels
                    blockWorldState.getMatchContext().set("efficiency", 2);
                    blockWorldState.getMatchContext().set("parallel", 8);
                    return true;
                } else if (states(floors.get(2)).test(blockWorldState)) {
                    // Bronze Machine Casing -> x6 speed, 8 parallels
                    blockWorldState.getMatchContext().set("efficiency", 6);
                    blockWorldState.getMatchContext().set("parallel", 8);
                    return true;
                } else if (states(floors.get(3)).test(blockWorldState)) {
                    // Bronze Hull -> x8 speed, 8 parallels
                    blockWorldState.getMatchContext().set("efficiency", 8);
                    blockWorldState.getMatchContext().set("parallel", 8);
                    return true;
                } else if (states(floors.get(4)).test(blockWorldState)) {
                    // Steel Brick Hull -> x8 speed, 16 parallels
                    blockWorldState.getMatchContext().set("efficiency", 8);
                    blockWorldState.getMatchContext().set("parallel", 16);
                    return true;
                } else if (states(floors.get(5)).test(blockWorldState)) {
                    // Steel Machine Casing -> x16 speed, 16 parallels
                    blockWorldState.getMatchContext().set("efficiency", 16);
                    blockWorldState.getMatchContext().set("parallel", 16);
                    return true;
                } else if (states(floors.get(6)).test(blockWorldState)) {
                    // Steel Hull -> x32 speed, 16 parallels
                    blockWorldState.getMatchContext().set("efficiency", 32);
                    blockWorldState.getMatchContext().set("parallel", 16);
                    return true;
                }
            return false;
        });
    }


    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        this.efficiency = context.getOrDefault("efficiency", 1);
        this.parallel = context.getOrDefault("parallel", 1);

        recipeMapWorkable.setSpeedBonus((double) 1 / this.efficiency);
        recipeMapWorkable.setMaximumOverclockVoltage(this.parallel * 2L);
        recipeMapWorkable.setParallelLimit(this.parallel);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.COKE_BRICKS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderOrientedState(renderState, translation, pipeline, getFrontFacing(),
                recipeMapWorkable.isActive(), recipeMapWorkable.isWorkingEnabled());
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.COKE_OVEN_OVERLAY;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());

        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive());
        builder.addWorkingStatusLine();
        builder.addCustom(list -> {
            if (isStructureFormed()) {
                ITextComponent bonus = TextComponentUtil.stringWithColor(TextFormatting.WHITE,
                        TextFormattingUtil.formatNumbers(efficiency * 100f));
                list.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                        "gtconsolidate.multiblock.speed_bonus", bonus));
            }
        });
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setDouble("efficiency", this.efficiency);
        data.setInteger("parallel", this.parallel);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.efficiency = data.getDouble("efficiency");
        this.parallel = data.getInteger("parallel");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeDouble(this.efficiency);
        buf.writeInt(this.parallel);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.efficiency = buf.readDouble();
        this.parallel = buf.readInt();
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return true;
    }
}
