package xyz.phanta.ae2fc.integration.jei;

import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.transfer.RecipeTransferErrorTooltip;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternEncoder;
import xyz.phanta.ae2fc.item.ItemFluidPacket;
import xyz.phanta.ae2fc.network.CPacketLoadPattern;
import xyz.phanta.ae2fc.tile.TileFluidPatternEncoder;

import javax.annotation.Nullable;

public class FluidPatternEncoderRecipeTransferHandler implements IRecipeTransferHandler<ContainerFluidPatternEncoder> {

    @Override
    public Class<ContainerFluidPatternEncoder> getContainerClass() {
        return ContainerFluidPatternEncoder.class;
    }

    @Nullable
    @Override
    public IRecipeTransferError transferRecipe(ContainerFluidPatternEncoder container, IRecipeLayout recipeLayout,
                                               EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
        if (recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
            return new RecipeTransferErrorTooltip(I18n.format(NameConst.TT_PROCESSING_RECIPE_ONLY));
        }
        if (doTransfer) {
            TileFluidPatternEncoder tile = container.getTile();
            IAEItemStack[] crafting = new IAEItemStack[tile.getCraftingSlots().getSlotCount()];
            IAEItemStack[] output = new IAEItemStack[tile.getOutputSlots().getSlotCount()];
            int ndxCrafting = 0, ndxOutput = 0;
            for (IGuiIngredient<ItemStack> ing : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                if (ing.isInput()) {
                    if (ndxCrafting < crafting.length) {
                        ItemStack stack = ing.getDisplayedIngredient();
                        if (stack != null) {
                            crafting[ndxCrafting++] = AEItemStack.fromItemStack(stack);
                        }
                    }
                } else {
                    if (ndxOutput < output.length) {
                        ItemStack stack = ing.getDisplayedIngredient();
                        if (stack != null) {
                            output[ndxOutput++] = AEItemStack.fromItemStack(stack);
                        }
                    }
                }
            }
            for (IGuiIngredient<FluidStack> ing : recipeLayout.getFluidStacks().getGuiIngredients().values()) {
                if (ing.isInput()) {
                    if (ndxCrafting < crafting.length) {
                        crafting[ndxCrafting++] = ItemFluidPacket.newAeStack(ing.getDisplayedIngredient());
                    }
                } else {
                    if (ndxOutput < output.length) {
                        output[ndxOutput++] = ItemFluidPacket.newAeStack(ing.getDisplayedIngredient());
                    }
                }
            }
            Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketLoadPattern(crafting, output));
        }
        return null;
    }

}
