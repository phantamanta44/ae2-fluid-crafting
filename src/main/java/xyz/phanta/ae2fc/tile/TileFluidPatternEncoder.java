package xyz.phanta.ae2fc.tile;

import appeng.api.AEApi;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.tile.AEBaseTile;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import appeng.util.inv.InvOperation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import xyz.phanta.ae2fc.util.AeStackInventory;
import xyz.phanta.ae2fc.util.AeStackInventoryImpl;

import javax.annotation.Nullable;
import java.util.List;

public class TileFluidPatternEncoder extends AEBaseTile implements IAEAppEngInventory {

    private final AppEngInternalInventory patternInv = new AppEngInternalInventory(this, 2);
    private final AeStackInventoryImpl<IAEItemStack> crafting = new AeStackInventoryImpl<>(
            AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class), 9, this);
    private final AeStackInventoryImpl<IAEItemStack> output = new AeStackInventoryImpl<>(
            AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class), 3, this);

    public IItemHandlerModifiable getInventory() {
        return patternInv;
    }

    public AeStackInventory<IAEItemStack> getCraftingSlots() {
        return crafting;
    }

    public AeStackInventory<IAEItemStack> getOutputSlots() {
        return output;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T)patternInv;
        } else {
            return null;
        }
    }

    @Override
    public void onChangeInventory(IItemHandler inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {
        // NO-OP
    }

    @Override
    public void getDrops(World world, BlockPos pos, List<ItemStack> drops) {
        for (ItemStack stack : patternInv) {
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        patternInv.readFromNBT(data, "Inventory");
        crafting.readFromNbt(data, "CraftingSlots");
        output.readFromNbt(data, "OutputSlots");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        patternInv.writeToNBT(data, "Inventory");
        crafting.writeToNbt(data, "CraftingSlots");
        output.writeToNbt(data, "OutputSlots");
        return data;
    }

}
