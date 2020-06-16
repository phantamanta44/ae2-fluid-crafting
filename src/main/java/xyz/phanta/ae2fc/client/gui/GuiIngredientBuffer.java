package xyz.phanta.ae2fc.client.gui;

import appeng.api.storage.data.IAEFluidStack;
import appeng.client.gui.AEBaseGui;
import appeng.core.localization.GuiText;
import appeng.fluids.util.IAEFluidTank;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.client.gui.component.MouseRegionManager;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.inventory.ContainerIngredientBuffer;
import xyz.phanta.ae2fc.network.CPacketDumpTank;
import xyz.phanta.ae2fc.tile.TileIngredientBuffer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuiIngredientBuffer extends AEBaseGui {

    private static final ResourceLocation TEX_BG = Ae2FluidCrafting.resource("textures/gui/ingredient_buffer.png");
    private static final int TANK_X = 47, TANK_X_OFF = 22, TANK_Y = 18;
    private static final int TANK_WIDTH = 16, TANK_HEIGHT = 74;

    private final ContainerIngredientBuffer cont;
    private final MouseRegionManager mouseRegions = new MouseRegionManager(this);

    public GuiIngredientBuffer(InventoryPlayer ipl, TileIngredientBuffer tile) {
        super(new ContainerIngredientBuffer(ipl, tile));
        this.cont = (ContainerIngredientBuffer)inventorySlots;
        this.ySize = 222;
        for (int i = 0; i < 4; i++) {
            mouseRegions.addRegion(TANK_X + TANK_X_OFF * i, TANK_Y, TANK_WIDTH, TANK_HEIGHT, new TankMouseHandler(i));
            mouseRegions.addRegion(TANK_X + 10 + 22 * i, TANK_Y + TANK_HEIGHT + 2, 7, 7, new DumpTankMouseHandler(i));
        }
    }

    @Override
    protected void mouseClicked(int xCoord, int yCoord, int btn) throws IOException {
        if (mouseRegions.onClick(xCoord, yCoord, btn)) {
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
        fontRenderer.drawString(getGuiDisplayName(I18n.format(NameConst.GUI_INGREDIENT_BUFFER)), 8, 6, 0x404040);
        fontRenderer.drawString(GuiText.inventory.getLocal(), 8, ySize - 94, 0x404040);
        GlStateManager.color(1F, 1F, 1F, 1F);

        IAEFluidTank fluidInv = cont.getTile().getFluidInventory();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        for (int i = 0; i < 4; i++) {
            IAEFluidStack fluid = fluidInv.getFluidInSlot(i);
            if (fluid != null) {
                FluidStack fluidStack = fluid.getFluidStack();
                TextureAtlasSprite sprite = mc.getTextureMapBlocks()
                        .getAtlasSprite(fluidStack.getFluid().getStill(fluidStack).toString());
                int height = Math.round(TANK_HEIGHT * (float)Math.min(1D, Math.max(0D,
                        fluid.getStackSize() / (double)fluidInv.getTankProperties()[i].getCapacity())));
                while (height > 0D) {
                    buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                    double x1 = TANK_X + i * TANK_X_OFF, y1 = TANK_Y + TANK_HEIGHT - height;
                    double x2 = x1 + TANK_WIDTH, y2 = y1 + Math.min(height, TANK_WIDTH);
                    double u1 = sprite.getMinU(), v1 = sprite.getMinV(), u2 = sprite.getMaxU(), v2 = sprite.getMaxV();
                    if (height < TANK_WIDTH) {
                        v2 = v1 + (v2 - v1) * (height / (double)TANK_WIDTH);
                        height = 0;
                    } else {
                        //noinspection SuspiciousNameCombination
                        height -= TANK_WIDTH;
                    }
                    buf.pos(x1, y1, 0D).tex(u1, v1).endVertex();
                    buf.pos(x1, y2, 0D).tex(u1, v2).endVertex();
                    buf.pos(x2, y2, 0D).tex(u2, v2).endVertex();
                    buf.pos(x2, y1, 0D).tex(u2, v1).endVertex();
                    tess.draw();
                }
            }
        }

        mouseRegions.render(mouseX, mouseY);
    }

    private class TankMouseHandler implements MouseRegionManager.Handler {

        private final int index;

        TankMouseHandler(int index) {
            this.index = index;
        }

        @Nullable
        @Override
        public List<String> getTooltip() {
            IAEFluidStack fluid = cont.getTile().getFluidInventory().getFluidInSlot(index);
            return fluid == null ? Collections.singletonList(I18n.format(NameConst.TT_EMPTY)) : Arrays.asList(
                    fluid.getFluidStack().getLocalizedName(),
                    TextFormatting.GRAY + String.format("%,d mB", fluid.getStackSize()));
        }

    }

    private class DumpTankMouseHandler implements MouseRegionManager.Handler {

        private final int index;

        DumpTankMouseHandler(int index) {
            this.index = index;
        }

        @Nullable
        @Override
        public List<String> getTooltip() {
            return Collections.singletonList(I18n.format(NameConst.TT_DUMP_TANK));
        }

        @Override
        public boolean onClick(int button) {
            if (button == 0) {
                if (cont.getTile().getFluidInventory().getFluidInSlot(index) != null) {
                    Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketDumpTank(index));
                }
                return true;
            }
            return false;
        }

    }

}
