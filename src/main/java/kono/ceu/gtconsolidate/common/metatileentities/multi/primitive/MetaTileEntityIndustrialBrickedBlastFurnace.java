package kono.ceu.gtconsolidate.common.metatileentities.multi.primitive;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.*;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.util.RelativeDirection;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.BlockSteamCasing;
import gregtech.common.blocks.MetaBlocks;

import kono.ceu.gtconsolidate.api.capability.impl.ParallelizedPrimitiveRecipeLogic;
import kono.ceu.gtconsolidate.api.util.mixinhelper.MultiblockDisplayTextMixinHelper;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

public class MetaTileEntityIndustrialBrickedBlastFurnace extends RecipeMapPrimitiveMultiblockController {

    private int parallel;

    public MetaTileEntityIndustrialBrickedBlastFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES);
        this.recipeMapWorkable = new ParallelizedPrimitiveRecipeLogic(this, RecipeMaps.PRIMITIVE_BLAST_FURNACE_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityIndustrialBrickedBlastFurnace(metaTileEntityId);
    }

    @Override
    protected void initializeAbilities() {
        this.importItems = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.exportItems = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start(RelativeDirection.RIGHT, RelativeDirection.FRONT, RelativeDirection.DOWN)
                .aisle("#######", "#######", "##XXX##", "##XAX##", "##XXX##", "#######", "#######")
                .aisle("#######", "##XXX##", "#XAAAX#", "#XAAAX#", "#XAAAX#", "##XXX##", "#######")
                .aisle("##XXX##", "#XAAAX#", "XAAAAAX", "XAAIAAX", "XAAAAAX", "#XAAAX#", "##XXX##").setRepeatable(1, 63)
                .aisle("##XSX##", "#XAAAX#", "XAAAAAX", "XAATAAX", "XAAAAAX", "#XAAAX#", "##XXX##")
                .aisle("##CCC##", "#CXXXC#", "CXXXXXC", "CXXXXXC", "CXXXXXC", "#CXXXC#", "##CCC##")
                .where('C',
                        states(MetaBlocks.STEAM_CASING.getState(BlockSteamCasing.SteamCasingType.STEEL_BRICKS_HULL))
                                .setMinGlobalLimited(12)
                                .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMinGlobalLimited(1).setPreviewCount(1))
                                .or(abilities(MultiblockAbility.EXPORT_ITEMS).setMinGlobalLimited(1)
                                        .setPreviewCount(1)))
                .where('I', indicatorPredicate())
                .where('S', selfPredicate())
                .where('T', states(getPillarState()))
                .where('X', states(getCasingState()))
                .where('A', air())
                .where('#', any())
                .build();
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.PRIMITIVE_BRICKS);
    }

    protected IBlockState getPillarState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    // This function is highly useful for detecting the length of this multiblock.
    public TraceabilityPredicate indicatorPredicate() {
        return new TraceabilityPredicate((blockWorldState) -> {
            if (states(getPillarState()).test(blockWorldState)) {
                blockWorldState.getMatchContext().increment("parallel", 1);
                return true;
            } else
                return false;
        });
    }

    private double getSpeedBonus() {
        double a = (double) (this.parallel + 1) / 8;
        return (double) 1 / (1 + a);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
        this.parallel = context.getOrDefault("parallel", 1);

        recipeMapWorkable.setSpeedBonus(getSpeedBonus());
        recipeMapWorkable.setMaximumOverclockVoltage(this.parallel + 1);
        recipeMapWorkable.setParallelLimit(this.parallel + 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart instanceof IMultiblockAbilityPart) {
            return Textures.STEAM_BRICKED_CASING_STEEL;
        } else {
            return Textures.PRIMITIVE_BRICKS;
        }
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
        return Textures.PRIMITIVE_BLAST_FURNACE_OVERLAY;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, isStructureFormed());

        builder.setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive());
        builder.addWorkingStatusLine();
        builder.addCustom(list -> {
            ITextComponent bonus = TextComponentUtil.stringWithColor(TextFormatting.WHITE,
                    TextFormattingUtil.formatNumbers((1 / getSpeedBonus()) * 100f));
            list.add(TextComponentUtil.translationWithColor(TextFormatting.GRAY,
                    "gtconsolidate.multiblock.speed_bonus", bonus));
        });
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedParallelLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addExtendedProgressLine(recipeMapWorkable);
        ((MultiblockDisplayTextMixinHelper) builder).addOutputLine(recipeMapWorkable);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.industrial_bbf.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.machine.industrial_bbf.tooltip.2"));
        tooltip.add(I18n.format("gtconsolidate.machine.industrial_bbf.tooltip.3"));
        tooltip.add(I18n.format("gtconsolidate.machine.industrial_bbf.tooltip.4"));
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("parallel", this.parallel);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.parallel = data.getInteger("parallel");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.parallel);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.parallel = buf.readInt();
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return true;
    }
}
