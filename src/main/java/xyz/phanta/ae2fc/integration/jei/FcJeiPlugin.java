package xyz.phanta.ae2fc.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.config.Constants;

@JEIPlugin
public class FcJeiPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
                new FluidPatternEncoderRecipeTransferHandler(), Constants.UNIVERSAL_RECIPE_TRANSFER_UID);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
                new FluidPatternTerminalRecipeTransferHandler(), Constants.UNIVERSAL_RECIPE_TRANSFER_UID);
    }

}
