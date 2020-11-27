package xyz.phanta.ae2fc.client.gui;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.client.gui.implementations.GuiPriority;
import appeng.core.sync.GuiBridge;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import xyz.phanta.ae2fc.client.util.Ae2ReflectClient;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;
import xyz.phanta.ae2fc.util.Ae2Reflect;

import java.io.IOException;

public class GuiMyCraftingStatus extends GuiCraftingStatus {
    public GuiMyCraftingStatus(InventoryPlayer inventoryPlayer,
                               ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn == Ae2ReflectClient.getAeButton(GuiCraftingStatus.class, this, "originalGuiBtn")) {
            Ae2GuiUtils.switchGui(Ae2GuiUtils.FLUID_PATTERN_TERMINAL);
            return;
        }

        super.actionPerformed(btn);
    }
}
