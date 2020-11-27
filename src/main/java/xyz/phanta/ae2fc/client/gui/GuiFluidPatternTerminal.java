package xyz.phanta.ae2fc.client.gui;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.container.slot.SlotFake;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import xyz.phanta.ae2fc.client.util.Ae2ReflectClient;
import xyz.phanta.ae2fc.client.util.FluidRenderUtils;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternTerminal;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.item.ItemFluidPacket;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;

public class GuiFluidPatternTerminal extends GuiPatternTerm {

    public GuiFluidPatternTerminal(InventoryPlayer inventoryPlayer,
                                   ITerminalHost te) {
        super(inventoryPlayer, te);
        ContainerFluidPatternTerminal container = new ContainerFluidPatternTerminal(inventoryPlayer, te);
        container.setGui(this);
        this.inventorySlots = container;
        Ae2ReflectClient.setContainerFluidPatternTerminal(this, container);
    }

    @Override
    public void drawSlot(Slot slot) {
        if (slot instanceof SlotFake) {
            ItemStack stack = slot.getStack();
            if (stack != null && stack.getItem() instanceof ItemFluidPacket) {
                FluidStack fluid = ItemFluidPacket.getFluidStack(stack);
                if (fluid != null && fluid.amount > 0) {
                    FluidRenderUtils.renderFluidIntoGuiCleanly(slot.xPos, slot.yPos, 16, 16, fluid, fluid.amount);
                    Ae2ReflectClient.getStackSizeRenderer(this)
                            .renderStackSize(fontRenderer, ItemFluidDrop.newAeStack(fluid), slot.xPos, slot.yPos);
                    return;
                }
            }
        }
        super.drawSlot(slot);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) {
        if (btn == Ae2ReflectClient.getAeButton(GuiMEMonitorable.class, this, "craftingStatusBtn")) {
            Ae2GuiUtils.switchGui(Ae2GuiUtils.MY_CRAFTING_STATUS);
            return;
        }

        super.actionPerformed(btn);
    }

}
