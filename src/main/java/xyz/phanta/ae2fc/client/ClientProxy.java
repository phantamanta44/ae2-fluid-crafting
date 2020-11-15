package xyz.phanta.ae2fc.client;

import appeng.api.util.AEColor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.phanta.ae2fc.CommonProxy;
import xyz.phanta.ae2fc.client.handler.ClientRegistryHandler;
import xyz.phanta.ae2fc.client.render.DropColourHandler;
import xyz.phanta.ae2fc.client.render.RenderIngredientBuffer;
import xyz.phanta.ae2fc.handler.RegistryHandler;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.item.ItemFluidPacket;
import xyz.phanta.ae2fc.tile.TileIngredientBuffer;

public class ClientProxy extends CommonProxy {

    private final DropColourHandler dropColourHandler = new DropColourHandler();

    @Override
    protected RegistryHandler createRegistryHandler() {
        return new ClientRegistryHandler();
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        super.onPreInit(event);
        MinecraftForge.EVENT_BUS.register(dropColourHandler);
        ClientRegistry.bindTileEntitySpecialRenderer(TileIngredientBuffer.class, new RenderIngredientBuffer());
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((s, i) -> {
            FluidStack fluid = ItemFluidDrop.getFluidStack(s);
            return fluid != null ? dropColourHandler.getColour(fluid) : -1;
        }, FcItems.FLUID_DROP);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((s, i) -> {
            if (i == 0) {
                return -1;
            }
            FluidStack fluid = ItemFluidPacket.getFluidStack(s);
            return fluid != null ? fluid.getFluid().getColor(fluid) : -1;
        }, FcItems.FLUID_PACKET);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((s, i) -> AEColor.TRANSPARENT.getVariantByTintIndex(i), FcItems.PART_FLUID_PATTERN_TERMINAL);
    }

}
