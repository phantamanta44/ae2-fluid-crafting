package xyz.phanta.ae2fc.util;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.item.ItemFluidPacket;

public class FluidConvertingInventoryCrafting extends InventoryCrafting {

    public FluidConvertingInventoryCrafting(Container container, int width, int height) {
        super(container, width, height);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (stack.getItem() instanceof ItemFluidDrop) {
            FluidStack fluid = ItemFluidDrop.getFluidStack(stack);
            if (fluid != null) {
                super.setInventorySlotContents(index, ItemFluidPacket.newStack(new FluidStack(fluid, stack.getCount())));
            } else {
                // wtf?
                super.setInventorySlotContents(index, ItemFluidPacket.newStack(new FluidStack(FluidRegistry.WATER, 1000)));
            }
        } else {
            super.setInventorySlotContents(index, stack);
        }
    }

}
