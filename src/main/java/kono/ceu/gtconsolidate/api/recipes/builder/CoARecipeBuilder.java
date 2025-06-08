package kono.ceu.gtconsolidate.api.recipes.builder;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import gregtech.api.GTValues;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.EnumValidationResult;

import kono.ceu.gtconsolidate.api.recipes.properties.CoAProperty;
import kono.ceu.gtconsolidate.api.util.Logs;

public class CoARecipeBuilder extends RecipeBuilder<CoARecipeBuilder> {

    public CoARecipeBuilder() {}

    public CoARecipeBuilder(Recipe recipe, RecipeMap<CoARecipeBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public CoARecipeBuilder(RecipeBuilder<CoARecipeBuilder> recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public CoARecipeBuilder copy() {
        return new CoARecipeBuilder(this);
    }

    @Override
    public boolean applyProperty(@NotNull String key, Object value) {
        if (key.equals(CoAProperty.KEY)) {
            this.casingTier(((Number) value).intValue());
            return true;
        }
        return super.applyProperty(key, value);
    }

    public CoARecipeBuilder casingTier(int tier) {
        if (tier < GTValues.ULV) {
            Logs.logger.error("Casing tier cannot be less than 0",
                    new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        if (tier > GTValues.MAX) {
            Logs.logger.error("Casing tier cannot be grater than 14",
                    new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        this.applyProperty(CoAProperty.getInstance(), tier);
        return this;
    }

    public int getCasingTier() {
        return this.recipePropertyStorage == null ? 0 :
                this.recipePropertyStorage.getRecipePropertyValue(CoAProperty.getInstance(), 0);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append(CoAProperty.getInstance().getKey(), getCasingTier())
                .toString();
    }
}
