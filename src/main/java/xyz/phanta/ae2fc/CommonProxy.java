package xyz.phanta.ae2fc;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.features.ItemDefinition;
import appeng.recipes.game.DisassembleRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.handler.RegistryHandler;
import xyz.phanta.ae2fc.init.FcBlocks;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.integration.pauto.PackagedFluidCrafting;
import xyz.phanta.ae2fc.inventory.InventoryHandler;
import xyz.phanta.ae2fc.network.*;
import xyz.phanta.ae2fc.tile.*;
import xyz.phanta.ae2fc.util.Ae2Reflect;

import java.util.Objects;

public class CommonProxy {

    private final RegistryHandler regHandler = createRegistryHandler();
    private final SimpleNetworkWrapper netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(Ae2FluidCrafting.MOD_ID);

    protected RegistryHandler createRegistryHandler() {
        return new RegistryHandler();
    }

    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(regHandler);
        FcBlocks.init(regHandler);
        FcItems.init(regHandler);
        GameRegistry.registerTileEntity(TileFluidDiscretizer.class, Ae2FluidCrafting.resource(NameConst.BLOCK_FLUID_DISCRETIZER));
        GameRegistry.registerTileEntity(TileFluidPatternEncoder.class, Ae2FluidCrafting.resource(NameConst.BLOCK_FLUID_PATTERN_ENCODER));
        GameRegistry.registerTileEntity(TileFluidPacketDecoder.class, Ae2FluidCrafting.resource(NameConst.BLOCK_FLUID_PACKET_DECODER));
        GameRegistry.registerTileEntity(TileIngredientBuffer.class, Ae2FluidCrafting.resource(NameConst.BLOCK_INGREDIENT_BUFFER));
        GameRegistry.registerTileEntity(TileBurette.class, Ae2FluidCrafting.resource(NameConst.BLOCK_BURETTE));
        GameRegistry.registerTileEntity(TileDualInterface.class, Ae2FluidCrafting.resource(NameConst.BLOCK_DUAL_INTERFACE));
        netHandler.registerMessage(new CPacketEncodePattern.Handler(), CPacketEncodePattern.class, 0, Side.SERVER);
        netHandler.registerMessage(new CPacketLoadPattern.Handler(), CPacketLoadPattern.class, 1, Side.SERVER);
        netHandler.registerMessage(new CPacketDumpTank.Handler(), CPacketDumpTank.class, 2, Side.SERVER);
        netHandler.registerMessage(new CPacketTransposeFluid.Handler(), CPacketTransposeFluid.class, 3, Side.SERVER);
        netHandler.registerMessage(new CPacketSwitchGuis.Handler(), CPacketSwitchGuis.class, 4, Side.SERVER);
        if (Loader.isModLoaded("packagedauto")) {
            PackagedFluidCrafting.init();
        }
    }

    public void onInit(FMLInitializationEvent event) {
        IRecipe disassembleRecipe = ForgeRegistries.RECIPES.getValue(new ResourceLocation(AppEng.MOD_ID, "disassemble"));
        if (disassembleRecipe instanceof DisassembleRecipe) {
            Ae2Reflect.getDisassemblyNonCellMap((DisassembleRecipe)disassembleRecipe).put(
                    createItemDefn(FcItems.DENSE_ENCODED_PATTERN),
                    AEApi.instance().definitions().materials().blankPattern());
        }
    }

    private static IItemDefinition createItemDefn(Item item) {
        return new ItemDefinition(Objects.requireNonNull(item.getRegistryName()).toString(), item);
    }

    public void onPostInit(FMLPostInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(Ae2FluidCrafting.INSTANCE, new InventoryHandler());
    }

    public SimpleNetworkWrapper getNetHandler() {
        return netHandler;
    }

}
