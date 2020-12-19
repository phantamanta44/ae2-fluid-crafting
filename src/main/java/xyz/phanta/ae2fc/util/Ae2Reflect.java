package xyz.phanta.ae2fc.util;

import appeng.api.definitions.IItemDefinition;
import appeng.container.implementations.ContainerPatternTerm;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.container.slot.SlotRestrictedInput;
import appeng.recipes.game.DisassembleRecipe;
import appeng.util.inv.ItemSlot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class Ae2Reflect {

    private static final Method mItemSlot_setExtractable;
    private static final Field fDisassembleRecipe_nonCellMappings;
    private static final Field fContainerPatternTerm_craftingSlots;
    private static final Field fContainerPatternTerm_outputSlots;
    private static final Field fContainerPatternTerm_patternSlotIN;
    private static final Field fContainerPatternTerm_patternSlotOUT;

    static {
        try {
            mItemSlot_setExtractable = reflectMethod(ItemSlot.class, "setExtractable", boolean.class);
            fDisassembleRecipe_nonCellMappings = reflectField(DisassembleRecipe.class, "nonCellMappings");
            fContainerPatternTerm_craftingSlots = reflectField(ContainerPatternTerm.class, "craftingSlots");
            fContainerPatternTerm_outputSlots = reflectField(ContainerPatternTerm.class, "outputSlots");
            fContainerPatternTerm_patternSlotIN = reflectField(ContainerPatternTerm.class, "patternSlotIN");
            fContainerPatternTerm_patternSlotOUT = reflectField(ContainerPatternTerm.class, "patternSlotOUT");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize AE2 reflection hacks!", e);
        }
    }

    private static Method reflectMethod(Class<?> owner, String name, Class<?>... paramTypes) throws NoSuchMethodException {
        Method m = owner.getDeclaredMethod(name, paramTypes);
        m.setAccessible(true);
        return m;
    }

    private static Field reflectField(Class<?> owner, String name) throws NoSuchFieldException {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        return f;
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
            return (Map<IItemDefinition, IItemDefinition>)fDisassembleRecipe_nonCellMappings.get(recipe);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + fDisassembleRecipe_nonCellMappings, e);
        }
    }

    public static SlotFakeCraftingMatrix[] getCraftingSlots(ContainerPatternTerm cont) {
        try {
            return (SlotFakeCraftingMatrix[])fContainerPatternTerm_craftingSlots.get(cont);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + fContainerPatternTerm_craftingSlots, e);
        }
    }
    
    public static OptionalSlotFake[] getOutputSlots(ContainerPatternTerm cont) {
        try {
            return (OptionalSlotFake[])fContainerPatternTerm_outputSlots.get(cont);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + fContainerPatternTerm_outputSlots, e);
        }
    }
    
    public static SlotRestrictedInput getPatternSlotIn(ContainerPatternTerm cont) {
        try {
            return (SlotRestrictedInput)fContainerPatternTerm_patternSlotIN.get(cont);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + fContainerPatternTerm_patternSlotIN, e);
        }
    }
    
    public static SlotRestrictedInput getPatternSlotOut(ContainerPatternTerm cont) {
        try {
            return (SlotRestrictedInput)fContainerPatternTerm_patternSlotOUT.get(cont);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read field: " + fContainerPatternTerm_patternSlotOUT, e);
        }
    }

}
