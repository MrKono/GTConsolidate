package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.INTAKE_HATCH_DIMENSION_MAPPING;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.FilteredItemHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.NotifiableFluidTank;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.util.TextComponentUtil;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockNotifiablePart;

import kono.ceu.gtconsolidate.client.GTConsolidateTextures;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;

public class MetaTileEntityAdvancedIntakeHatch extends MetaTileEntityMultiblockNotifiablePart
                                               implements IMultiblockAbilityPart<IFluidTank> {

    private final int capacity;
    private final int fillAmount;
    private final FluidTank fluidTank;
    private Fluid fluid;
    private final int tier;
    private int dimensionId;
    private static final int MAX = Integer.MAX_VALUE;
    private static final int MIN = Integer.MIN_VALUE;

    public MetaTileEntityAdvancedIntakeHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, GTValues.ZPM, false);
        this.tier = tier;
        this.capacity = (int) (80000 * Math.pow(2, tier));
        this.fillAmount = (int) (8000 * Math.pow(2, tier));
        this.fluidTank = new NotifiableFluidTank(this.capacity, this, false);
        this.dimensionId = 0;
        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityAdvancedIntakeHatch(metaTileEntityId, this.tier);
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            if (isFirstTick()) {
                this.fluid = getMaterial().getFluid(FluidStorageKeys.GAS);
            }
            if (getOffsetTimer() % 20 == 0 && isExposeAir() && getController() != null) {
                int fillAmount = fluidTank.fill(new FluidStack(fluid, getFillAmount()), true);
            }
        }
        fillContainerFromInternalTank(fluidTank);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                GTConsolidateTextures.INTAKE_HATCH.renderSided(facing, renderState, translation, pipeline);
            }
        }
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        }
        return super.getCapability(capability, side);
    }

    private int getInventorySize() {
        return this.capacity;
    }

    private int getFillAmount() {
        return this.fillAmount;
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        return new FluidTankList(false, fluidTank);
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new FilteredItemHandler(this).setFillPredicate(
                FilteredItemHandler.getCapabilityFilter(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY));
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new ItemStackHandler(1);
    }

    @Override
    public MultiblockAbility<IFluidTank> getAbility() {
        return MultiblockAbility.IMPORT_FLUIDS;
    }

    @Override
    public void registerAbilities(List<IFluidTank> abilityList) {
        abilityList.add(fluidTank);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return createTankUI(fluidTank, getMetaFullName(), entityPlayer).build(getHolder(), entityPlayer);
    }

    public ModularUI.Builder createTankUI(IFluidTank fluidTank, String title, EntityPlayer entityPlayer) {
        ModularUI.Builder builder = new ModularUI.Builder(GuiTextures.BACKGROUND, 196, 216);

        TankWidget tankWidget;
        tankWidget = new TankWidget(fluidTank, 5, 30, 30, 96)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setDrawHoveringText(false)
                .setContainerClicking(false, false);

        ServerWidgetGroup setDimensionGroup = new ServerWidgetGroup(() -> true);
        setDimensionGroup.addWidget(new ImageWidget(74, 65, 77, 20, GuiTextures.DISPLAY)
                .setTooltip("gtconsolidate.intake_hatch.dim.tooltip"));
        setDimensionGroup.addWidget(new IncrementButtonWidget(156, 65, 30, 20, 1, 4, 16, 64, this::setDimensionId)
                .setDefaultTooltip()
                .setShouldClientCallback(false));
        setDimensionGroup
                .addWidget(new IncrementButtonWidget(40, 65, 30, 20, -1, -4, -16, -64, this::setDimensionId)
                        .setDefaultTooltip()
                        .setShouldClientCallback(false));
        setDimensionGroup.addWidget(new TextFieldWidget2(75, 70, 75, 20, this::getDimensionIdToString, val -> {
            if (val != null && !val.isEmpty()) {
                setDimensionId(Integer.parseInt(val) - getDimensionId());
            }
        })
                .setCentered(true)
                .setNumbersOnly(MIN, MAX)
                .setMaxLength(11)
                .setValidator(getTextFieldValidator(() -> MAX)));

        // Add general widgets
        return builder.widget(new AdvancedTextWidget(5, 5,
                textList -> textList.add(new TextComponentTranslation(I18n.format(title))), 0xFFFFFF)
                        .setMaxWidthLimit(180))
                .widget(tankWidget)
                .label(39, 30, "gtconsolidate.universal.status", 0xFFFFFF)
                .widget(new AdvancedTextWidget(44, 40, this::addStatusLine, 0xFFFFFF))
                .label(39, 55, "gtconsolidate.intake_hatch.dim", 0xFFFFFF)
                .widget(setDimensionGroup)
                .widget(new AdvancedTextWidget(49, 95, this::addDimensionLine, 0xFFFFFF).setMaxWidthLimit(150))
                .widget(new AdvancedTextWidget(39, 110, text -> getCollectingFluidText(tankWidget, text), 0xFFFFFF))
                .widget(new AdvancedTextWidget(49, 120, getFluidAmountText(tankWidget), 0xFFFFFF))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 17, 134);
    }

    private void getCollectingFluidText(TankWidget tankWidget, List<ITextComponent> textList) {
        TextComponentTranslation translation = tankWidget.getFluidTextComponent();
        if (translation != null) {
            ITextComponent text = TextComponentUtil.translationWithColor(
                    TextFormatting.AQUA, "%s", translation);
            textList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.WHITE,
                    "gtconsolidate.intake_hatch.collecting_fluid", text));
        }
    }

    private Consumer<List<ITextComponent>> getFluidAmountText(TankWidget tankWidget) {
        return (list) -> {
            String fluidAmount = tankWidget.getFormattedFluidAmount();
            String capacity = String.format("%,d", getInventorySize());
            if (!fluidAmount.isEmpty()) {
                list.add(new TextComponentString(fluidAmount + " L / " + capacity + " L"));
            }
        };
    }

    private void addStatusLine(List<ITextComponent> textList) {
        ITextComponent status;
        if (getController() == null) {
            status = TextComponentUtil.translationWithColor(
                    TextFormatting.RED, "gtconsolidate.intake_hatch.controller_null");
        } else if (!isExposeAir()) {
            status = TextComponentUtil.translationWithColor(
                    TextFormatting.YELLOW, "gtconsolidate.intake_hatch.blocked");
        } else {
            status = TextComponentUtil.translationWithColor(
                    TextFormatting.AQUA, "gregtech.multiblock.running");
        }
        textList.add(status);
    }

    public void setDimensionId(int amount) {
        this.dimensionId = MathHelper.clamp(this.dimensionId + amount, MIN, MAX);
        this.fluidTank.drain(getInventorySize(), true);
        this.fluid = getMaterial().getFluid(FluidStorageKeys.GAS);
        markDirty();
    }

    private String getDimensionIdToString() {
        return Integer.toString(this.dimensionId);
    }

    private void addDimensionLine(List<ITextComponent> textList) {
        int dimId = getDimensionId();
        ITextComponent name;
        DimensionType type;
        try {
            type = DimensionType.getById(dimId);
            name = TextComponentUtil.stringWithColor(TextFormatting.AQUA, type.getName());
        } catch (IllegalArgumentException e) {
            name = TextComponentUtil.translationWithColor(TextFormatting.YELLOW,
                    "gtconsolidate_intake_hatch.dim_unregistered");
        }
        textList.add(TextComponentUtil.translationWithColor(
                TextFormatting.WHITE, "gtconsolidate_intake_hatch.dim_target", name, dimId));
    }

    public static @NotNull Function<String, String> getTextFieldValidator(IntSupplier maxSupplier) {
        return val -> {
            if (val.isEmpty())
                return String.valueOf(0);
            int max = maxSupplier.getAsInt();
            int num;
            try {
                num = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
                return String.valueOf(max);
            }
            if (num < MIN + 1)
                return String.valueOf(MIN);
            if (num > MAX - 1)
                return String.valueOf(MAX);
            return val;
        };
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("gtconsolidate.intake_hatch.tooltip.1"));
        tooltip.add(I18n.format("gtconsolidate.intake_hatch.tooltip.2"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.fluid_storage_capacity", getInventorySize()));
        tooltip.add(I18n.format("gtconsolidate.intake_hatch.collection_rate", getFillAmount()));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
    }

    @Override
    public void addToolUsages(ItemStack stack, @Nullable World world, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
        super.addToolUsages(stack, world, tooltip, advanced);
    }

    private int getDimensionId() {
        return this.dimensionId;
    }

    private Material getMaterial() {
        if (INTAKE_HATCH_DIMENSION_MAPPING.containsKey(getDimensionId())) {
            return INTAKE_HATCH_DIMENSION_MAPPING.get(getDimensionId());
        }
        return Materials.Air;
    }

    private boolean isExposeAir() {
        World world = this.getWorld();
        BlockPos pos = this.getPos();
        if (world == null || pos == null) {
            return false;
        }
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(facing))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("dim", this.dimensionId);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.dimensionId = data.getInteger("dim");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(this.dimensionId);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.dimensionId = buf.readInt();
        scheduleRenderUpdate();
    }
}
