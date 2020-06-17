package xyz.phanta.ae2fc.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidRenderUtils {

    @Nullable
    public static TextureAtlasSprite prepareRender(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            return null;
        }
        Fluid fluid = fluidStack.getFluid();
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite(fluid.getStill(fluidStack).toString());
        int colour = fluid.getColor(fluidStack);
        GlStateManager.color(
                ((colour >> 16) & 0xFF) / 255F,
                ((colour >> 8) & 0xFF) / 255F,
                (colour & 0xFF) / 255F,
                ((colour >> 24) & 0xFF) / 255F);
        return sprite;
    }

}
