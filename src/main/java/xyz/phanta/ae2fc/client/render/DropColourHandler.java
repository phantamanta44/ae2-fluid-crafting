package xyz.phanta.ae2fc.client.render;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropColourHandler {

    private final TObjectIntMap<String> colourCache = new TObjectIntHashMap<>(
            Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);

    @SubscribeEvent
    public void onTextureMapStitch(TextureStitchEvent event) {
        if (event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks()) {
            colourCache.clear();
        }
    }

    public int getColour(Fluid fluid) {
        int colour = colourCache.get(fluid.getName());
        if (colour == -1) {
            colour = fluid.getColor();
            if (colour == -1) {
                TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
                        .getTextureExtry(fluid.getStill().toString());
                if (sprite != null && sprite.getFrameCount() > 0) {
                    int[][] image = sprite.getFrameTextureData(0);
                    int r = 0, g = 0, b = 0, count = 0;
                    for (int[] row : image) {
                        for (int pixel : row) {
                            if (((pixel >> 24) & 0xFF) > 127) { // is alpha above 50%?
                                r += (pixel >> 16) & 0xFF;
                                g += (pixel >> 8) & 0xFF;
                                b += pixel & 0xFF;
                                ++count;
                            }
                        }
                    }
                    if (count > 0) {
                        // probably shouldn't need to mask each component by 0xFF
                        colour = ((r / count) << 16) | ((g / count) << 8) | (b / count);
                    }
                }
            }
            colourCache.put(fluid.getName(), colour);
        }
        return colour;
    }

}
