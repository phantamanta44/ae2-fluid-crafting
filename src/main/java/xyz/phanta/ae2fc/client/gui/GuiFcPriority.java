package xyz.phanta.ae2fc.client.gui;

import appeng.client.gui.implementations.GuiPriority;
import appeng.client.gui.widgets.GuiTabButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import xyz.phanta.ae2fc.client.util.Ae2ReflectClient;
import xyz.phanta.ae2fc.inventory.GuiType;
import xyz.phanta.ae2fc.inventory.InventoryHandler;
import xyz.phanta.ae2fc.tile.base.FcPriorityHost;

import java.io.IOException;

public class GuiFcPriority extends GuiPriority {

    private final GuiType originalGui;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private GuiTabButton originalGuiBtn;

    public GuiFcPriority(final InventoryPlayer inventoryPlayer, final FcPriorityHost te) {
        super(inventoryPlayer, te);
        this.originalGui = te.getGuiType();
    }

    @Override
    public void initGui() {
        super.initGui();
        originalGuiBtn = Ae2ReflectClient.getOriginalGuiButton(this);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn == originalGuiBtn) {
            InventoryHandler.switchGui(originalGui);
        } else {
            super.actionPerformed(btn);
        }
    }

    protected String getBackground() {
        return "guis/priority.png";
    }

}
