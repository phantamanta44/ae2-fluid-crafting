package tfar.ae2extras;

/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

import appeng.api.definitions.IDefinitions;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.Api;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public final class DisassembleRecipe extends ShapelessRecipe {
    public static final IRecipeSerializer<ShapelessRecipe> SERIALIZER = new Serializer2();

    private static final ItemStack MISMATCHED_STACK = ItemStack.EMPTY;

    public DisassembleRecipe(ShapelessRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.getRecipeOutput(),recipe.getIngredients());
    }


    @Override
    public boolean matches(@Nonnull final CraftingInventory inv, @Nonnull final World w) {
        return super.matches(inv,w) && isCellEmpty(inv, w);
    }

    public boolean isCellEmpty(@Nonnull final CraftingInventory inv, @Nonnull final World w) {
        int itemCount = 0;
        for (int slotIndex = 0; slotIndex < inv.getSizeInventory(); slotIndex++) {
            final ItemStack storageCell = inv.getStackInSlot(slotIndex);
            if (!storageCell.isEmpty()) {
                // needs a single input in the recipe
                itemCount++;
                if (itemCount > 1) {
                    return false;
                }

                // handle storage cells
                // make sure the storage cell storageCell empty...
                final IMEInventory<IAEItemStack> cellInv = Api.instance().registries().cell().getCellInventory(
                        storageCell, null, Api.instance().storage().getStorageChannel(IItemStorageChannel.class));
                if (cellInv != null) {
                    final IItemList<IAEItemStack> list = cellInv.getAvailableItems(
                            Api.instance().storage().getStorageChannel(IItemStorageChannel.class).createList());
                    if (!list.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inv.getStackInSlot(i);
            if (Block.getBlockFromItem(item.getItem()) instanceof CraftingStorageBlockEx) {
                IDefinitions definitions = Api.instance().definitions();
                nonnulllist.set(i, definitions.blocks().craftingUnit().stack(1));
            }
        }
        return nonnulllist;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<ShapelessRecipe> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer2 extends Serializer {

        @Override
        public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json) {
            return new DisassembleRecipe(super.read(recipeId, json));
        }

        @Override
        public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new DisassembleRecipe(super.read(recipeId, buffer));
        }
    }
}