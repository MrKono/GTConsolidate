package kono.ceu.gtconsolidate.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
import gregtech.core.sound.GTSoundEvents;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.gregtech.addon.GTConsolidate")
@ZenRegister
public class GTConsolidateRecipeMaps {

    @ZenProperty
    public static final RecipeMap<SimpleRecipeBuilder> COA_RECIPES = new RecipeMap<>("component_assemly_line", 15, 2, 4,
            0,
            new SimpleRecipeBuilder(), false)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_ASSEMBLY_LINE_ARROW, ProgressWidget.MoveType.HORIZONTAL)
                    .setSound(GTSoundEvents.ASSEMBLER);
}
