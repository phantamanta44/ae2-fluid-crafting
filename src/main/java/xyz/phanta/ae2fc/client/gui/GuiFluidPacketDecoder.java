package xyz.phanta.ae2fc.client.gui;

import appeng.client.gui.AEBaseGui;
import appeng.core.localization.GuiText;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.inventory.ContainerFluidPacketDecoder;
import xyz.phanta.ae2fc.tile.TileFluidPacketDecoder;

public class GuiFluidPacketDecoder extends AEBaseGui {

    private static final ResourceLocation TEX_BG = Ae2FluidCrafting.resource("textures/gui/fluid_packet_decoder.png");

    public GuiFluidPacketDecoder(InventoryPlayer ipl, TileFluidPacketDecoder tile) {
        super(new ContainerFluidPacketDecoder(ipl, tile));
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEX_BG);
        drawTexturedModalRect(offsetX, offsetY, 0, 0, 176, ySize);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        fontRenderer.drawString(getGuiDisplayName(I18n.format(NameConst.GUI_FLUID_PACKET_DECODER)), 8, 6, 0x404040);
        fontRenderer.drawString(GuiText.inventory.getLocal(), 8, ySize - 94, 0x404040);
    }

}
