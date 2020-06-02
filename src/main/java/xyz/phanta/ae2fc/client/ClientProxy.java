package xyz.phanta.ae2fc.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.phanta.ae2fc.CommonProxy;
import xyz.phanta.ae2fc.client.handler.ClientRegistryHandler;
import xyz.phanta.ae2fc.client.render.DropColourHandler;
import xyz.phanta.ae2fc.handler.RegistryHandler;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.item.ItemFluidDrop;
import xyz.phanta.ae2fc.item.ItemFluidPacket;

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
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((s, i) -> {
            FluidStack fluid = ItemFluidDrop.getFluidStack(s);
            return fluid != null ? dropColourHandler.getColour(fluid.getFluid()) : -1;
        }, FcItems.FLUID_DROP);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler((s, i) -> {
            if (i == 0) {
                return -1;
            }
            FluidStack fluid = ItemFluidPacket.getFluidStack(s);
            return fluid != null ? dropColourHandler.getColour(fluid.getFluid()) : -1;
        }, FcItems.FLUID_PACKET);
    }

}
