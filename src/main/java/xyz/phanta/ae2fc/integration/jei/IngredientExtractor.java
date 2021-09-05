package xyz.phanta.ae2fc.integration.jei;

import mezz.jei.api.gui.IRecipeLayout;

import java.util.stream.Stream;

interface IngredientExtractor<T> {

    Stream<WrappedIngredient<T>> extract(IRecipeLayout recipeLayout);

}
