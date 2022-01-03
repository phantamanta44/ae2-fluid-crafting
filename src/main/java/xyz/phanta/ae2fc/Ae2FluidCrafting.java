package xyz.phanta.ae2fc;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Ae2FluidCrafting.MOD_ID)
public class Ae2FluidCrafting {
    public static final String MOD_ID = "ae2fc";
    public static final String VERSION = "1.0.10";
    public static Ae2FluidCrafting INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    public Ae2FluidCrafting() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        } else {
            INSTANCE = this;
        }
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }
}
