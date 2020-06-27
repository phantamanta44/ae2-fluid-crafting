package xyz.phanta.ae2fc.inventory;

import appeng.api.AEApi;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.AEBaseContainer;
import appeng.container.slot.SlotFake;
import appeng.container.slot.SlotRestrictedInput;
import appeng.helpers.InventoryAction;
import appeng.util.item.AEItemStack;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemHandlerHelper;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.inventory.slot.SlotDense;
import xyz.phanta.ae2fc.item.ItemDenseEncodedPattern;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.tile.TileFluidPatternEncoder;
import xyz.phanta.ae2fc.util.AeItemStackHandler;
import xyz.phanta.ae2fc.util.AeStackInventory;
import xyz.phanta.ae2fc.util.DensePatternDetails;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContainerFluidPatternEncoder extends AEBaseContainer {

    private final TileFluidPatternEncoder tile;

    public ContainerFluidPatternEncoder(InventoryPlayer ipl, TileFluidPatternEncoder tile) {
        super(ipl, tile);
        this.tile = tile;
        AeItemStackHandler crafting = new AeItemStackHandler(tile.getCraftingSlots());
        AeItemStackHandler output = new AeItemStackHandler(tile.getOutputSlots());
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new SlotFluidConvertingFake(crafting, y * 3 + x, 23 + x * 18, 17 + y * 18));
            }
            addSlotToContainer(new SlotFluidConvertingFake(output, y, 113, 17 + y * 18));
        }
        addSlotToContainer(new SlotRestrictedInput(
                SlotRestrictedInput.PlacableItemType.BLANK_PATTERN, tile.getInventory(), 0, 138, 20, ipl));
        addSlotToContainer(new SlotRestrictedInput(
                SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN, tile.getInventory(), 1, 138, 50, ipl));
        bindPlayerInventory(ipl, 0, 84);
    }

    public TileFluidPatternEncoder getTile() {
        return tile;
    }

    public boolean canEncodePattern() {
        if (isNotPattern(tile.getInventory().getStackInSlot(0)) && isNotPattern(tile.getInventory().getStackInSlot(1))) {
            return false;
        }
        find_input:
        {
            for (IAEItemStack stack : tile.getCraftingSlots()) {
                if (stack != null && stack.getStackSize() > 0) {
                    break find_input;
                }
            }
            return false;
        }
        for (IAEItemStack stack : tile.getOutputSlots()) {
            if (stack != null && stack.getStackSize() > 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNotPattern(ItemStack stack) {
        return stack.isEmpty() || !(AEApi.instance().definitions().materials().blankPattern().isSameAs(stack)
                || (stack.getItem() instanceof ItemDenseEncodedPattern));
    }

    public void encodePattern() {
        if (canEncodePattern()) {
            // if there is an encoded pattern, overwrite it; otherwise, consume a blank
            if (tile.getInventory().getStackInSlot(1).isEmpty()) {
                tile.getInventory().extractItem(0, 1, false); // this better work
            }
            ItemStack patternStack = new ItemStack(FcItems.DENSE_ENCODED_PATTERN);
            DensePatternDetails pattern = new DensePatternDetails(patternStack);
            pattern.setInputs(collectAeInventory(tile.getCraftingSlots()));
            pattern.setOutputs(collectAeInventory(tile.getOutputSlots()));
            tile.getInventory().setStackInSlot(1, pattern.writeToStack());
        }
    }

    private static IAEItemStack[] collectAeInventory(AeStackInventory<IAEItemStack> inv) {
        // see note at top of DensePatternDetails
        List<IAEItemStack> acc = new ArrayList<>();
        for (IAEItemStack stack : inv) {
            if (stack != null) {
                acc.add(stack);
            }
        }
        return acc.toArray(new IAEItemStack[0]);
    }

    // adapted from ae2's AEBaseContainer#doAction
    @Override
    public void doAction(EntityPlayerMP player, InventoryAction action, int slotId, long id) {
        Slot slot = getSlot(slotId);
        if (slot instanceof SlotFluidConvertingFake) {
            final ItemStack stack = player.inventory.getItemStack();
            switch (action) {
                case PICKUP_OR_SET_DOWN:
                    if (stack.isEmpty()) {
                        slot.putStack(ItemStack.EMPTY);
                    } else {
                        ((SlotFluidConvertingFake)slot).putConvertedStack(stack.copy());
                    }
                    break;
                case PLACE_SINGLE:
                    if (!stack.isEmpty()) {
                        ((SlotFluidConvertingFake)slot).putConvertedStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
                    }
                    break;
                case SPLIT_OR_PLACE_SINGLE:
                    ItemStack inSlot = slot.getStack();
                    if (!inSlot.isEmpty()) {
                        if (stack.isEmpty()) {
                            slot.putStack(ItemHandlerHelper.copyStackWithSize(inSlot, Math.max(1, inSlot.getCount() - 1)));
                        } else if (stack.isItemEqual(inSlot)) {
                            slot.putStack(ItemHandlerHelper.copyStackWithSize(inSlot,
                                    Math.min(inSlot.getMaxStackSize(), inSlot.getCount() + 1)));
                        } else {
                            ((SlotFluidConvertingFake)slot).putConvertedStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
                        }
                    } else if (!stack.isEmpty()) {
                        ((SlotFluidConvertingFake)slot).putConvertedStack(ItemHandlerHelper.copyStackWithSize(stack, 1));
                    }
                    break;
            }
        } else {
            super.doAction(player, action, slotId, id);
        }
    }

    private static class SlotFluidConvertingFake extends SlotFake implements SlotDense {

        private final AeStackInventory<IAEItemStack> inv;

        public SlotFluidConvertingFake(AeItemStackHandler inv, int idx, int x, int y) {
            super(inv, idx, x, y);
            this.inv = inv.getAeInventory();
        }

        @Override
        public void putStack(ItemStack stack) {
            inv.setStack(getSlotIndex(), AEItemStack.fromItemStack(stack));
        }

        @Override
        public void setAeStack(@Nullable IAEItemStack stack, boolean sync) {
            inv.setStack(getSlotIndex(), stack);
        }

        public void putConvertedStack(ItemStack stack) {
            if (stack.isEmpty()) {
                setAeStack(null, false);
                return;
            } else if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                IFluidTankProperties[] tanks = Objects.requireNonNull(
                        stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                        .getTankProperties();
                for (IFluidTankProperties tank : tanks) {
                    IAEItemStack aeStack = ItemFluidDrop.newAeStack(tank.getContents());
                    if (aeStack != null) {
                        setAeStack(aeStack, false);
                        return;
                    }
                }
            }
            putStack(stack);
        }

        @Nullable
        @Override
        public IAEItemStack getAeStack() {
            return inv.getStack(getSlotIndex());
        }

    }

}
