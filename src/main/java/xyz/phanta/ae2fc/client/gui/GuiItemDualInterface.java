package xyz.phanta.ae2fc.client.gui;

import appeng.api.AEApi;
import appeng.client.gui.implementations.GuiInterface;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.helpers.IInterfaceHost;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;

import java.io.IOException;

public class GuiItemDualInterface extends GuiInterface {
    private GuiTabButton switchInterface;

    public GuiItemDualInterface(final InventoryPlayer inventoryPlayer, final IInterfaceHost te) {
        super(inventoryPlayer, te);
    }

    @Override
    protected void addButtons() {
        super.addButtons();
        final ItemStack fluidInterface =
                AEApi.instance().definitions().blocks().fluidIface().maybeStack(1).orElse(ItemStack.EMPTY);
        this.switchInterface =
                new GuiTabButton(this.guiLeft + 133, this.guiTop, fluidInterface, fluidInterface.getDisplayName(),
                        this.itemRender);
        this.buttonList.add(this.switchInterface);
    }

    @Override
    protected String getBackground() {
        return "guis/interface.png";
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn == ReflectionHelper.getPrivateValue(GuiInterface.class, this, "priority")) {
            Ae2GuiUtils.switchGui(Ae2GuiUtils.MY_PRIORITY);
            return;
        }

        if (btn == this.switchInterface) {
            Ae2GuiUtils.switchGui(Ae2GuiUtils.DUAL_FLUID_INTERFACE);
        }

        super.actionPerformed(btn);
    }
}
