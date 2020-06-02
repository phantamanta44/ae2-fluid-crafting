package xyz.phanta.ae2fc.item;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.items.misc.ItemEncodedPattern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import xyz.phanta.ae2fc.client.model.HasCustomModel;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.util.DensePatternDetails;

import javax.annotation.Nullable;

public class ItemDenseEncodedPattern extends ItemEncodedPattern implements HasCustomModel {

    @Override
    protected void getCheckedSubItems(CreativeTabs creativeTab, NonNullList<ItemStack> itemStacks) {
        // NO-OP
    }

    @Nullable
    @Override
    public ICraftingPatternDetails getPatternForItem(ItemStack is, World w) {
        DensePatternDetails pattern = new DensePatternDetails(is);
        return pattern.readFromStack() ? pattern : null;
    }

    @Override
    public ResourceLocation getCustomModelPath() {
        return NameConst.MODEL_DENSE_ENCODED_PATTERN;
    }

}
