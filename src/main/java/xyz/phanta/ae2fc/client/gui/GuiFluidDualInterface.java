package xyz.phanta.ae2fc.client.gui;

import appeng.api.AEApi;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.localization.GuiText;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketSwitchGuis;
import appeng.fluids.client.gui.widgets.GuiFluidSlot;
import appeng.fluids.client.gui.widgets.GuiFluidTank;
import appeng.fluids.container.ContainerFluidInterface;
import appeng.fluids.helper.DualityFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import appeng.fluids.util.IAEFluidTank;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.network.CPacketSwitchGuis;

import java.io.IOException;

public class GuiFluidDualInterface extends GuiUpgradeable {
    private final IFluidInterfaceHost host;
    private GuiTabButton priority;
    private GuiTabButton switchInterface;

    public GuiFluidDualInterface(final InventoryPlayer ip, final IFluidInterfaceHost te) {
        super(new ContainerFluidInterface(ip, te));
        this.ySize = 231;
        this.host = te;
    }

    @Override
    public void initGui() {
        super.initGui();

        final IAEFluidTank configFluids = this.host.getDualityFluidInterface().getConfig();
        final IAEFluidTank fluidTank = this.host.getDualityFluidInterface().getTanks();

        for (int i = 0; i < DualityFluidInterface.NUMBER_OF_TANKS; ++i) {
            final GuiFluidTank guiTank = new GuiFluidTank(fluidTank, i, DualityFluidInterface.NUMBER_OF_TANKS + i, this.getGuiLeft() + 35 + 18 * i, this
                    .getGuiTop() + 53, 16, 68);
            this.buttonList.add(guiTank);
            this.guiSlots.add(new GuiFluidSlot(configFluids, i, i, 35 + 18 * i, 35));
        }

        this.priority = new GuiTabButton(this.getGuiLeft() + 154, this.getGuiTop(), 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender);
        this.buttonList.add(this.priority);

        final ItemStack iFace = AEApi.instance().definitions().blocks().iface().maybeStack( 1 ).orElse( ItemStack.EMPTY );;
        this.switchInterface = new GuiTabButton( this.guiLeft + 133, this.guiTop, iFace, iFace.getDisplayName(), this.itemRender );
        this.buttonList.add( this.switchInterface );
    }

    @Override
    protected void addButtons() {
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.getGuiDisplayName(GuiText.FluidInterface.getLocal()), 8, 6, 4210752);
        this.fontRenderer.drawString(GuiText.Config.getLocal(), 35, 6 + 11 + 7, 4210752);
        this.fontRenderer.drawString(GuiText.StoredFluids.getLocal(), 35, 6 + 112 + 7, 4210752);
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.bindTexture("guis/interfacefluid.png");
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);
        if (btn == this.priority) {
            Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketSwitchGuis(6));
        }

        if (btn == this.switchInterface){
            Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketSwitchGuis(4));
        }
    }

    @Override
    protected boolean drawUpgrades() {
        return false;
    }
}
