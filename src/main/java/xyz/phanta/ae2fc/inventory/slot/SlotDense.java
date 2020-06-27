package xyz.phanta.ae2fc.inventory.slot;

import appeng.api.storage.data.IAEItemStack;

import javax.annotation.Nullable;

public interface SlotDense {

    @Nullable
    IAEItemStack getAeStack();

    void setAeStack(@Nullable IAEItemStack stack, boolean sync);

}
