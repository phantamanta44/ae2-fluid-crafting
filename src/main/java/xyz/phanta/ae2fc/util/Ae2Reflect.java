package xyz.phanta.ae2fc.util;

import appeng.util.inv.ItemSlot;

import java.lang.reflect.Method;

public class Ae2Reflect {

    private static final Method mItemSlot_setExtractable;

    static {
        try {
            mItemSlot_setExtractable = ItemSlot.class.getDeclaredMethod("setExtractable", boolean.class);
            mItemSlot_setExtractable.setAccessible(true);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    public static void setItemSlotExtractable(ItemSlot slot, boolean extractable) {
        try {
            mItemSlot_setExtractable.invoke(slot, extractable);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to invoke method: " + mItemSlot_setExtractable, e);
        }
    }

}
