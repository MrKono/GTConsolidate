package kono.ceu.gtconsolidate.common.metatileentities.multi.tank;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.capability.impl.FluidHandlerProxy;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.util.GTTransferUtils;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;

public class MetaTileEntityQuadrupleTankValve extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<IFluidHandler> {

    private static final int SELECTABLE_TANKS = 4;
    private int[] selectableTanks = new int[SELECTABLE_TANKS];
    private static final int MIN_TANK = 0;
    private int maxTank;

    public MetaTileEntityQuadrupleTankValve(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 0);
        Arrays.fill(selectableTanks, 1);
    }

    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityQuadrupleTankValve(metaTileEntityId);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        Textures.PIPE_IN_OVERLAY.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }

    @Override
    public ICubeRenderer getBaseTexture() {
        if (getController() == null) {
            return Textures.ROBUST_TUNGSTENSTEEL_CASING;
        }
        return super.getBaseTexture();
    }

    @Override
    public int getDefaultPaintingColor() {
        return 0xFFFFFF;
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && getOffsetTimer() % 5 == 0L && isAttachedToMultiBlock() &&
                getFrontFacing() == EnumFacing.DOWN) {
            TileEntity tileEntity = getNeighbor(getFrontFacing());
            IFluidHandler fluidHandler = tileEntity == null ? null : tileEntity
                    .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, getFrontFacing().getOpposite());
            if (fluidHandler != null) {
                GTTransferUtils.transferFluids(fluidInventory, fluidHandler);
            }
        }
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        initializeDummyInventory();
    }

    /**
     * When this block is not connected to any multiblock it uses dummy inventory to prevent problems with capability
     * checks
     */
    private void initializeDummyInventory() {
        this.fluidInventory = new FluidHandlerProxy(new FluidTankList(false), new FluidTankList(false));
    }

    @Override
    public void addToMultiBlock(MultiblockControllerBase controllerBase) {
        super.addToMultiBlock(controllerBase);
        // directly use controllers fluid inventory as
        // there is no reason to proxy it
        setFluidHandler(controllerBase);
        if (controllerBase instanceof MetaTileEntityMultiblockLargeTank) {
            this.maxTank = ((MetaTileEntityMultiblockLargeTank) controllerBase).getTotalTanks();
        }
    }

    public void setFluidHandler(MultiblockControllerBase controllerBase) {
        if (controllerBase instanceof MetaTileEntityMultiblockLargeTank) {
            IFluidTank[] fluidTanks = new IFluidTank[selectableTanks.length];
            for (int i = 0; i < selectableTanks.length; i++) {
                fluidTanks[i] = ((MetaTileEntityMultiblockLargeTank) controllerBase).getFluidTankFromIndex(selectableTanks[i]);
            }
            this.fluidInventory = new FluidTankList(true, fluidTanks);
        } else {
            this.fluidInventory = controllerBase.getFluidInventory();
        }
    }

    public void setTargetTankByNumber(int amount, int tankNumber) {
        int index = tankNumber - 1;
        if (index < 0 || index >= selectableTanks.length) return;

        selectableTanks[index] =
                MathHelper.clamp(selectableTanks[index] + amount, 1, this.maxTank);

        setFluidHandler(this.getController());
    }

    public int getTargetTankByNumber(int tankNumber) {
        int index = tankNumber - 1;
        if (index < 0 || index >= selectableTanks.length) return 0;
        return selectableTanks[index];
    }

    public String getTargetTankToStringByNumber(int tankNumber) {
        return Integer.toString(getTargetTankByNumber(tankNumber));
    }

    @Override
    protected ModularUI createUI(@NotNull EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 223, 274)
                .widget(new LabelWidget(5, 5, getMetaFullName()));

        int yPos = 15;
        for (int i = 1; i < 5; i++) {
            builder.widget(createSelectorWidget(77, yPos, i));
            yPos += 22;
            int j = i;
            builder.widget(new AdvancedTextWidget(5, yPos, textList -> addDisplayText(textList, j), 4210752).setMaxWidthLimit(203));
            yPos += 22;
        }
        builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 30, yPos);

        return builder.build(getHolder(), entityPlayer);
    }

    public void addDisplayText(List<ITextComponent> textList, int targetTankNo) {
        FluidStack fluidStack =  ((MetaTileEntityMultiblockLargeTank) getController()).getFluidTankFromIndex(selectableTanks[targetTankNo - 1]).getFluid();
        ITextComponent fluidName = TextComponentUtil.stringWithColor(
                fluidStack != null ? TextFormatting.AQUA : TextFormatting.YELLOW,
                fluidStack != null ? fluidStack.getLocalizedName() : I18n.format("gtconsolidate.universal.empty"));
        textList.add(new TextComponentTranslation("gtconsolidate.machine.advanced_tank_valve.fluid.1", selectableTanks[targetTankNo - 1], fluidName));
    }

    public ServerWidgetGroup createSelectorWidget(int xPos, int yPos, int targetTankNo) {
        int textWidth = 51;
        int buttonWidth = 30;
        int displayWidth = 53;
        int widgetHeight = 20;

        ServerWidgetGroup selector = new ServerWidgetGroup(() -> true);
        selector.addWidget(new ImageWidget(xPos, yPos, displayWidth, widgetHeight, GuiTextures.DISPLAY)
                .setTooltip("gtconsolidate.machine.advanced_tank_valve.display"));
        selector.addWidget(new IncrementButtonWidget(xPos + displayWidth + 3 , yPos, buttonWidth, widgetHeight, 1, 4, 16, 64, amount -> setTargetTankByNumber(amount, targetTankNo))
                .setDefaultTooltip()
                .setShouldClientCallback(false));
        selector.addWidget(new IncrementButtonWidget(xPos - buttonWidth + 3, yPos, buttonWidth, widgetHeight, -1, -4, -16, -64, amount -> setTargetTankByNumber(amount, targetTankNo))
                .setDefaultTooltip()
                .setShouldClientCallback(false));
        selector.addWidget(new TextFieldWidget2(xPos + 1, yPos + 6, textWidth, widgetHeight, () -> getTargetTankToStringByNumber(targetTankNo), val -> {
            if (val != null && !val.isEmpty()) {
                setTargetTankByNumber(targetTankNo, Integer.parseInt(val));
            }
        })
                .setCentered(true)
                .setNumbersOnly(1, this.maxTank)
                .setMaxLength(3)
                .setValidator(getTextFieldValidator(() -> this.maxTank)));
        return selector;
    }

    public static @NotNull Function<String, String> getTextFieldValidator(IntSupplier maxSupplier) {
        return val -> {
            if (val.isEmpty())
                return String.valueOf(MIN_TANK);
            int max = maxSupplier.getAsInt();
            int num;
            try {
                num = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
                return String.valueOf(max);
            }
            if (num < MIN_TANK)
                return String.valueOf(MIN_TANK);
            if (num > max)
                return String.valueOf(max);
            return val;
        };
    }

    @Override
    protected boolean openGUIOnRightClick() {
        return isAttachedToMultiBlock();
    }

    @Override
    public boolean canPartShare() {
        return false;
    }

    @Override
    public MultiblockAbility<IFluidHandler> getAbility() {
        return MultiblockAbility.TANK_VALVE;
    }

    @Override
    public void registerAbilities(@NotNull List<IFluidHandler> abilityList) {
        abilityList.add(this.getImportFluids());
    }

    @Override
    protected boolean shouldSerializeInventories() {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gtconsolidate.machine.advanced_tank_valve.tooltip"));
        tooltip.add(I18n.format("gtconsolidate.machine.advanced_tank_valve.tooltip.selectable", SELECTABLE_TANKS));
    }

    @Override
    public boolean needsSneakToRotate() {
        return true;
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        tooltip.add(I18n.format("gregtech.tool_action.wrench.set_facing"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }


    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound data) {
        data.setIntArray("selectableTanks", this.selectableTanks);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.selectableTanks = data.getIntArray("selectableTanks");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeVarIntArray(this.selectableTanks);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.selectableTanks = buf.readVarIntArray();
    }
}
