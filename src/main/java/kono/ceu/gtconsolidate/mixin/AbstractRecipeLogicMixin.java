package kono.ceu.gtconsolidate.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import gregtech.api.capability.impl.AbstractRecipeLogic;

import kono.ceu.gtconsolidate.api.util.mixinhelper.AbstractRecipeLogicMixinHelper;

@Mixin(value = AbstractRecipeLogic.class, remap = false)
public class AbstractRecipeLogicMixin implements AbstractRecipeLogicMixinHelper {

    @Shadow
    protected int parallelRecipesPerformed;

    @Unique
    @Override
    public int getCurrentParallel() {
        return this.parallelRecipesPerformed;
    }
}
