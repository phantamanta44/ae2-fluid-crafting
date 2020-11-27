package xyz.phanta.ae2fc.client.util;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.implementations.GuiPriority;
import appeng.client.render.StackSizeRenderer;
import appeng.core.sync.GuiBridge;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.common.model.TRSRTransformation;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternTerminal;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@SuppressWarnings("unchecked") // can't annotate a static init block :(
public class Ae2ReflectClient {

    private static final Field fAEBaseGui_stackSizeRenderer;
    private static final Constructor<? extends IBakedModel> cItemEncodedPatternBakedModel;

    static {
        try {
            fAEBaseGui_stackSizeRenderer = AEBaseGui.class.getDeclaredField("stackSizeRenderer");
            fAEBaseGui_stackSizeRenderer.setAccessible(true);
            cItemEncodedPatternBakedModel = (Constructor<? extends IBakedModel>)Class
                    .forName("appeng.client.render.crafting.ItemEncodedPatternBakedModel")
                    .getDeclaredConstructor(IBakedModel.class, ImmutableMap.class);
            cItemEncodedPatternBakedModel.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static StackSizeRenderer getStackSizeRenderer(AEBaseGui gui) {
        try {
            return (StackSizeRenderer)fAEBaseGui_stackSizeRenderer.get(gui);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read field: " + fAEBaseGui_stackSizeRenderer, e);
        }
    }

    public static IBakedModel bakeEncodedPatternModel(IBakedModel baseModel,
                                                      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
        try {
            return cItemEncodedPatternBakedModel.newInstance(baseModel, transforms);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to invoke constructor: " + cItemEncodedPatternBakedModel, e);
        }
    }

    private static Field findField(Class<?> clazz, String fieldNames) {
        try {
            Field f = clazz.getDeclaredField(fieldNames);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static GuiBridge getPriorityGuiBridge(GuiPriority instance) {
        try {
            return (GuiBridge) findField(GuiPriority.class, "OriginalGui").get(instance);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to PriorityGuiBridge reflection hacks!", e);
        }
    }

    public static GuiButton getAeButton(@Nonnull Class<? extends AEBaseGui> clazz, @Nonnull AEBaseGui instance,
                                        @Nonnull String buttonName) {
        try {
            return (GuiButton) findField(clazz, buttonName).get(instance);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to AeButton reflection hacks!", e);
        }
    }

    public static void setContainerFluidPatternTerminal(GuiPatternTerm instance, ContainerFluidPatternTerminal container) {
        try {
            findField(GuiPatternTerm.class, "container").set(instance, container);
            findField(GuiMEMonitorable.class, "monitorableContainer").set(instance, container);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to AeButton reflection hacks!", e);
        }
    }

}
