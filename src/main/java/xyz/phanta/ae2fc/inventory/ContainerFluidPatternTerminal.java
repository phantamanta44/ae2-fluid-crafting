package xyz.phanta.ae2fc.inventory;

import appeng.api.AEApi;
import appeng.api.definitions.IDefinitions;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.util.item.AEItemStack;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.item.ItemFluidPacket;
import xyz.phanta.ae2fc.util.Ae2Reflect;
import xyz.phanta.ae2fc.util.DensePatternDetails;

import java.util.ArrayList;
import java.util.List;

public class ContainerFluidPatternTerminal extends ContainerPatternTerm {
    private final Slot[] craftingSlots;
    private final Slot[] outputSlots;
    private final Slot patternSlotIN;
    private final Slot patternSlotOUT;

    public ContainerFluidPatternTerminal(InventoryPlayer ip,
                                         ITerminalHost monitorable) {
        super(ip, monitorable);
        craftingSlots = Ae2Reflect.getContainerPatternTermSlots(this, "craftingSlots");
        outputSlots = Ae2Reflect.getContainerPatternTermSlots(this, "outputSlots");
        patternSlotIN = Ae2Reflect.getContainerPatternTermSlot(this, "patternSlotIN");
        patternSlotOUT = Ae2Reflect.getContainerPatternTermSlot(this, "patternSlotOUT");
    }

    @Override
    public void encode() {
        if (!checkHasFluidPattern()) {
            super.encode();
        }
    }

    private boolean checkHasFluidPattern() {
        if (this.craftingMode) {
            return false;
        }
        boolean hasFluid = false;
        boolean craftingEmpty = true;
        boolean outEmpty = true;
        for (Slot craftingSlot : this.craftingSlots) {
            final ItemStack crafting = craftingSlot.getStack();
            if (!crafting.isEmpty()) {
                craftingEmpty = false;
            }
            if (crafting.getItem() instanceof ItemFluidPacket) {
                hasFluid = true;
                break;
            }
        }
        for (Slot outputSlot : this.outputSlots) {
            final ItemStack out = outputSlot.getStack();
            if (!out.isEmpty()) {
                outEmpty = false;
            }
            if (out.getItem() instanceof ItemFluidPacket) {
                hasFluid = true;
                break;
            }
        }
        if (hasFluid && !craftingEmpty && !outEmpty) {
            encodeFluidPattern();
            return true;
        }
        return false;
    }

    private void encodeFluidPattern() {
        ItemStack output = this.patternSlotOUT.getStack();
        if (!output.isEmpty() && !this.isPattern(output)) {
            return;
        } else if (output.isEmpty()) {
            output = this.patternSlotIN.getStack();
            if (output.isEmpty() || !this.isPattern(output)) {
                return;
            }
            output.setCount(output.getCount() - 1);
            if (output.getCount() == 0) {
                this.patternSlotIN.putStack(ItemStack.EMPTY);
            }

            ItemStack patternStack = new ItemStack(FcItems.DENSE_ENCODED_PATTERN);
            DensePatternDetails pattern = new DensePatternDetails(patternStack);
            pattern.setInputs(collectAeInventory(craftingSlots));
            pattern.setOutputs(collectAeInventory(outputSlots));
            this.patternSlotOUT.putStack(pattern.writeToStack());
        }
    }

    private boolean isPattern(final ItemStack output) {
        if (output.isEmpty()) {
            return false;
        }
        final IDefinitions definitions = AEApi.instance().definitions();
        boolean isPattern = definitions.items().encodedPattern().isSameAs(output);
        isPattern |= definitions.materials().blankPattern().isSameAs(output);
        return isPattern;
    }

    private static IAEItemStack[] collectAeInventory(Slot[] slots) {
        List<IAEItemStack> acc = new ArrayList<>();
        for (Slot slot : slots) {
            IAEItemStack stack = AEItemStack.fromItemStack(slot.getStack().copy());
            if (stack != null) {
                if (stack.getItem() instanceof ItemFluidPacket) {
                    IAEItemStack dropStack = ItemFluidDrop.newAeStack(ItemFluidPacket.getFluidStack(stack));
                    if (dropStack != null) {
                        acc.add(dropStack);
                        continue;
                    }
                }
                acc.add(stack);
            }
        }
        return acc.toArray(new IAEItemStack[0]);
    }
}
