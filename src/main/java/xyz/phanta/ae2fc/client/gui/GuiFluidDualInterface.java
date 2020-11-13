package xyz.phanta.ae2fc.client.gui;

import appeng.api.AEApi;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.fluids.client.gui.GuiFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;

import java.io.IOException;

public class GuiFluidDualInterface extends GuiFluidInterface {
    private GuiTabButton switchInterface;

    public GuiFluidDualInterface(final InventoryPlayer ip, final IFluidInterfaceHost te) {
        super(ip, te);
    }

    @Override
    public void initGui() {
        super.initGui();
        final ItemStack itemInterface =
                AEApi.instance().definitions().blocks().iface().maybeStack(1).orElse(ItemStack.EMPTY);
        this.switchInterface =
                new GuiTabButton(this.guiLeft + 133, this.guiTop, itemInterface, itemInterface.getDisplayName(),
                        this.itemRender);
        this.buttonList.add(this.switchInterface);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        if (btn == ReflectionHelper.getPrivateValue(GuiFluidInterface.class, this, "priority")) {
            Ae2GuiUtils.switchGui(Ae2GuiUtils.MY_PRIORITY);
            return;
        }

        if (btn == this.switchInterface) {
            Ae2GuiUtils.switchGui(Ae2GuiUtils.DUAL_ITEM_INTERFACE);
        }

        super.actionPerformed(btn);
    }

    @Override
    protected boolean drawUpgrades() {
        return false;
    }
}
