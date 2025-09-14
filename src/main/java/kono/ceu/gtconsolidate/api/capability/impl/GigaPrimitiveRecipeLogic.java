package kono.ceu.gtconsolidate.api.capability.impl;

import org.jetbrains.annotations.NotNull;

import gregtech.api.capability.impl.PrimitiveRecipeLogic;
import gregtech.api.metatileentity.multiblock.ParallelLogicType;
import gregtech.api.metatileentity.multiblock.RecipeMapPrimitiveMultiblockController;

public class GigaPrimitiveRecipeLogic extends PrimitiveRecipeLogic {

    public GigaPrimitiveRecipeLogic(RecipeMapPrimitiveMultiblockController tileEntity) {
        super(tileEntity, tileEntity.getRecipeMap());
    }

    @NotNull
    @Override
    public ParallelLogicType getParallelLogicType() {
        return ParallelLogicType.APPEND_ITEMS;
    }
}
