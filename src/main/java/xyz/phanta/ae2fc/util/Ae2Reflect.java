package xyz.phanta.ae2fc.util;

import appeng.api.definitions.IItemDefinition;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.recipes.game.DisassembleRecipe;
import appeng.util.inv.ItemSlot;
import net.minecraft.inventory.Slot;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class Ae2Reflect {

    private static final Method mItemSlot_setExtractable;
    private static final Field fDisassembleRecipe_nonCellMappings;

    static {
        try {
            mItemSlot_setExtractable = ItemSlot.class.getDeclaredMethod("setExtractable", boolean.class);
            mItemSlot_setExtractable.setAccessible(true);
            fDisassembleRecipe_nonCellMappings = DisassembleRecipe.class.getDeclaredField("nonCellMappings");
            fDisassembleRecipe_nonCellMappings.setAccessible(true);
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

    @SuppressWarnings("unchecked")
    public static Map<IItemDefinition, IItemDefinition> getDisassemblyNonCellMap(DisassembleRecipe recipe) {
        try {
            return (Map<IItemDefinition, IItemDefinition>) fDisassembleRecipe_nonCellMappings.get(recipe);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + fDisassembleRecipe_nonCellMappings, e);
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

    public static Slot getContainerPatternTermSlot(@Nonnull ContainerPatternTerm instance, @Nonnull String slotName) {
        try {
            return (Slot) findField(ContainerPatternTerm.class, slotName).get(instance);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to ContainerPatternTermSlot reflection hacks!", e);
        }
    }

    public static Slot[] getContainerPatternTermSlots(@Nonnull ContainerPatternTerm instance,
                                                      @Nonnull String slotName) {
        try {
            return (Slot[]) findField(ContainerPatternTerm.class, slotName).get(instance);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to ContainerPatternTermSlots reflection hacks!", e);
        }
    }

}
