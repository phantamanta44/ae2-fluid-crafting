package xyz.phanta.ae2fc.client.gui;

import appeng.api.AEApi;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.gui.widgets.GuiToggleButton;
import appeng.container.implementations.ContainerInterface;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import appeng.helpers.IInterfaceHost;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.network.CPacketSwitchGuis;

import java.io.IOException;

public class GuiItemDualInterface extends GuiUpgradeable {
    private GuiTabButton priority;
    private GuiImgButton BlockMode;
    private GuiToggleButton interfaceMode;
    private GuiTabButton switchInterface;
    public GuiItemDualInterface(final InventoryPlayer inventoryPlayer, final IInterfaceHost te ) {
        super( new ContainerInterface( inventoryPlayer, te ) );
        this.ySize = 211;
    }

    @Override
    protected void addButtons()
    {
        this.priority = new GuiTabButton( this.guiLeft + 154, this.guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), this.itemRender );
        this.buttonList.add( this.priority );

        this.BlockMode = new GuiImgButton( this.guiLeft - 18, this.guiTop + 8, Settings.BLOCK, YesNo.NO );
        this.buttonList.add( this.BlockMode );

        this.interfaceMode = new GuiToggleButton( this.guiLeft - 18, this.guiTop + 26, 84, 85, GuiText.InterfaceTerminal
                .getLocal(), GuiText.InterfaceTerminalHint.getLocal() );
        this.buttonList.add( this.interfaceMode );

        final ItemStack fluidIface = AEApi.instance().definitions().blocks().fluidIface().maybeStack( 1 ).orElse( ItemStack.EMPTY );;
        this.switchInterface = new GuiTabButton( this.guiLeft + 133, this.guiTop, fluidIface, fluidIface.getDisplayName(), this.itemRender );
        this.buttonList.add( this.switchInterface );
    }

    @Override
    public void drawFG( final int offsetX, final int offsetY, final int mouseX, final int mouseY )
    {
        if( this.BlockMode != null )
        {
            this.BlockMode.set( ( (ContainerInterface) this.cvb ).getBlockingMode() );
        }

        if( this.interfaceMode != null )
        {
            this.interfaceMode.setState( ( (ContainerInterface) this.cvb ).getInterfaceTerminalMode() == YesNo.YES );
        }

        this.fontRenderer.drawString( this.getGuiDisplayName( GuiText.Interface.getLocal() ), 8, 6, 4210752 );

        this.fontRenderer.drawString( GuiText.Config.getLocal(), 8, 6 + 11 + 7, 4210752 );
        this.fontRenderer.drawString( GuiText.StoredItems.getLocal(), 8, 6 + 60 + 7, 4210752 );
        this.fontRenderer.drawString( GuiText.Patterns.getLocal(), 8, 6 + 73 + 7, 4210752 );

        this.fontRenderer.drawString( GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752 );
    }

    @Override
    protected String getBackground()
    {
        return "guis/interface.png";
    }

    @Override
    protected void actionPerformed( final GuiButton btn ) throws IOException
    {
        super.actionPerformed( btn );

        final boolean backwards = Mouse.isButtonDown( 1 );

        if (btn == this.priority) {
            Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketSwitchGuis(6));
        }

        if (btn == this.switchInterface){
            Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketSwitchGuis(5));
        }

        if( btn == this.interfaceMode )
        {
            NetworkHandler.instance().sendToServer( new PacketConfigButton( Settings.INTERFACE_TERMINAL, backwards ) );
        }

        if( btn == this.BlockMode )
        {
            NetworkHandler.instance().sendToServer( new PacketConfigButton( this.BlockMode.getSetting(), backwards ) );
        }
    }
}
