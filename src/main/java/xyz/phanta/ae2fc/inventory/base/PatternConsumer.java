package xyz.phanta.ae2fc.inventory.base;

import appeng.api.storage.data.IAEItemStack;

public interface PatternConsumer {

    void acceptPattern(IAEItemStack[] inputs, IAEItemStack[] outputs);

}
