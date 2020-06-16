package xyz.phanta.ae2fc.handler;

import appeng.api.storage.data.IAEItemStack;
import appeng.util.InventoryAdaptor;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.item.ItemFluidPacket;
import xyz.phanta.ae2fc.util.FluidConvertingInventoryAdaptor;
import xyz.phanta.ae2fc.util.FluidConvertingInventoryCrafting;

import javax.annotation.Nullable;
import java.util.Objects;

public class CoreModHooks {

    public static InventoryCrafting wrapCraftingBuffer(InventoryCrafting inv) {
        return new FluidConvertingInventoryCrafting(inv.eventHandler, inv.getWidth(), inv.getHeight());
    }

    public static IAEItemStack wrapFluidPacketStack(IAEItemStack stack) {
        if (stack.getItem() == FcItems.FLUID_PACKET) {
            IAEItemStack dropStack = ItemFluidDrop.newAeStack(ItemFluidPacket.getFluidStack(stack.getDefinition()));
            if (dropStack != null) {
                return dropStack;
            }
        }
        return stack;
    }

    @Nullable
    public static InventoryAdaptor wrapInventory(@Nullable TileEntity tile, EnumFacing face) {
        if (tile != null) {
            // sometimes i wish i had the monadic version from 1.15
            return new FluidConvertingInventoryAdaptor(
                    tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)
                            ? Objects.requireNonNull(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face))
                            : null,
                    tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face)
                            ? Objects.requireNonNull(tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face))
                            : null);
        }
        return null;
    }

    public static long getCraftingByteCost(IAEItemStack stack) {
        return stack.getItem() instanceof ItemFluidDrop
                ? (long)Math.ceil(stack.getStackSize() / 1000D) : stack.getStackSize();
    }

}
