package kono.ceu.gtconsolidate.common.metatileentities.multi.tank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FilteredFluidHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.Widget;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.ClickButtonWidget;
import gregtech.api.gui.widgets.IndicatorImageWidget;
import gregtech.api.gui.widgets.WidgetGroup;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.util.BlockInfo;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.common.metatileentities.MetaTileEntities;

import kono.ceu.gtconsolidate.api.multiblock.ITankData;
import kono.ceu.gtconsolidate.api.util.GTConsolidateValues;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

public class MetaTileEntityTestTankB extends MultiblockWithDisplayBase {

    private final int MAX = 2000000000;
    private FluidTankList fluidTankList;

    // Match Context Headers
    private static final String PMC_TANK_HEADER = "Tanks_";

    // Display
    private final int PAGE_SIZE = 3;
    private int totalPage;
    private int currentPage;
    private int factor;
    private long capacity;

    public MetaTileEntityTestTankB(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.currentPage = 0;
        this.factor = 1;
        this.capacity = 1000;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityTestTankB(metaTileEntityId);
    }

    @Override
    public void initializeInventory() {
        super.initializeInventory();
        // this.exportFluids = this.importFluids = new FluidTankList(true, filteredFluidHandlers());
        // this.fluidInventory = this.fluidTankList = new FluidTankList(true, filteredFluidHandlers());
    }

    private long getCapacity() {
        return this.capacity;
    }

    private int numIntMaxTanks() {
        return Math.toIntExact(getCapacity() / MAX);
    }

    private int capacityExtraTank() {
        return (int) (getCapacity() % MAX);
    }

    private int getTotalTanks() {
        int extra = capacityExtraTank() > 0 ? 1 : 0;
        return numIntMaxTanks() + extra;
    }

    @Override
    protected void updateFormedValid() {}

