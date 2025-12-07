package kono.ceu.gtconsolidate.common.metatileentities.multi.multiblockpart;

import gregtech.api.capability.impl.MultiblockFuelRecipeLogic;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.common.items.behaviors.AbstractMaterialPartBehavior;
import gregtech.common.items.behaviors.TurbineRotorBehavior;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityRotorHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetaTileEntityPowerEnhancedRotorHolder extends MetaTileEntityRotorHolder{

    private final GTConsolidateInventoryRotorHolder inventory;
    private final int maxSpeed;
    private int rotorColor = -1;

    public MetaTileEntityPowerEnhancedRotorHolder(ResourceLocation metaTileEntityId, int tier) {
        super(metaTileEntityId, tier);
        this.inventory = new GTConsolidateInventoryRotorHolder(this);
        this.maxSpeed = (2000 + 1000 * tier) * (7/8);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityPowerEnhancedRotorHolder(metaTileEntityId, getTier());
    }

    @Override
    public int getRotorPower() {
        return inventory.getRotorPower() * 2;
    }

    @Override
    public void damageRotor(int amount) {
        inventory.damageRotor(amount);
    }

    public class GTConsolidateInventoryRotorHolder extends NotifiableItemStackHandler {

        public GTConsolidateInventoryRotorHolder(MetaTileEntityRotorHolder holder) {
            super(holder, 1, null, false);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onLoad() {
            rotorColor = getRotorColor();
        }

        @Override
        public void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setRotorColor(getRotorColor());
            scheduleRenderUpdate();
        }

        @Nullable
        private ItemStack getTurbineStack() {
            if (!hasRotor())
                return null;
            return getStackInSlot(0);
        }

        @Nullable
        private TurbineRotorBehavior getTurbineBehavior() {
            ItemStack stack = getStackInSlot(0);
            if (stack.isEmpty()) return null;

            return TurbineRotorBehavior.getInstanceFor(stack);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean hasRotor() {
            return getTurbineBehavior() != null;
        }

        private int getRotorColor() {
            if (!hasRotor()) return -1;
            // noinspection ConstantConditions
            return getTurbineBehavior().getPartMaterial(getStackInSlot(0)).getMaterialRGB();
        }

        private int getRotorDurabilityPercent() {
            if (!hasRotor()) return 0;

            // noinspection ConstantConditions
            return getTurbineBehavior().getRotorDurabilityPercent(getStackInSlot(0));
        }

        private int getRotorEfficiency() {
            if (!hasRotor()) return -1;

            // noinspection ConstantConditions
            return getTurbineBehavior().getRotorEfficiency(getTurbineStack());
        }

        private int getRotorPower() {
            if (!hasRotor()) return -1;

            // noinspection ConstantConditions
            return getTurbineBehavior().getRotorPower(getTurbineStack());
        }

        private void damageRotor(int damageAmount) {
            if (!hasRotor()) return;

            if (getTurbineBehavior().getPartMaxDurability(getTurbineStack()) <=
                    AbstractMaterialPartBehavior.getPartDamage(getTurbineStack()) + damageAmount) {
                var holder = (MultiblockFuelRecipeLogic) getController().getRecipeLogic();
                if (holder != null && holder.isWorking()) {
                    holder.invalidate();
                }
            }

            // noinspection ConstantConditions
            getTurbineBehavior().applyRotorDamage(getStackInSlot(0), damageAmount);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return TurbineRotorBehavior.getInstanceFor(stack) != null && super.isItemValid(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack itemStack = super.extractItem(slot, amount, simulate);
            if (!simulate && itemStack != ItemStack.EMPTY) setRotorColor(-1);
            return itemStack;
        }
    }
}
