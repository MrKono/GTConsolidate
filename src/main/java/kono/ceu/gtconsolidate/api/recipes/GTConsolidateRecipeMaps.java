package kono.ceu.gtconsolidate.api.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.core.sound.GTSoundEvents;

import kono.ceu.gtconsolidate.api.recipes.builder.CoARecipeBuilder;
import kono.ceu.gtconsolidate.api.recipes.machine.RecipeMapCoA;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.gregtech.addon.GTConsolidate")
@ZenRegister
public class GTConsolidateRecipeMaps {

    @ZenProperty
    public static final RecipeMap<CoARecipeBuilder> COA_RECIPES = new RecipeMapCoA<>(
            "component_assembly_line", 16, false, 1, false, 4, false, 1, false,
            new CoARecipeBuilder(), false)
                    .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressWidget.MoveType.HORIZONTAL)
                    .setSound(GTSoundEvents.ASSEMBLER);
}
