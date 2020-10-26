package xyz.phanta.ae2fc.client.handler;

import appeng.api.AEApi;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.client.model.DenseEncodedPatternModel;
import xyz.phanta.ae2fc.client.model.HasCustomModel;
import xyz.phanta.ae2fc.handler.RegistryHandler;
import xyz.phanta.ae2fc.item.ItemPartDualInterface;
import xyz.phanta.ae2fc.parts.PartDualInterface;

public class ClientRegistryHandler extends RegistryHandler {

    @SubscribeEvent
    public void onRegisterModels(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new DenseEncodedPatternModel.Loader());
        for (Pair<String, Block> entry : blocks) {
            registerModel(entry.getLeft(), Item.getItemFromBlock(entry.getRight()));
        }
        for (Pair<String, Item> entry : items) {
            registerModel(entry.getLeft(), entry.getRight());
        }
        AEApi.instance().registries().partModels().registerModels(PartDualInterface.MODEL_BASE, PartDualInterface.MODEL_ON, PartDualInterface.MODEL_OFF, PartDualInterface.MODEL_HAS_CHANNEL);
    }

    private static void registerModel(String key, Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(
                item instanceof HasCustomModel ? ((HasCustomModel)item).getCustomModelPath() : Ae2FluidCrafting.resource(key),
                "inventory"));
    }

}
