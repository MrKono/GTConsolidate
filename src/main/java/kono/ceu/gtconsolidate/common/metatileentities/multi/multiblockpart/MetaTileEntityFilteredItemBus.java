package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.capability.impl.GhostCircuitItemStackHandler;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.ItemStackHashStrategy;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityItemBus;

public class MetaTileEntityFilteredItemBus extends MetaTileEntityItemBus {

    protected @Nullable GhostCircuitItemStackHandler circuitInventory;
    private IItemHandlerModifiable actualImportItems;
    private FilteredImportHandler filteredImportHandler;

    public MetaTileEntityFilteredItemBus(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier, false);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityFilteredItemBus(metaTileEntityId, getTier());
    }

    @Override
    protected void initializeInventory() {
        this.filteredImportHandler = new FilteredImportHandler(this, getInventorySize(), getController());
        this.circuitInventory = new GhostCircuitItemStackHandler(this);
        this.actualImportItems = new ItemHandlerList(Arrays.asList(
                this.filteredImportHandler, this.circuitInventory));
        super.initializeInventory();
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        return this.actualImportItems;
    }

    @Override
    protected IItemHandlerModifiable createImportItemHandler() {
        return new FilteredImportHandler(this, getInventorySize(), getController());
    }

    private int getInventorySize() {
        int sizeRoot = 1 + Math.min(9, getTier());
        return sizeRoot * sizeRoot;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip,
                               boolean advanced) {
        tooltip.add(I18n.format("gregtech.machine.item_bus.import.tooltip"));
        tooltip.add(I18n.format("gtconsolidate.machine.filtered_item_bus.import.tooltip"));
        tooltip.add(I18n.format("gregtech.universal.tooltip.item_storage_capacity", getInventorySize()));
        tooltip.add(I18n.format("gregtech.universal.enabled"));
    }

    public static class FilteredImportHandler extends NotifiableItemStackHandler {

        public FilteredImportHandler(MetaTileEntity metaTileEntity, int slots, MetaTileEntity entityToNotify) {
            super(metaTileEntity, slots, entityToNotify, false);
        }

        @NotNull
        @Override
        // Insert item returns the remainder stack that was not inserted
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            // If the item was not valid, nothing from the stack can be inserted
            if (!isItemValid(slot, stack)) {
                return stack;
            }

            // Return Empty if passed Empty
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            int amountInSlot = this.getStackInSlot(slot).getCount();
            int slotLimit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
            int spaceAvailable = slotLimit - amountInSlot;

            // Insert the minimum amount between the amount of space available and the amount being inserted
            int amountToInsert = Math.min(spaceAvailable, stack.getCount());

            // If the current stack size in the slot is greater than the limit of the Multiblock, nothing can be
            // inserted
            if (amountToInsert <= 0) {
                return stack;
            }

            ItemStack existingStack = this.getStackInSlot(slot);
            // Slot is not empty; only allow insertion if same item
            if (!existingStack.isEmpty()) {
                if (!ItemStackHashStrategy.comparingAllButCount().equals(stack, existingStack)) {
                    return stack;
                }
            } else {
                // Slot is empty; check that no other slot contains mismatched item
                for (int i = 0; i < getSlots(); i++) {
                    ItemStack otherStack = this.getStackInSlot(i);
                    if (!otherStack.isEmpty() &&
                            !ItemStackHashStrategy.comparingAllButCount().equals(stack, otherStack)) {
                        return stack;
                    }
                }
            }

            // The remainder that was not inserted
            int remainderAmount = stack.getCount() - amountToInsert;

            // Handle any remainder
            ItemStack remainder = ItemStack.EMPTY;

            if (remainderAmount > 0) {
                remainder = stack.copy();
                remainder.setCount(remainderAmount);
            }

            if (!simulate) {
                // Perform the actual insertion
                ItemStack temp = stack.copy();
                temp.setCount(amountInSlot + amountToInsert);
                this.setStackInSlot(slot, temp);
            }

            return remainder;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return this.getStackInSlot(slot).isEmpty() ||
                    ItemStackHashStrategy.comparingAllButCount().equals(this.getStackInSlot(slot), stack);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }
    }
}
