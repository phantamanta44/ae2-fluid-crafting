package xyz.phanta.ae2fc.util;

import appeng.api.storage.data.IAEStack;

import javax.annotation.Nullable;

public interface AeStackInventory<T extends IAEStack<T>> extends Iterable<T> {

    int getSlotCount();

    @Nullable
    T getStack(int slot);

    void setStack(int slot, @Nullable T stack);

}
