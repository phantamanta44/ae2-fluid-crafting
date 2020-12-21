package xyz.phanta.ae2fc.integration.jei;

import appeng.api.storage.data.IAEItemStack;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.transfer.RecipeTransferErrorTooltip;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternTerminal;
import xyz.phanta.ae2fc.network.CPacketLoadPattern;
import xyz.phanta.ae2fc.parts.PartFluidPatternTerminal;

import javax.annotation.Nullable;

public class FluidPatternTerminalRecipeTransferHandler implements IRecipeTransferHandler<ContainerFluidPatternTerminal> {

    @Override
    public Class<ContainerFluidPatternTerminal> getContainerClass() {
        return ContainerFluidPatternTerminal.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(ContainerFluidPatternTerminal container, IRecipeLayout recipeLayout,
                                               EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (container.craftingMode) {
            if (!recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
                return new RecipeTransferErrorTooltip(I18n.format(NameConst.TT_PROCESSING_RECIPE_ONLY));
            }
        } else if (recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
            return new RecipeTransferErrorTooltip(I18n.format(NameConst.TT_CRAFTING_RECIPE_ONLY));
        }
        if (doTransfer && container.getPatternTerminal() instanceof PartFluidPatternTerminal) {
            PartFluidPatternTerminal tile = (PartFluidPatternTerminal)container.getPatternTerminal();
            IAEItemStack[] crafting = new IAEItemStack[tile.getInventoryByName("crafting").getSlots()];
            IAEItemStack[] output = new IAEItemStack[tile.getInventoryByName("output").getSlots()];
            FluidPatternEncoderRecipeTransferHandler.transferRecipeSlots(recipeLayout, crafting, output, container.craftingMode);
            Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketLoadPattern(crafting, output));
        }
        return null;
    }

}
