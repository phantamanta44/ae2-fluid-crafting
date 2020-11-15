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
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternTerminal;
import xyz.phanta.ae2fc.item.ItemFluidPacket;
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
        if (!container.craftingMode && recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
            return new RecipeTransferErrorTooltip(I18n.format(NameConst.TT_PROCESSING_RECIPE_ONLY));
        }
        if (container.craftingMode && !recipeLayout.getRecipeCategory().getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
            return new RecipeTransferErrorTooltip(I18n.format(NameConst.TT_CRAFTING_RECIPE_ONLY));
        }
        if (doTransfer) {
            if (container.getPatternTerminal() instanceof PartFluidPatternTerminal) {
                PartFluidPatternTerminal tile = (PartFluidPatternTerminal) container.getPatternTerminal();
                IAEItemStack[] crafting = new IAEItemStack[tile.getInventoryByName("crafting").getSlots()];
                IAEItemStack[] output = new IAEItemStack[tile.getInventoryByName("output").getSlots()];
                int ndxCrafting = 0, ndxOutput = 0;
                for (IGuiIngredient<ItemStack> ing : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                    if (ing.isInput()) {
                        if (ndxCrafting < crafting.length) {
                            ItemStack stack = ing.getDisplayedIngredient();
                            if (stack != null) {
                                crafting[ndxCrafting++] = AEItemStack.fromItemStack(stack);
                            } else if (container.craftingMode) {
                                crafting[ndxCrafting++] = AEItemStack.fromItemStack(ItemStack.EMPTY);
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
        }
        return null;
    }

}
