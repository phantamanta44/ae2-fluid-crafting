package xyz.phanta.ae2fc.integration.pauto;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thelm.packagedauto.client.gui.GuiEncoder;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.item.ItemFluidPacket;

import java.util.List;

class RecipeEncoderFluidTooltipHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemTooltip(ItemTooltipEvent event) {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiEncoder) {
            ItemStack stack = event.getItemStack();
            if (stack.getItem() == FcItems.FLUID_PACKET) {
                FluidStack fluid = ItemFluidPacket.getFluidStack(stack);
                if (fluid != null) {
                    List<String> tooltip = event.getToolTip();
                    tooltip.clear();
                    tooltip.add(fluid.getLocalizedName());
                    tooltip.add(String.format(TextFormatting.GRAY + "%,d mB", fluid.amount));
                }
            }
        }
    }

}
