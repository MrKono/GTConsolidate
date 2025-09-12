package kono.ceu.gtconsolidate.api.util.mixinhelper;

import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.recipes.Recipe;

public interface MultiblockDisplayTextMixinHelper {

    MultiblockDisplayText.Builder addExtendedProgressLine(int current, int total, double percent);

    MultiblockDisplayText.Builder addOutputLine(Recipe recipe);

    MultiblockDisplayText.Builder addOutputLine(Recipe recipe, int parallel);
}
