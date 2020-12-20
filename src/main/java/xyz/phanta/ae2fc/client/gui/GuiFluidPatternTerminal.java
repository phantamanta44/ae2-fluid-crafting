package xyz.phanta.ae2fc.client.gui;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.render.StackSizeRenderer;
import appeng.container.slot.SlotFake;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import xyz.phanta.ae2fc.client.util.Ae2ReflectClient;
import xyz.phanta.ae2fc.client.util.FluidRenderUtils;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternTerminal;
import xyz.phanta.ae2fc.inventory.GuiType;
import xyz.phanta.ae2fc.inventory.InventoryHandler;

public class GuiFluidPatternTerminal extends GuiPatternTerm {

    private final StackSizeRenderer stackSizeRenderer = Ae2ReflectClient.getStackSizeRenderer(this);
    @SuppressWarnings("NotNullFieldNotInitialized")
    private GuiTabButton craftingStatusBtn;

    public GuiFluidPatternTerminal(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
        ContainerFluidPatternTerminal container = new ContainerFluidPatternTerminal(inventoryPlayer, te);
        container.setGui(this);
        this.inventorySlots = container;
        Ae2ReflectClient.setGuiContainer(this, container);
    }

    @Override
    public void initGui() {
        super.initGui();
        craftingStatusBtn = Ae2ReflectClient.getCraftingStatusButton(this);
    }

    @Override
    public void drawSlot(Slot slot) {
        if (!(slot instanceof SlotFake && FluidRenderUtils.renderFluidPacketIntoGuiSlot(
                slot, slot.getStack(), stackSizeRenderer, fontRenderer))) {
            super.drawSlot(slot);
        }
    }

    @Override
    protected void actionPerformed(final GuiButton btn) {
        if (btn == craftingStatusBtn) {
            InventoryHandler.switchGui(GuiType.FLUID_PAT_TERM_CRAFTING_STATUS);
        } else {
            super.actionPerformed(btn);
        }
    }

}
