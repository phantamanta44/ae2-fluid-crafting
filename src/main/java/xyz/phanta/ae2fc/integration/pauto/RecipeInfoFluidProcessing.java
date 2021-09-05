package xyz.phanta.ae2fc.integration.pauto;

import thelm.packagedauto.api.IRecipeType;
import thelm.packagedauto.recipe.RecipeInfoProcessing;

class RecipeInfoFluidProcessing extends RecipeInfoProcessing {

    @Override
    public IRecipeType getRecipeType() {
        return RecipeTypeFluidProcessing.INSTANCE;
    }

}
