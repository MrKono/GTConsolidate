package kono.ceu.gtconsolidate.api.util.mixinhelper;

import gregtech.api.capability.impl.AbstractRecipeLogic;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;

public interface MultiblockDisplayTextMixinHelper {

    MultiblockDisplayText.Builder addExtendedParallelLine(AbstractRecipeLogic logic);

    MultiblockDisplayText.Builder addExtendedProgressLine(int current, int total, double percent);

    MultiblockDisplayText.Builder addOutputLine(AbstractRecipeLogic logic);
}
