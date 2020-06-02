package xyz.phanta.ae2fc.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.init.FcItems;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemFluidPacket extends Item {

    public ItemFluidPacket() {
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        // NO-OP
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        FluidStack fluid = getFluidStack(stack);
        return I18n.format(getTranslationKey(stack) + ".name", fluid != null ? fluid.getLocalizedName() : "???");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        FluidStack fluid = getFluidStack(stack);
        if (fluid != null) {
            tooltip.add(String.format(TextFormatting.GRAY + "%s, %,d mB", fluid.getLocalizedName(), fluid.amount));
        } else {
            tooltip.add(TextFormatting.RED + I18n.format(NameConst.TT_INVALID_FLUID));
        }
    }

    @Nullable
    public static FluidStack getFluidStack(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(Objects.requireNonNull(stack.getTagCompound()).getCompoundTag("FluidStack"));
        return (fluid != null && fluid.amount > 0) ? fluid : null;
    }

    public static ItemStack newStack(@Nullable FluidStack fluid) {
        if (fluid == null || fluid.amount == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(FcItems.FLUID_PACKET);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound fluidTag = new NBTTagCompound();
        fluid.writeToNBT(fluidTag);
        tag.setTag("FluidStack", fluidTag);
        stack.setTagCompound(tag);
        return stack;
    }

}
