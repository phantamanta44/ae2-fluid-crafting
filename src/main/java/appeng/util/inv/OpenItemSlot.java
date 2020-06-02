package appeng.util.inv;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.item.ItemStack;

// ItemSlot has package-private accessors and no useful constructors... for some reason >:(
public class OpenItemSlot extends ItemSlot {

    public OpenItemSlot(int slot, IAEItemStack stack, boolean extractable) {
        setSlot(slot);
        setAEItemStack(stack);
        setExtractable(extractable);
    }

    public OpenItemSlot(int slot, ItemStack stack, boolean extractable) {
        setSlot(slot);
        setItemStack(stack);
        setExtractable(extractable);
    }

    public OpenItemSlot() {
        super();
    }

    @Override
    public void setAEItemStack(IAEItemStack is) {
        super.setAEItemStack(is);
    }

    @Override
    public void setExtractable(boolean isExtractable) {
        super.setExtractable(isExtractable);
    }

}
