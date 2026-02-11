package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.*;
import gregtech.api.capability.impl.*;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.GhostCircuitSlotWidget;
import gregtech.api.gui.widgets.SlotWidget;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.items.itemhandlers.GTItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.util.GTHashMaps;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockNotifiablePart;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import kono.ceu.gtconsolidate.api.capability.impl.GTConsolidateDualHandler;
import kono.ceu.gtconsolidate.api.capability.impl.IGTConsolidateDualHandler;
import kono.ceu.gtconsolidate.api.multiblock.GTConsolidateMultiblockAbility;
import kono.ceu.gtconsolidate.api.multiblock.IMultiblockDualAbilityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MetaTileEntityDualHatch extends MetaTileEntityMultiblockNotifiablePart
        implements IMultiblockAbilityPart<IFluidTank>, IControllable, IGhostSlotConfigurable {

    // Item Bus
    @Nullable
    protected GhostCircuitItemStackHandler circuitInventory;
    private IItemHandlerModifiable actualImportItems;
    private boolean autoCollapse;

    private boolean workingEnabled;

    // Fluid Hatch
    private static final int TANK_SIZE = 8000;
    private final int capacity;
    private final int numTanks;
    private final FluidTankList fluidTankList;

    public MetaTileEntityDualHatch(ResourceLocation metaTileEntityId, int tier, boolean isExport) {
        super(metaTileEntityId, tier, isExport);
        this.workingEnabled = true;

        this.capacity = TANK_SIZE * Math.min(Integer.MAX_VALUE, 1 << tier);;
        this.numTanks = 1 + Math.min(GTValues.UHV, tier);

        FluidTank[] fluidsHandlers = new FluidTank[numTanks];
        for (int i = 0; i < fluidsHandlers.length; i++) {
            fluidsHandlers[i] = new NotifiableFluidTank(capacity, this, isExportHatch);
        }
        this.fluidTankList = new FluidTankList(false, fluidsHandlers);

        initializeInventory();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityDualHatch(metaTileEntityId, getTier(), isExportHatch);
    }

    // ==Common Section==
    @Override
    protected void initializeInventory() {
        if (this.fluidTankList == null) return;
        super.initializeInventory();
        if (this.hasGhostCircuitInventory()) {
            this.circuitInventory = new GhostCircuitItemStackHandler(this);
            this.circuitInventory.addNotifiableMetaTileEntity(this);
            this.actualImportItems = new ItemHandlerList(Arrays.asList(super.getImportItems(), this.circuitInventory));
        } else {
            this.actualImportItems = this.importItems;
        }
    }

    @Override
    public void addToMultiBlock(MultiblockControllerBase controllerBase) {
        super.addToMultiBlock(controllerBase);
        if (hasGhostCircuitInventory() && this.actualImportItems instanceof ItemHandlerList) {
            for (IItemHandler handler : ((ItemHandlerList) this.actualImportItems).getBackingHandlers()) {
                if (handler instanceof INotifiableHandler notifiable) {
                    notifiable.addNotifiableMetaTileEntity(controllerBase);
                    notifiable.addToNotifiedList(this, handler, isExportHatch);
                }
            }
        }
    }

    @Override
    public void removeFromMultiBlock(MultiblockControllerBase controllerBase) {
        super.removeFromMultiBlock(controllerBase);
        if (hasGhostCircuitInventory() && this.actualImportItems instanceof ItemHandlerList) {
            for (IItemHandler handler : ((ItemHandlerList) this.actualImportItems).getBackingHandlers()) {
                if (handler instanceof INotifiableHandler notifiable) {
                    notifiable.removeNotifiableMetaTileEntity(controllerBase);
                }
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if (!getWorld().isRemote && getOffsetTimer() % 5 == 0) {
            if (workingEnabled) {
                if (isExportHatch) {
                    pushItemsIntoNearbyHandlers(getFrontFacing());
                    pushFluidsIntoNearbyHandlers(getFrontFacing());
                } else {
                    pullItemsFromNearbyHandlers(getFrontFacing());
                    pullFluidsFromNearbyHandlers(getFrontFacing());
                }
            }
            // Only attempt to auto collapse the inventory contents once the bus has been notified
            if (isAutoCollapse()) {
                // Exclude the ghost circuit inventory from the auto collapse, so it does not extract any ghost circuits
                // from the slot
                IItemHandlerModifiable inventory = (isExportHatch ? this.getExportItems() : super.getImportItems());
                if (isExportHatch ? this.getNotifiedItemOutputList().contains(inventory) :
                        this.getNotifiedItemInputList().contains(inventory)) {
                    collapseInventorySlotContents(inventory);
                }
            }
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        World world = getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(GregtechDataCodes.WORKING_ENABLED, buf -> buf.writeBoolean(workingEnabled));
        }
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);
        if (shouldRenderOverlay()) {
            SimpleOverlayRenderer renderer = isExportHatch ? Textures.PIPE_OUT_OVERLAY : Textures.PIPE_IN_OVERLAY;
            renderer.renderSided(getFrontFacing(), renderState, translation, pipeline);
            SimpleOverlayRenderer overlay = isExportHatch ? Textures.ITEM_HATCH_OUTPUT_OVERLAY :
                    Textures.ITEM_HATCH_INPUT_OVERLAY;
            overlay.renderSided(getFrontFacing(), renderState, translation, pipeline);
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeBoolean(workingEnabled);
        buf.writeBoolean(autoCollapse);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.workingEnabled = buf.readBoolean();
        this.autoCollapse = buf.readBoolean();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("workingEnabled", workingEnabled);
        data.setBoolean("autoCollapse", autoCollapse);
        if (this.circuitInventory != null && !this.isExportHatch) {
            this.circuitInventory.write(data);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("workingEnabled")) {
            this.workingEnabled = data.getBoolean("workingEnabled");
        }
        if (data.hasKey("autoCollapse")) {
            this.autoCollapse = data.getBoolean("autoCollapse");
        }
        if (this.circuitInventory != null && !this.isExportHatch) {
            this.circuitInventory.read(data);
        }
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GregtechDataCodes.TOGGLE_COLLAPSE_ITEMS) {
            this.autoCollapse = buf.readBoolean();
        } else if (dataId == GregtechDataCodes.WORKING_ENABLED) {
            this.workingEnabled = buf.readBoolean();
        }
    }

    // == UI Section ==
    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        int rowSize = (int) Math.sqrt(getItemSize());
        return createUITemplate(entityPlayer, rowSize)
                .build(getHolder(), entityPlayer);
    }

    private ModularUI.Builder createUITemplate(EntityPlayer player, int gridSize) {
        int backgroundWidth = (gridSize > 6 ? 176 + (gridSize - 6) * 18 : 176) + 28;
        int center = backgroundWidth / 2;

        int gridStartX = center - (gridSize * 9);

        int inventoryStartX = center - 9 - 4 * 18;
        int inventoryStartY = 18 + 18 * gridSize + 12;

        int fluidStartX = gridStartX + gridSize * 18;

        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, backgroundWidth, 18 + 18 * gridSize + 94)
                .label(10, 5, getMetaFullName());

        // Item & Fluid
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int index = y * gridSize + x;

                builder.widget(new SlotWidget(isExportHatch ? exportItems : importItems, index,
                        gridStartX + x * 18, 18 + y * 18, true, !isExportHatch)
                        .setBackgroundTexture(GuiTextures.SLOT));
            }
            builder.widget(
                    new TankWidget(fluidTankList.getTankAt(y), fluidStartX, 18 + y * 18, 18, 18)
                            .setBackgroundTexture(GuiTextures.FLUID_SLOT)
                            .setContainerClicking(true, !isExportHatch)
                            .setAlwaysShowFull(true));
        }
        // GhostCircuit
        if (hasGhostCircuitInventory() && this.circuitInventory != null) {
            int circuitX = gridStartX - 28;
            int circuitY = 18 + (gridSize - 1) * 18;

            SlotWidget circuitSlot = new GhostCircuitSlotWidget(circuitInventory, 0, circuitX, circuitY)
                    .setBackgroundTexture(GuiTextures.SLOT, getCircuitSlotOverlay());
            builder.widget(circuitSlot.setConsumer(this::getCircuitSlotTooltip));
        }

        return builder.bindPlayerInventory(player.inventory, GuiTextures.SLOT, inventoryStartX, inventoryStartY);
    }

    // == Item Bus Section ==
    /*@Override
    public void registerAbilities1(List<IItemHandlerModifiable> abilityList) {
        if (this.hasGhostCircuitInventory() && this.actualImportItems != null) {
            abilityList.add(isExportHatch ? this.exportItems : this.actualImportItems);
        } else {
            abilityList.add(isExportHatch ? this.exportItems : this.importItems);
        }
    }

    @Override
    public MultiblockAbility<IItemHandlerModifiable> getAbility1() {
        return isExportHatch ? MultiblockAbility.EXPORT_ITEMS : MultiblockAbility.IMPORT_ITEMS;
    }*/

    protected int getItemSize() {
        int sizeRoot = 1 + Math.min(GTValues.UHV, getTier());
        return sizeRoot * sizeRoot;
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        return this.actualImportItems == null ? super.getImportItems() : this.actualImportItems;
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return isExportHatch ? new NotifiableItemStackHandler(this, getItemSize(), getController(), true) :
                new GTItemStackHandler(this, 0);
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return isExportHatch ? new GTItemStackHandler(this, 0) :
                new NotifiableItemStackHandler(this, getItemSize(), getController(), false);
    }

    @Override
    public boolean hasGhostCircuitInventory() {
        return !this.isExportHatch;
    }

    // Method provided to override
    protected TextureArea getCircuitSlotOverlay() {
        return GuiTextures.INT_CIRCUIT_OVERLAY;
    }

    // Method provided to override
    protected void getCircuitSlotTooltip(@NotNull SlotWidget widget) {
        String configString;
        if (circuitInventory == null || circuitInventory.getCircuitValue() == GhostCircuitItemStackHandler.NO_CONFIG) {
            configString = new TextComponentTranslation("gregtech.gui.configurator_slot.no_value").getFormattedText();
        } else {
            configString = String.valueOf(circuitInventory.getCircuitValue());
        }

        widget.setTooltipText("gregtech.gui.configurator_slot.tooltip", configString);
    }

    private static void collapseInventorySlotContents(IItemHandlerModifiable inventory) {
        // Gather a snapshot of the provided inventory
        Object2IntMap<ItemStack> inventoryContents = GTHashMaps.fromItemHandler(inventory, true);

        List<ItemStack> inventoryItemContents = new ArrayList<>();

        // Populate the list of item stacks in the inventory with apportioned item stacks, for easy replacement
        for (Object2IntMap.Entry<ItemStack> e : inventoryContents.object2IntEntrySet()) {
            ItemStack stack = e.getKey();
            int count = e.getIntValue();
            int maxStackSize = stack.getMaxStackSize();
            while (count >= maxStackSize) {
                ItemStack copy = stack.copy();
                copy.setCount(maxStackSize);
                inventoryItemContents.add(copy);
                count -= maxStackSize;
            }
            if (count > 0) {
                ItemStack copy = stack.copy();
                copy.setCount(count);
                inventoryItemContents.add(copy);
            }
        }

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stackToMove;
            // Ensure that we are not exceeding the List size when attempting to populate items
            if (i >= inventoryItemContents.size()) {
                stackToMove = ItemStack.EMPTY;
            } else {
                stackToMove = inventoryItemContents.get(i);
            }

            // Populate the slots
            inventory.setStackInSlot(i, stackToMove);
        }
    }

    @Override
    public boolean onScrewdriverClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                      CuboidRayTraceResult hitResult) {
        boolean isAttached = false;
        if (this.isAttachedToMultiBlock()) {
            setAutoCollapse(!this.autoCollapse);
            isAttached = true;
        }

        if (!getWorld().isRemote) {
            if (isAttached) {
                if (this.autoCollapse) {
                    playerIn.sendStatusMessage(new TextComponentTranslation("gregtech.bus.collapse_true"), true);
                } else {
                    playerIn.sendStatusMessage(new TextComponentTranslation("gregtech.bus.collapse_false"), true);
                }
            } else {
                playerIn.sendStatusMessage(new TextComponentTranslation("gregtech.bus.collapse.error"), true);
            }
        }
        return true;
    }

    public boolean isAutoCollapse() {
        return autoCollapse;
    }

    public void setAutoCollapse(boolean inverted) {
        autoCollapse = inverted;
        if (!getWorld().isRemote) {
            if (autoCollapse) {
                if (isExportHatch) {
                    addNotifiedOutput(this.getExportItems());
                } else {
                    addNotifiedInput(super.getImportItems());
                }
            }
            writeCustomData(GregtechDataCodes.TOGGLE_COLLAPSE_ITEMS,
                    packetBuffer -> packetBuffer.writeBoolean(autoCollapse));
            notifyBlockUpdate();
            markDirty();
        }
    }

    @Override
    public void setGhostCircuitConfig(int config) {
        if (this.circuitInventory == null || this.circuitInventory.getCircuitValue() == config) {
            return;
        }
        this.circuitInventory.setCircuitValue(config);
        if (!getWorld().isRemote) {
            markDirty();
        }
    }

    public IItemHandlerModifiable getInventory() {
        if (this.hasGhostCircuitInventory() && this.actualImportItems != null) {
            return isExportHatch ? this.exportItems : this.actualImportItems;
        } else {
            return isExportHatch ? this.exportItems : this.importItems;
        }
    }

    // == Fluid Hatch Section ==
    @Override
    public void registerAbilities(List<IFluidTank> abilityList) {
        abilityList.addAll(fluidTankList.getFluidTanks());
    }

    @Override
    public MultiblockAbility<IFluidTank> getAbility() {
        return isExportHatch ? GTConsolidateMultiblockAbility.EXPORT_DUAL : GTConsolidateMultiblockAbility.IMPORT_DUAL;
    }

    @Override
    protected FluidTankList createImportFluidHandler() {
        return isExportHatch ? new FluidTankList(false) : fluidTankList;
    }

    @Override
    protected FluidTankList createExportFluidHandler() {
        return isExportHatch ? fluidTankList : new FluidTankList(false);
    }
}
