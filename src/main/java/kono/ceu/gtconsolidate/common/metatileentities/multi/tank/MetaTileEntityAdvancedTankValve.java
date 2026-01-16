package kono.ceu.gtconsolidate.common.metatileentities.multi.tank;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;

import gregtech.api.util.TextComponentUtil;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

public class MetaTileEntityAdvancedTankValve extends MetaTileEntityMultiblockPart
                                             implements IMultiblockAbilityPart<IFluidHandler> {

    private static final int MIN_TANK = 0;
    private int targetTank = 0;
    private int maxTank;

    public MetaTileEntityAdvancedTankValve(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 0);
        this.targetTank = 1;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAdvancedTankValve(metaTileEntityId);
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
        if (controllerBase instanceof MetaTileEntityMultiblockLargeTank) {
            this.fluidInventory = ((MetaTileEntityMultiblockLargeTank) controllerBase)
                    .getFluidInventoryFromIndex(this.targetTank);
            this.maxTank = ((MetaTileEntityMultiblockLargeTank) controllerBase).getTotalTanks();
        } else {
            this.fluidInventory = controllerBase.getFluidInventory(); // directly use controllers fluid inventory as
                                                                      // there
            // is no reason to proxy it
        }
    }

    @Override
    public void removeFromMultiBlock(MultiblockControllerBase controllerBase) {
        super.removeFromMultiBlock(controllerBase);
        initializeDummyInventory();
    }

    public int getTargetTank() {
        return targetTank;
    }

    public void setTargetTank(int amount) {
        this.targetTank = MathHelper.clamp(this.targetTank + amount, 1, this.maxTank);
        addToMultiBlock(getController());
    }

    @Override
    protected ModularUI createUI(@NotNull EntityPlayer entityPlayer) {
        ServerWidgetGroup targetPageGroup = new ServerWidgetGroup(() -> true);
        targetPageGroup.addWidget(new ImageWidget(62, 36, 53, 20, GuiTextures.DISPLAY)
                .setTooltip("gtconsolidate.machine.advanced_tank_valve.display"));

        targetPageGroup.addWidget(new IncrementButtonWidget(118, 36, 30, 20, 1, 4, 16, 64, this::setTargetTank)
                .setDefaultTooltip()
                .setShouldClientCallback(false));
        targetPageGroup
                .addWidget(new IncrementButtonWidget(29, 36, 30, 20, -1, -4, -16, -64, this::setTargetTank)
                        .setDefaultTooltip()
                        .setShouldClientCallback(false));

        targetPageGroup.addWidget(new TextFieldWidget2(63, 42, 51, 20, this::getParallelAmountToString, val -> {
            if (val != null && !val.isEmpty()) {
                setTargetTank(Integer.parseInt(val));
            }
        })
                .setCentered(true)
                .setNumbersOnly(1, this.maxTank)
                .setMaxLength(3)
                .setValidator(getTextFieldValidator(() -> this.maxTank)));

        return ModularUI.defaultBuilder()
                .widget(new LabelWidget(5, 5, getMetaFullName()))
                .widget(targetPageGroup)
                .widget(new AdvancedTextWidget(29, 60, this::addDisplayText, 4210752))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 0)
                .build(getHolder(), entityPlayer);
    }

    public String getParallelAmountToString() {
        return Integer.toString(this.targetTank);
    }

    private void addDisplayText(List<ITextComponent> textList) {
        FluidStack fluidStack = this.fluidInventory.drain(1, false);
        ITextComponent fluidName = TextComponentUtil.stringWithColor(
                fluidStack != null ? TextFormatting.AQUA : TextFormatting.YELLOW,
                fluidStack != null ? fluidStack.getLocalizedName() : I18n.format("gtconsolidate.universal.empty"));
        textList.add(new TextComponentTranslation("gtconsolidate.machine.advanced_tank_valve.fluid", this.targetTank, fluidName));
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
        tooltip.add(I18n.format("gregtech.tank_valve.tooltip"));
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
        data.setInteger("targetTank", this.targetTank);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.targetTank = data.getInteger("targetTank");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.targetTank);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.targetTank = buf.readInt();
    }
}
