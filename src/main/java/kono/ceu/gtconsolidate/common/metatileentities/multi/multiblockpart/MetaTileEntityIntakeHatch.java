package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart;

import static kono.ceu.gtconsolidate.api.util.GTConsolidateValues.INTAKE_HATCH_DIMENSION_MAPPING;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
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

public class MetaTileEntityIntakeHatch extends MetaTileEntityMultiblockNotifiablePart
                                       implements IMultiblockAbilityPart<IFluidTank> {

    private final int capacity;
    private final int fillAmount;
    private final FluidTank fluidTank;
    private Fluid fluid;
    private final int tier;

    public MetaTileEntityIntakeHatch(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, GTValues.IV, false);
        this.tier = tier;
        this.capacity = (int) (80000 * Math.pow(2, tier));
        this.fillAmount = (int) (8000 * Math.pow(2, tier));
        this.fluidTank = new NotifiableFluidTank(this.capacity, this, false);
        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityIntakeHatch(metaTileEntityId, this.tier);
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote) {
            if (isFirstTick()) {
                this.fluid = getMaterial().getFluid(FluidStorageKeys.GAS);
            }
            if (getOffsetTimer() % 20 == 0 && isExposeAir() && getController() != null) {
                fluidTank.fill(new FluidStack(fluid, getFillAmount()), true);
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
        ModularUI.Builder builder = new ModularUI.Builder(GuiTextures.BACKGROUND, 196, 166);

        TankWidget tankWidget;
        tankWidget = new TankWidget(fluidTank, 5, 15, 30, 61)
                .setBackgroundTexture(GuiTextures.SLOT)
                .setDrawHoveringText(false)
                .setContainerClicking(false, false);

        // Add general widgets
        return builder.label(5, 5, title)
                .widget(tankWidget)
                .label(39, 15, "gtconsolidate.universal.status", 0xFFFFFF)
                .widget(new AdvancedTextWidget(44, 25, this::addStatusLine, 0xFFFFFF))
                .widget(new AdvancedTextWidget(39, 40, text -> getCollectingFluidText(tankWidget, text), 0xFFFFFF))
                .widget(new AdvancedTextWidget(49, 50, getFluidAmountText(tankWidget), 0xFFFFFF))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 17, 84);
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
        return this.getWorld().provider.getDimension();
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
}
