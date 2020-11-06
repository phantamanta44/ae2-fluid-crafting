package xyz.phanta.ae2fc.handler;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IMachineSet;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.MachineSet;
import appeng.parts.misc.PartInterface;
import appeng.tile.misc.TileInterface;
import appeng.util.InventoryAdaptor;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.item.ItemFluidPacket;
import xyz.phanta.ae2fc.parts.PartDualInterface;
import xyz.phanta.ae2fc.tile.TileDualInterface;
import xyz.phanta.ae2fc.util.FluidConvertingInventoryAdaptor;
import xyz.phanta.ae2fc.util.FluidConvertingInventoryCrafting;
import xyz.phanta.ae2fc.util.FluidConvertingItemHandler;

import javax.annotation.Nullable;
import java.util.HashSet;

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
        return tile != null ? FluidConvertingInventoryAdaptor.wrap(tile, face) : null;
    }

    public static long getCraftingByteCost(IAEItemStack stack) {
        return stack.getItem() instanceof ItemFluidDrop
                ? (long)Math.ceil(stack.getStackSize() / 1000D) : stack.getStackSize();
    }

    public static boolean checkForItemHandler(ICapabilityProvider capProvider, Capability<?> capability, EnumFacing side) {
        return capProvider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                || capProvider.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }

    public static IItemHandler wrapItemHandler(ICapabilityProvider capProvider, Capability<?> capability, EnumFacing side) {
        return FluidConvertingItemHandler.wrap(capProvider, side);
    }

    public static IAEItemStack[] flattenFluidPackets(IAEItemStack[] stacks) {
        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i].getItem() instanceof ItemFluidPacket) {
                stacks[i] = ItemFluidDrop.newAeStack(ItemFluidPacket.getFluidStack(stacks[i]));
            }
        }
        return stacks;
    }

    public static IMachineSet getMachines(IGrid grid, Class<? extends IGridHost> c){
        if (c == TileInterface.class){
            IMachineSet m1 =  grid.getMachines(c);
            IMachineSet m2 =  grid.getMachines(TileDualInterface.class);
            if (m1 instanceof MachineSet && m2 instanceof MachineSet)
                ((MachineSet) m1).addAll((MachineSet)m2);
            return m1;
        } else if (c == PartInterface.class){
            IMachineSet m1 =  grid.getMachines(c);
            IMachineSet m2 =  grid.getMachines(PartDualInterface.class);
            if (m1 instanceof MachineSet && m2 instanceof MachineSet)
                ((MachineSet) m1).addAll((MachineSet)m2);
            return m1;
        }
        return grid.getMachines(c);
    }
}
