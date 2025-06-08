package kono.ceu.gtconsolidate.api.recipes.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import org.jetbrains.annotations.NotNull;

import gregtech.api.GTValues;
import gregtech.api.recipes.recipeproperties.RecipeProperty;

public class CoAProperty extends RecipeProperty<Integer> {

    public static final String KEY = "coa_tier";
    private static CoAProperty INSTANCE;

    protected CoAProperty() {
        super(KEY, Integer.class);
    }

    public static CoAProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CoAProperty();
        }

        return INSTANCE;
    }

    @Override
    public void drawInfo(@NotNull Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(I18n.format("gtconsolidate.recipe.coa", GTValues.VN[castValue(value)]), x, y,
                color);
    }
}
