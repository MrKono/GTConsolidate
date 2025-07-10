package kono.ceu.gtconsolidate.api.recipes;

import static gregtech.api.GTValues.*;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.BlastRecipeBuilder;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;
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

    @ZenProperty
    public static final RecipeMap<BlastRecipeBuilder> TURBO_BLAST_RECIPE = new RecipeMap<>("turbo_blast_recipe", 9, 3,
            3, 3, new BlastRecipeBuilder(), false)
                    .setSlotOverlay(false, false, false, GuiTextures.FURNACE_OVERLAY_1)
                    .setSlotOverlay(false, false, true, GuiTextures.FURNACE_OVERLAY_1)
                    .setSlotOverlay(false, true, false, GuiTextures.FURNACE_OVERLAY_2)
                    .setSlotOverlay(false, true, true, GuiTextures.FURNACE_OVERLAY_2)
                    .setSlotOverlay(true, true, false, GuiTextures.FURNACE_OVERLAY_2)
                    .setSlotOverlay(true, true, true, GuiTextures.FURNACE_OVERLAY_2)
                    .setSound(GTSoundEvents.FURNACE);

    @ZenProperty
    public static final RecipeMap<SimpleRecipeBuilder> ABSOLUTE_VACUUM_RECIPE = new RecipeMap<>(
            "absolute_vacuum_recipe",
            3, 3, 3, 3, new SimpleRecipeBuilder().duration(1).EUt(VA[UEV]), false)
                    .setSound(GTSoundEvents.COOLING);
}
