package xyz.phanta.ae2fc.client.gui;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.client.gui.widgets.GuiTabButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import xyz.phanta.ae2fc.client.util.Ae2ReflectClient;
import xyz.phanta.ae2fc.inventory.GuiType;
import xyz.phanta.ae2fc.inventory.InventoryHandler;

import java.io.IOException;

public class GuiFluidPatternTerminalCraftingStatus extends GuiCraftingStatus {

    @SuppressWarnings("NotNullFieldNotInitialized")
    private GuiTabButton originalGuiBtn;

    public GuiFluidPatternTerminalCraftingStatus(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Override
    public void initGui() {
        super.initGui();
        originalGuiBtn = Ae2ReflectClient.getOriginalGuiButton(this);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn == originalGuiBtn) {
            InventoryHandler.switchGui(GuiType.FLUID_PATTERN_TERMINAL);
        } else {
            super.actionPerformed(btn);
        }
    }

}
