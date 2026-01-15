package kono.ceu.gtconsolidate.common.metatileentities.multi.tank;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FilteredFluidHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockHermeticCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;
import kono.ceu.gtconsolidate.client.GTConsolidateTextures;
import kono.ceu.gtconsolidate.common.blocks.BlockTankPart;
import kono.ceu.gtconsolidate.common.blocks.BlockTankWall;
import kono.ceu.gtconsolidate.common.blocks.GTConsolidateMetaBlocks;
import kono.ceu.gtconsolidate.common.metatileentities.GTConsolidateMetaTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MetaTileEntityMultiblockLargeTank extends MultiblockWithDisplayBase {

    private final int MAX = 2000000000;
    private FluidTankList fluidTankList;
    private final int tier;

    // Display
    private final int PAGE_SIZE = 3;
    private int currentPage = 0;
    private int factor = 1;

    public MetaTileEntityMultiblockLargeTank(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId);
        this.tier = tier;
        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMultiblockLargeTank(metaTileEntityId, this.tier);
    }

    @Override
    public void initializeInventory() {
        super.initializeInventory();
        if (getTotalTanks() > 0) {
            FilteredFluidHandler[] filteredFluidHandlers = new FilteredFluidHandler[getTotalTanks()];
            for (int i = 0; i < numIntMaxTanks(); i++) {
                filteredFluidHandlers[i] = new FilteredFluidHandler(MAX);
            }
            if (capacityExtraTank() > 0) {
                filteredFluidHandlers[numIntMaxTanks()] = new FilteredFluidHandler(capacityExtraTank());
            }
            this.exportFluids = this.importFluids = new FluidTankList(true, filteredFluidHandlers);
            this.fluidInventory = this.fluidTankList = new FluidTankList(true, filteredFluidHandlers);
        }
    }

    private long getCapacity() {
        return BlockTankPart.TankPartType.getTankPartTypeFromTier(this.tier).getCapacity();
    }

    private int numIntMaxTanks() {
        return Math.toIntExact(getCapacity() / MAX);
    }

    private int capacityExtraTank() {
        return (int) (getCapacity() % MAX);
    }

    public int getTotalTanks() {
        int extra = capacityExtraTank() > 0 ? 1 : 0;
        return  numIntMaxTanks() + extra;
    }

    @Override
    protected void updateFormedValid() {}

    @Override
    @NotNull
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX")
                .aisle("XXXXX", "XHHHX", "XHHHX", "XHHHX", "XXXXX")
                .aisle("XXXXX", "XHHHX", "XHTHX", "XHHHX", "XXXXX")
                .aisle("XXXXX", "XHHHX", "XHHHX", "XHHHX", "XXXXX")
                .aisle("XXXXX", "XXXXX", "XXSXX", "XXXXX", "XXXXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState())
                        .or(metaTileEntities(MetaTileEntities.STEEL_TANK_VALVE,
                                GTConsolidateMetaTileEntity.ADVANCED_TANK_VALVE)
                                .setMaxGlobalLimited(10).setPreviewCount(2)))
                .where('H', states(getHermeticState()))
                .where('T', states(getTankState()))
                .build();
    }

    private IBlockState getCasingState() {
        return GTConsolidateMetaBlocks.TANK_WALL.getState(BlockTankWall.TankWallType.getWallTypeFromTier(this.tier));
    }

    private IBlockState getHermeticState() {
        return switch (this.tier) {
            case 2 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_MV);
            case 3 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_HV);
            case 4 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_EV);
            case 5 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_IV);
            case 6 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_LUV);
            case 7 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_ZPM);
            case 8 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_UV);
            case 9 -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_UHV);
            default -> MetaBlocks.HERMETIC_CASING.getState(BlockHermeticCasing.HermeticCasingsType.HERMETIC_LV);
        };
    }

    private IBlockState getTankState() {
        return GTConsolidateMetaBlocks.TANK_PART.getState(BlockTankPart.TankPartType.getTankPartTypeFromTier(this.tier));
    }

    @SideOnly(Side.CLIENT)
    @Override
    @NotNull
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return GTConsolidateTextures.TANK_WALLS[this.tier];
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public boolean onRightClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                CuboidRayTraceResult hitResult) {
        if (!isStructureFormed())
            return false;
        return super.onRightClick(playerIn, hand, facing, hitResult);
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return isStructureFormed();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        int maxPage = getPageSize();
        int displayPage = this.currentPage < 0 ? 0 : Math.min(this.currentPage, maxPage);
        int startIndex = displayPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, this.fluidTankList.getTanks());

        MultiblockDisplayText.builder(textList, isStructureFormed())
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        // Capacity Line
                        ITextComponent capacity = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                TextFormattingUtil.formatNumbers(getCapacity()));
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.test.0", capacity));
                        // Tank
                        ITextComponent numTanks = TextComponentUtil.stringWithColor(
                                TextFormatting.GREEN,
                                TextFormattingUtil.formatNumbers(this.fluidTankList.getTanks()));
                        ITextComponent pages = TextComponentUtil.stringWithColor(
                                TextFormatting.YELLOW,
                                TextFormattingUtil.formatNumbers(getPageSize()));
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.WHITE,
                                "gtconsolidate.test.1", numTanks, pages));
                        // Tank Info
                        ITextComponent current = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                TextFormattingUtil.formatNumbers(this.currentPage + 1));
                        ITextComponent max = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                TextFormattingUtil.formatNumbers(getPageSize()));
                        ITextComponent factorLine = TextComponentUtil.stringWithColor(
                                TextFormatting.YELLOW,
                                TextFormattingUtil.formatNumbers(this.factor));
                        ITextComponent factorBody = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.test.9", factorLine);
                        ITextComponent factorHover = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.test.10");
                        tl.add(TextComponentUtil.setHover(factorBody, factorHover));
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.test.8", current, max));
                        for (int i = startIndex; i < endIndex; i++) {
                            IMultipleTankHandler.MultiFluidTankEntry tankEntry = this.importFluids.getTankAt(i);
                            FluidStack fluid = tankEntry.getFluid();
                            int amount = tankEntry.getFluidAmount();
                            ITextComponent fluidName = TextComponentUtil.stringWithColor(
                                    fluid != null ? TextFormatting.AQUA : TextFormatting.YELLOW,
                                    fluid != null ? fluid.getLocalizedName() : "Empty");
                            ITextComponent body = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY, "gtconsolidate.test.2", i + 1, fluidName);
                            ITextComponent hover = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gtconsolidate.test.3", fluidName, TextFormattingUtil.formatNumbers(amount), TextFormattingUtil.formatNumbers(tankEntry.getCapacity()));
                            tl.add(TextComponentUtil.setHover(body, hover));
                        }
                    }
                });
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        getFrontOverlay().renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.MULTIBLOCK_TANK_OVERLAY;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.multiblock.tank.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity", getCapacity()));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        int maxPage = getPageSize();
        int displayPage = this.currentPage < 0 ? 0 : Math.min(this.currentPage, maxPage);
        int pageGroupStart = (displayPage / 3) * 3;
        int startIndex = pageGroupStart * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE * 3, this.fluidTankList.getTanks());

        List<IFluidTank> displayTanks = new ArrayList<>();
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (isStructureFormed() && this.fluidTankList.getTanks() > 0) {
                for (int i = startIndex; i < endIndex; i++) {
                    IMultipleTankHandler.MultiFluidTankEntry tankEntry = this.importFluids.getTankAt(i);
                    displayTanks.add(tankEntry);
                }
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(new FluidTankList(true, displayTanks));
            } else {
                return null;
            }
        }
        return super.getCapability(capability, side);
    }

    @NotNull
    public IFluidHandler getFluidInventory(int index) {
        if (index < 1 || index > this.fluidTankList.getTanks()) {
            return getFluidInventory();
        }
        return this.fluidTankList.getTankAt(index - 1);
    }

    private int getPageSize() {
        return (this.fluidTankList.getTanks() + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    public void decrementFactor(Widget.ClickData data) {
        this.factor = MathHelper.clamp(factor - changed(data), 1, 100000);
    }

    public void incrementFactor(Widget.ClickData data) {
        this.factor = MathHelper.clamp(factor + changed(data), 1, 100000);
    }

    public void moveToPreviousPage(Widget.ClickData data) {
        this.currentPage = MathHelper.clamp(currentPage - changed(data) * this.factor, 0, currentPage);
    }

    public void moveToNextPage(Widget.ClickData data) {
        this.currentPage = MathHelper.clamp(currentPage + changed(data) * this.factor, 0, getPageSize() - 1);
    }

    private int changed(Widget.ClickData date) {
        // none -> 1, shift -> 10, ctrl -> 100, shift+ctrl -> 1000
        return date.isShiftClick && date.isCtrlClick ? 1000 : date.isCtrlClick ? 100 : date.isShiftClick ? 10 : 1;
    }

    // increment / decrement factor button
    @Override
    protected @NotNull Widget getFlexButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::decrementFactor)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gtconsolidate.test.4"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::incrementFactor)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gtconsolidate.test.5"));
        return group;
    }

    // page change button
    protected @NotNull Widget getPageButton(int x, int y, int width, int height) {
        WidgetGroup group = new WidgetGroup(x, y, width, height);
        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", this::moveToPreviousPage)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gtconsolidate.test.6"));
        group.addWidget(new ClickButtonWidget(9, 0, 9, 18, "", this::moveToNextPage)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gtconsolidate.test.7", getPageSize()));
        return group;
    }

    /**
     * Overrides the default UI template to fix layout issues and customize buttons.
     *
     * <p>
     * In the original implementation, some UI elements overflow the display area.
     * To resolve this, the overall height of the GUI is increased by +10.
     * </p>
     *
     * <p>
     * Additionally, unnecessary default buttons are removed and replaced with
     * custom buttons specific to this implementation.
     * </p>
     *
     * <p><b>Main modifications:</b></p>
     * <ul>
     *   <li>Increase the GUI background height by +10.</li>
     *   <li>Increase the display area height by +10.</li>
     *   <li>Adjust related widgets to follow the expanded display size.</li>
     *   <li>Replace unused default buttons with custom buttons.</li>
     * </ul>
     */
    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        // Determines the overall GUI size.
        // The third argument (height) is increased by 20.
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 198, 228);

        // === Display ===

        // Text display area.
        // The 4th argument (height) is increased by 20.
        builder.image(4, 4, 190, 137, GuiTextures.DISPLAY);
        // Logo display area.
        // Since the display width is expanded, the yPosition of IndicatorImageWidget
        // (2nd argument) is also increased by 20.
        builder.widget(new IndicatorImageWidget(174, 121, 17, 17, getLogo())
                .setWarningStatus(getWarningLogo(), this::addWarningText)
                .setErrorStatus(getErrorLogo(), this::addErrorText));
        // Machine name label (unchanged).
        builder.label(9, 9, getMetaFullName(), 0xFFFFFF);
        // Additional text display area (unchanged).
        builder.widget(new AdvancedTextWidget(9, 20, this::addDisplayText, 0xFFFFFF)
                .setMaxWidthLimit(181)
                .setClickHandler(this::handleDisplayClick));

        // Power Button and Void mode button are unnecessary here, so they are removed.

        // Page change button.
        // Originally the Distinct Buses Button, but it is unnecessary here,
        // so it is replaced with a custom button.
        // The second argument (y) is increased by 20.
        builder.widget(getPageButton(173, 163, 18 ,18 ));
        // Factor change button.
        // Uses the provided getFlexButton method.
        // The second argument (y) is increased by 20.
        builder.widget(getFlexButton(173, 145, 18, 18));
        // Player inventory.
        // The startY value (second argument) is increased by 20 to follow the expanded display.
        builder.bindPlayerInventory(entityPlayer.inventory, 145);
        return builder;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("currentPage", this.currentPage);
        data.setInteger("factor", this.factor);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.currentPage = data.getInteger("currentPage");
        this.factor = data.getInteger("factor");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.currentPage);
        buf.writeInt(this.factor);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.currentPage = buf.readInt();
        this.factor = buf.readInt();
    }
}
