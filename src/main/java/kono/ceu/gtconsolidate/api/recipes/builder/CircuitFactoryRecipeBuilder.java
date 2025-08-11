package kono.ceu.gtconsolidate.api.recipes.builder;

import gregtech.api.GTValues;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.EnumValidationResult;
import kono.ceu.gtconsolidate.api.recipes.properties.CoAProperty;
import kono.ceu.gtconsolidate.api.util.Logs;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

public class CircuitFactoryRecipeBuilder extends RecipeBuilder<CircuitFactoryRecipeBuilder> {

    public CircuitFactoryRecipeBuilder() {}

    public CircuitFactoryRecipeBuilder(Recipe recipe, RecipeMap<CircuitFactoryRecipeBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public CircuitFactoryRecipeBuilder(RecipeBuilder<CircuitFactoryRecipeBuilder> recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public CircuitFactoryRecipeBuilder copy() {
        return new CircuitFactoryRecipeBuilder(this);
    }

    @Override
    public boolean applyProperty(@NotNull String key, Object value) {
        if (key.equals(CoAProperty.KEY)) {
            this.casingTier(((Number) value).intValue());
            return true;
        }
        return super.applyProperty(key, value);
    }

    public CircuitFactoryRecipeBuilder casingTier(int tier) {
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
