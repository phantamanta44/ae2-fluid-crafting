package xyz.phanta.ae2fc.integration.pauto;

import thelm.packagedauto.api.RecipeTypeRegistry;

public class PackagedFluidCrafting {

    public static void init() {
        RecipeTypeRegistry.registerRecipeType(RecipeTypeFluidProcessing.INSTANCE);
    }

}