    @Override
    @NotNull
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX")
                .aisle("XXXXX", "XTTTX", "XTTTX", "XTTTX", "XXXXX")
                .aisle("XXXXX", "XTTTX", "XTTTX", "XTTTX", "XXXXX")
                .aisle("XXXXX", "XTTTX", "XTTTX", "XTTTX", "XXXXX")
                .aisle("XXXXX", "XXXXX", "XXSXX", "XXXXX", "XXXXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState())
                        .or(metaTileEntities(getValve()).setMaxGlobalLimited(2)))
                .where('T', TANK_PREDICATE.get())
                .build();
    }

    protected static final Supplier<TraceabilityPredicate> TANK_PREDICATE = () -> new TraceabilityPredicate(
            blockWorldState -> {
                IBlockState state = blockWorldState.getBlockState();
                if (GTConsolidateValues.MULTIBLOCK_INTERNAL_TANKS.containsKey(state)) {
                    ITankData tank = GTConsolidateValues.MULTIBLOCK_INTERNAL_TANKS.get(state);
                    // Allow unfilled batteries in the structure, but do not add them to match context.
                    // This lets you use empty batteries as "filler slots" for convenience if desired.
                    if (tank.getTier() != -1 && tank.getCapacity() > 0) {
                        String key = PMC_TANK_HEADER + tank.getTankName();
                        TankMatchWrapper wrapper = blockWorldState.getMatchContext().get(key);
                        if (wrapper == null) wrapper = new TankMatchWrapper(tank);
                        blockWorldState.getMatchContext().set(key, wrapper.increment());
                    }
                    return true;
                }
                return false;
            }, () -> GTConsolidateValues.MULTIBLOCK_INTERNAL_TANKS.entrySet().stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getValue().getTier()))
                    .map(entry -> new BlockInfo(entry.getKey(), null))
                    .toArray(BlockInfo[]::new))
                            .addTooltips("gregtech.multiblock.pattern.error.batteries");

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        List<ITankData> parts = new ArrayList<>();
        for (Map.Entry<String, Object> battery : context.entrySet()) {
            if (battery.getKey().startsWith(PMC_TANK_HEADER) &&
                    battery.getValue() instanceof TankMatchWrapper wrapper) {
                for (int i = 0; i < wrapper.amount; i++) {
                    parts.add(wrapper.partType);
                }
            }
        }
        if (parts.isEmpty()) {
            // only empty batteries found in the structure
            invalidateStructure();
            return;
        }
        /**
         * if (this.tankBank == null) {
         * this.tankBank = new TankBank(parts);
         * } else {
         * this.tankBank = tankBank.rebuild(parts);
         * }
         **/
        parts.forEach(tankType -> this.capacity += tankType.getCapacity());
        // this.capacity = calculateCapacity(parts);
        // this.exportFluids = this.importFluids = new FluidTankList(true, filteredFluidHandlers());
        // this.fluidInventory = this.fluidTankList = new FluidTankList(true, filteredFluidHandlers());
        initializeAbilities();
    }

    protected void initializeAbilities() {
        this.importFluids = this.exportFluids = new FluidTankList(true, filteredFluidHandlers());
        this.fluidInventory = this.fluidTankList = new FluidTankList(true, filteredFluidHandlers());
    }

    private IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    private MetaTileEntity getValve() {
        return MetaTileEntities.STEEL_TANK_VALVE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @NotNull
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
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
                        ITextComponent capacity = TextComponentUtil.stringWithColor(
                                TextFormatting.AQUA,
                                TextFormattingUtil.formatNumbers(getCapacity()));
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gtconsolidate.test.0", capacity));
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
                                    "gtconsolidate.test.3", fluidName, TextFormattingUtil.formatNumbers(amount),
                                    TextFormattingUtil.formatNumbers(tankEntry.getCapacity()));
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
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (isStructureFormed()) {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidInventory);
            } else {
                return null;
            }
        }
        return super.getCapability(capability, side);
    }

    private FilteredFluidHandler[] filteredFluidHandlers() {
        FilteredFluidHandler[] filteredFluidHandlers = new FilteredFluidHandler[getTotalTanks()];
        if (numIntMaxTanks() > 0) {
            for (int i = 0; i < numIntMaxTanks(); i++) {
                filteredFluidHandlers[i] = new FilteredFluidHandler(MAX);
            }
        }
        if (capacityExtraTank() > 0) {
            filteredFluidHandlers[numIntMaxTanks()] = new FilteredFluidHandler(capacityExtraTank());
        }
        return filteredFluidHandlers;
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
     * To resolve this, the overall width of the GUI is increased by +10.
     * </p>
     *
     * <p>
     * Additionally, unnecessary default buttons are removed and replaced with
     * custom buttons specific to this implementation.
     * </p>
     *
     * <p>
     * <b>Main modifications:</b>
     * </p>
     * <ul>
     * <li>Increase the GUI background width by +10.</li>
     * <li>Increase the display area width by +10.</li>
     * <li>Adjust related widgets to follow the expanded display size.</li>
     * <li>Replace unused default buttons with custom buttons.</li>
     * </ul>
     */
    @Override
    protected ModularUI.Builder createUITemplate(EntityPlayer entityPlayer) {
        // Determines the overall GUI size.
        // The second argument (width) is increased by +10.
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 198, 218);

        // === Display ===

        // Text display area.
        // The third argument (width) is increased by +10.
        builder.image(4, 4, 190, 127, GuiTextures.DISPLAY);
        // Logo display area.
        // Since the display width is expanded, the width of IndicatorImageWidget
        // (third argument) is also increased by +10.
        builder.widget(new IndicatorImageWidget(174, 101, 17, 17, getLogo())
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
        // The third argument (width) is increased by +10.
        builder.widget(getPageButton(173, 153, 18, 18));
        // Factor change button.
        // Uses the provided getFlexButton method.
        // The third argument (width) is increased by +10.
        builder.widget(getFlexButton(173, 135, 18, 18));
        // Player inventory.
        // The startY value (second argument) is increased by +10 to follow the expanded display.
        builder.bindPlayerInventory(entityPlayer.inventory, 135);
        return builder;
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
    }

    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound data) {
        super.writeToNBT(data);
        // data.setString("capacity", String.valueOf(this.capacity));
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        // this.capacity = Long.getLong(data.getString("capacity"));
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        // buf.writeString(String.valueOf(this.capacity));
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        // buf.writeString(String.valueOf(this.capacity));
    }

    private static class TankMatchWrapper {

        private final ITankData partType;
        private int amount;

        public TankMatchWrapper(ITankData partType) {
            this.partType = partType;
        }

        public TankMatchWrapper increment() {
            amount++;
            return this;
        }
    }
}
