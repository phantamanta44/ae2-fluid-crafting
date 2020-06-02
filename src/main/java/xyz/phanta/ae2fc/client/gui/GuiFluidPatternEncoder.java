package xyz.phanta.ae2fc.client.gui;

import appeng.client.gui.AEBaseGui;
import appeng.core.localization.GuiText;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.client.util.Ae2ReflectClient;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternEncoder;
import xyz.phanta.ae2fc.inventory.SlotDense;
import xyz.phanta.ae2fc.inventory.SlotSingleItem;
import xyz.phanta.ae2fc.network.CPacketEncodePattern;
import xyz.phanta.ae2fc.tile.TileFluidPatternEncoder;

import java.io.IOException;

public class GuiFluidPatternEncoder extends AEBaseGui {

    private static final ResourceLocation TEX_BG = Ae2FluidCrafting.resource("textures/gui/fluid_pattern_encoder.png");

    private final ContainerFluidPatternEncoder cont;

    public GuiFluidPatternEncoder(InventoryPlayer ipl, TileFluidPatternEncoder tile) {
        super(new ContainerFluidPatternEncoder(ipl, tile));
        this.cont = (ContainerFluidPatternEncoder)inventorySlots;
    }

    @Override
    protected void mouseClicked(int xCoord, int yCoord, int btn) throws IOException {
        int mX = xCoord - guiLeft, mY = yCoord - guiTop;
        if (btn == 0 && mX >= 141 && mX < 151 && mY >= 38 && mY < 48) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1F));
            if (cont.canEncodePattern()) {
                Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketEncodePattern());
            }
        } else {
            super.mouseClicked(xCoord, yCoord, btn);
        }
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEX_BG);
        drawTexturedModalRect(offsetX, offsetY, 0, 0, 176, ySize);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        fontRenderer.drawString(getGuiDisplayName(I18n.format(NameConst.GUI_FLUID_PATTERN_ENCODER)), 8, 6, 0x404040);
        fontRenderer.drawString(GuiText.inventory.getLocal(), 8, ySize - 94, 0x404040);
    }

    @Override
    public void drawSlot(Slot slot) {
        if (slot instanceof SlotDense) {
            super.drawSlot(new SlotSingleItem(slot));
            Ae2ReflectClient.getStackSizeRenderer(this)
                    .renderStackSize(fontRenderer, ((SlotDense)slot).getAeStack(), slot.xPos, slot.yPos);
        } else {
            super.drawSlot(slot);
        }
    }

}
