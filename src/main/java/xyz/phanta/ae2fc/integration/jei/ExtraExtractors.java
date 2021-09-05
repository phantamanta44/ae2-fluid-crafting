package xyz.phanta.ae2fc.integration.jei;

import mezz.jei.api.gui.IRecipeLayout;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.stream.Stream;

class ExtraExtractors {

    @Nullable
    private final IngredientExtractor<FluidStack> extModMach;

    public ExtraExtractors(@Nullable IngredientExtractor<FluidStack> extModMach) {
        this.extModMach = extModMach;
    }

    public Stream<WrappedIngredient<FluidStack>> extractFluids(IRecipeLayout recipeLayout) {
        return extModMach != null ? extModMach.extract(recipeLayout) : Stream.empty();
    }

}
