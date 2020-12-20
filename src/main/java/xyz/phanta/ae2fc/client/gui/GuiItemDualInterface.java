package xyz.phanta.ae2fc.client.gui;

import appeng.api.AEApi;
import appeng.client.gui.implementations.GuiInterface;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.helpers.IInterfaceHost;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import xyz.phanta.ae2fc.client.util.Ae2ReflectClient;
import xyz.phanta.ae2fc.inventory.GuiType;
import xyz.phanta.ae2fc.inventory.InventoryHandler;

import java.io.IOException;

public class GuiItemDualInterface extends GuiInterface {

    @SuppressWarnings("NotNullFieldNotInitialized")
    private GuiTabButton switchInterface;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private GuiTabButton priorityBtn;

    public GuiItemDualInterface(final InventoryPlayer inventoryPlayer, final IInterfaceHost te) {
        super(inventoryPlayer, te);
    }

    @Override
    protected void addButtons() {
        super.addButtons();
        ItemStack icon = AEApi.instance().definitions().blocks().fluidIface().maybeStack(1).orElse(ItemStack.EMPTY);
        switchInterface = new GuiTabButton(guiLeft + 133, guiTop, icon, icon.getDisplayName(), itemRender);
        buttonList.add(switchInterface);
        priorityBtn = Ae2ReflectClient.getPriorityButton(this);
    }

    @Override
    protected String getBackground() {
        return "guis/interface.png";
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn == switchInterface) {
            InventoryHandler.switchGui(GuiType.DUAL_FLUID_INTERFACE);
        } else if (btn == priorityBtn) {
            InventoryHandler.switchGui(GuiType.PRIORITY);
        } else {
            super.actionPerformed(btn);
        }
    }

}
