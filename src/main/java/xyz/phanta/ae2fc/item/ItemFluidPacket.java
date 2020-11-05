package xyz.phanta.ae2fc.item;

import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import xyz.phanta.ae2fc.client.model.HasCustomModel;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.init.FcItems;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemFluidPacket extends Item implements HasCustomModel {

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
        return fluid != null ? String.format("%s, %,d mB", fluid.getLocalizedName(), fluid.amount)
                : super.getItemStackDisplayName(stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        FluidStack fluid = getFluidStack(stack);
        if (fluid != null) {
            for (String line : I18n.translateToLocal(NameConst.TT_FLUID_PACKET).split("\\\\n")) {
                tooltip.add(TextFormatting.GRAY + line);
            }
        } else {
            tooltip.add(TextFormatting.RED + I18n.translateToLocal(NameConst.TT_INVALID_FLUID));
        }
    }

    @Nullable
    public static FluidStack getFluidStack(ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTagCompound()) {
            return null;
        }
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(Objects.requireNonNull(stack.getTagCompound()).getCompoundTag("FluidStack"));
        return (fluid != null && fluid.amount > 0) ? fluid : null;
    }

    @Nullable
    public static FluidStack getFluidStack(@Nullable IAEItemStack stack) {
        return stack != null ? getFluidStack(stack.getDefinition()) : null;
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

    @Nullable
    public static IAEItemStack newAeStack(@Nullable FluidStack fluid) {
        return AEItemStack.fromItemStack(newStack(fluid));
    }

    @Override
    public ResourceLocation getCustomModelPath() {
        return NameConst.MODEL_FLUID_PACKET;
    }

}
