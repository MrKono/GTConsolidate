package kono.ceu.gtconsolidate.api.capability.impl;

import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.INotifiableHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.ItemStackHashStrategy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GTConsolidateDualHandler implements IItemHandlerModifiable, IMultipleTankHandler, INotifiableHandler {

    @NotNull
    private static final ItemStackHashStrategy strategy = ItemStackHashStrategy.comparingAll();
    @NotNull
    protected IItemHandlerModifiable itemDelegate;
    @NotNull
    protected IMultipleTankHandler fluidDelegate;
    private final MultiFluidTankEntry[] fluidTanks;
    private final boolean allowSameFluidFill;
    private boolean isExport;

    List<MetaTileEntity> notifiableEntities = new ArrayList<>();

    public GTConsolidateDualHandler(@NotNull IItemHandlerModifiable itemDelegate,
                                    @NotNull IMultipleTankHandler fluidDelegate,
                                    boolean isExport, boolean allowSameFluidFill, IFluidTank... fluidTanks) {
        this.itemDelegate = itemDelegate;
        this.fluidDelegate = fluidDelegate;
        this.isExport = isExport;
        ArrayList<MultiFluidTankEntry> list = new ArrayList<>();
        for (IFluidTank tank : fluidTanks) list.add(wrapIntoEntry(tank));
        this.fluidTanks = list.toArray(new MultiFluidTankEntry[0]);
        this.allowSameFluidFill = allowSameFluidFill;
    }

    public GTConsolidateDualHandler(@NotNull IItemHandlerModifiable itemDelegate,
                                    @NotNull IMultipleTankHandler fluidDelegate,
                                    boolean isExport, boolean allowSameFluidFill, @NotNull List<? extends IFluidTank> fluidTanks) {

        this.itemDelegate = itemDelegate;
        this.fluidDelegate = fluidDelegate;
        this.isExport = isExport;
        ArrayList<MultiFluidTankEntry> list = new ArrayList<>();
        for (IFluidTank tank : fluidTanks) list.add(wrapIntoEntry(tank));
        this.fluidTanks = list.toArray(new MultiFluidTankEntry[0]);
        this.allowSameFluidFill = allowSameFluidFill;
    }

    public GTConsolidateDualHandler(@NotNull IItemHandlerModifiable itemDelegate,
                                    @NotNull IMultipleTankHandler fluidDelegate,
                                    boolean isExport, boolean allowSameFluidFill, @NotNull IMultipleTankHandler parent,
                         IFluidTank... additionalTanks) {
        this.itemDelegate = itemDelegate;
        this.fluidDelegate = fluidDelegate;
        this.isExport = isExport;
        ArrayList<MultiFluidTankEntry> list = new ArrayList<>(parent.getFluidTanks());
        for (IFluidTank tank : additionalTanks) list.add(wrapIntoEntry(tank));
        this.fluidTanks = list.toArray(new MultiFluidTankEntry[0]);
        this.allowSameFluidFill = allowSameFluidFill;
    }

    private MultiFluidTankEntry wrapIntoEntry(IFluidTank tank) {
        return tank instanceof MultiFluidTankEntry entry ? entry : new MultiFluidTankEntry(this, tank);
    }


    // IMultipleTankHandler
    @Override
    public @NotNull List<MultiFluidTankEntry> getFluidTanks() {
        return Collections.unmodifiableList(Arrays.asList(fluidTanks));
    }

    @Override
    public int getTanks() {
        return fluidTanks.length;
    }

    @Override
    public @NotNull MultiFluidTankEntry getTankAt(int index) {
        return fluidTanks[index];
    }

    @Override
    public boolean allowSameFluidFill() {
        return allowSameFluidFill;
    }


    @Override
    public IFluidTankProperties[] getTankProperties() {
        ArrayList<IFluidTankProperties> propertiesList = new ArrayList<>();
        for (MultiFluidTankEntry fluidTank : fluidTanks) {
            Collections.addAll(propertiesList, fluidTank.getTankProperties());
        }
        return propertiesList.toArray(new IFluidTankProperties[0]);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }
        int totalInserted = 0;
        boolean inputFluidCopied = false;
        // flag value indicating whether the fluid was stored in 'distinct' slot at least once
        boolean distinctSlotVisited = false;

        MultiFluidTankEntry[] fluidTanks = this.fluidTanks.clone();
        Arrays.sort(fluidTanks, IMultipleTankHandler.ENTRY_COMPARATOR);

        // search for tanks with same fluid type first
        for (MultiFluidTankEntry tank : fluidTanks) {
            // if the fluid to insert matches the tank, insert the fluid
            if (resource.isFluidEqual(tank.getFluid())) {
                int inserted = tank.fill(resource, doFill);
                if (inserted > 0) {
                    totalInserted += inserted;
                    if (resource.amount - inserted <= 0) {
                        return totalInserted;
                    }
                    if (!inputFluidCopied) {
                        inputFluidCopied = true;
                        resource = resource.copy();
                    }
                    resource.amount -= inserted;
                }
                // regardless of whether the insertion succeeded, presence of identical fluid in
                // a slot prevents distinct fill to other slots
                if (!tank.allowSameFluidFill()) {
                    distinctSlotVisited = true;
                }
            }
        }
        // if we still have fluid to insert, loop through empty tanks until we find one that can accept the fluid
        for (MultiFluidTankEntry tank : fluidTanks) {
            // if the tank uses distinct fluid fill (allowSameFluidFill disabled) and another distinct tank had
            // received the fluid, skip this tank
            boolean usesDistinctFluidFill = tank.allowSameFluidFill();
            if ((usesDistinctFluidFill || !distinctSlotVisited) && tank.getFluidAmount() == 0) {
                int inserted = tank.fill(resource, doFill);
                if (inserted > 0) {
                    totalInserted += inserted;
                    if (resource.amount - inserted <= 0) {
                        return totalInserted;
                    }
                    if (!inputFluidCopied) {
                        inputFluidCopied = true;
                        resource = resource.copy();
                    }
                    resource.amount -= inserted;
                    if (!usesDistinctFluidFill) {
                        distinctSlotVisited = true;
                    }
                }
            }
        }
        // return the amount of fluid that was inserted
        return totalInserted;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return null;
        }
        int amountLeft = resource.amount;
        FluidStack totalDrained = null;
        for (IFluidTank handler : fluidTanks) {
            if (!resource.isFluidEqual(handler.getFluid())) {
                continue;
            }
            FluidStack drain = handler.drain(amountLeft, doDrain);
            if (drain != null) {
                if (totalDrained == null) {
                    totalDrained = drain;
                } else {
                    totalDrained.amount += drain.amount;
                }
                amountLeft -= drain.amount;
                if (amountLeft <= 0) {
                    return totalDrained;
                }
            }
        }
        return totalDrained;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }
        FluidStack totalDrained = null;
        for (IFluidTank handler : fluidTanks) {
            if (totalDrained == null) {
                totalDrained = handler.drain(maxDrain, doDrain);
                if (totalDrained != null) {
                    maxDrain -= totalDrained.amount;
                }
            } else {
                if (!totalDrained.isFluidEqual(handler.getFluid())) {
                    continue;
                }
                FluidStack drain = handler.drain(maxDrain, doDrain);
                if (drain != null) {
                    totalDrained.amount += drain.amount;
                    maxDrain -= drain.amount;
                }
            }
            if (maxDrain <= 0) {
                return totalDrained;
            }
        }
        return totalDrained;
    }

    // IItemHandlerModifiable
    @Override
    public int getSlots() {
        return itemDelegate.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return itemDelegate.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        var remainder = itemDelegate.insertItem(slot, stack, simulate);
        if (!simulate && !strategy.equals(remainder, stack))
            onContentsChanged();
        return remainder;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        var extracted = itemDelegate.extractItem(slot, amount, simulate);
        if (!simulate && !extracted.isEmpty())
            onContentsChanged();
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemDelegate.getSlotLimit(slot);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        var oldStack = itemDelegate.getStackInSlot(slot);
        itemDelegate.setStackInSlot(slot, stack);
        if (!strategy.equals(oldStack, stack))
            onContentsChanged();
    }

    // INotifiableHandler
    @Override
    public void addNotifiableMetaTileEntity(MetaTileEntity metaTileEntity) {
        if (metaTileEntity == null || this.notifiableEntities.contains(metaTileEntity))
            return;
        this.notifiableEntities.add(metaTileEntity);
        if (getItemDelegate() instanceof INotifiableHandler handler) {
            handler.addNotifiableMetaTileEntity(metaTileEntity);
        } else if (getItemDelegate() instanceof ItemHandlerList list) {
            for (IItemHandler handler : list.getBackingHandlers()) {
                if (handler instanceof INotifiableHandler notifiableHandler) {
                    notifiableHandler.addNotifiableMetaTileEntity(metaTileEntity);
                }
            }
        }
        for (MultiFluidTankEntry entry : getFluidDelegate()) {
            if (entry.getDelegate() instanceof INotifiableHandler handler) {
                handler.addNotifiableMetaTileEntity(metaTileEntity);
            }
        }
    }

    @Override
    public void removeNotifiableMetaTileEntity(MetaTileEntity metaTileEntity) {
        this.notifiableEntities.remove(metaTileEntity);
        if (getItemDelegate() instanceof INotifiableHandler handler) {
            handler.removeNotifiableMetaTileEntity(metaTileEntity);
        } else if (getItemDelegate() instanceof ItemHandlerList list) {
            for (IItemHandler handler : list.getBackingHandlers()) {
                if (handler instanceof INotifiableHandler notifiableHandler) {
                    notifiableHandler.removeNotifiableMetaTileEntity(metaTileEntity);
                }
            }
        }
        for (MultiFluidTankEntry entry : getFluidDelegate()) {
            if (entry.getDelegate() instanceof INotifiableHandler handler) {
                handler.removeNotifiableMetaTileEntity(metaTileEntity);
            }
        }
    }

    public @NotNull IItemHandlerModifiable getItemDelegate() {
        return itemDelegate;
    }

    public @NotNull IMultipleTankHandler getFluidDelegate() {
        return fluidDelegate;
    }

    public void onContentsChanged(Object handler) {
        for (MetaTileEntity metaTileEntity : notifiableEntities) {
            addToNotifiedList(metaTileEntity, handler, isExport);
        }
    }

    public void onContentsChanged() {
        onContentsChanged(this);
    }
}
