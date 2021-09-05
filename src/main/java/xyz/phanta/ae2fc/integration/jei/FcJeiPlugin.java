package xyz.phanta.ae2fc.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.config.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

@JEIPlugin
public class FcJeiPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        IngredientExtractor<FluidStack> extModMach = Loader.isModLoaded("modularmachinery")
                ? new ModMachHybridFluidStackExtractor(registry) : null;
        ExtraExtractors ext = new ExtraExtractors(extModMach);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
                new FluidPatternEncoderRecipeTransferHandler(ext), Constants.UNIVERSAL_RECIPE_TRANSFER_UID);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
                new FluidPatternTerminalRecipeTransferHandler(ext), Constants.UNIVERSAL_RECIPE_TRANSFER_UID);
    }

}
