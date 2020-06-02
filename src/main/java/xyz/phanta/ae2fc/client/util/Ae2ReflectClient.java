package xyz.phanta.ae2fc.client.util;

import appeng.client.gui.AEBaseGui;
import appeng.client.render.StackSizeRenderer;

import java.lang.reflect.Field;

public class Ae2ReflectClient {

    private static final Field fAEBaseGui_stackSizeRenderer;

    static {
        try {
            fAEBaseGui_stackSizeRenderer = AEBaseGui.class.getDeclaredField("stackSizeRenderer");
            fAEBaseGui_stackSizeRenderer.setAccessible(true);
        } catch (NoSuchFieldException e) {
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

}
