package kono.ceu.gtconsolidate.api.capability.impl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IGTConsolidateDualHandler extends IItemHandlerModifiable {


    void setStackInSlot(int slot, @Nonnull ItemStack stack);

    @Nullable
    FluidStack getFluid();

    int getFluidAmount();

    int getCapacity();

    FluidTankInfo getInfo();

    int fill(FluidStack resource, boolean doFill);

    @Nullable
    FluidStack drain(int maxDrain, boolean doDrain);
}
