package kono.ceu.gtconsolidate.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.PrimitiveRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;
import gregtech.api.recipes.RecipeMap;

public class ParallelizedPrimitiveRecipeLogic extends PrimitiveRecipeLogic {

    private long maxVoltage = GTValues.LV;
    private long maximumOverclockVoltage = GTValues.LV;

    public ParallelizedPrimitiveRecipeLogic(RecipeMapPrimitiveMultiblockController tileEntity, RecipeMap<?> recipeMap) {
        super(tileEntity, recipeMap);
    }

    @Override
    public long getMaxVoltage() {
        return this.maxVoltage;
    }

    @Override
    public long getMaximumOverclockVoltage() {
        return this.maximumOverclockVoltage;
    }

    @Override
    protected long getMaxParallelVoltage() {
        return getMaximumOverclockVoltage();
    }

    @Override
    public void setMaximumOverclockVoltage(long voltage) {
        maximumOverclockVoltage = voltage;
        maxVoltage = voltage;
    }
}
