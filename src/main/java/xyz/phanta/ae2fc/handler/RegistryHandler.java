package xyz.phanta.ae2fc.handler;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.init.FcItems;

import java.util.ArrayList;
import java.util.List;

public class RegistryHandler {

    protected final List<Pair<String, Block>> blocks = new ArrayList<>();
    protected final List<Pair<String, Item>> items = new ArrayList<>();

    public void block(String name, Block block) {
        blocks.add(Pair.of(name, block));
    }

    public void item(String name, Item item) {
        items.add(Pair.of(name, item));
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        for (Pair<String, Block> entry : blocks) {
            String key = entry.getLeft();
            Block block = entry.getRight();
            block.setRegistryName(key);
            block.setTranslationKey(Ae2FluidCrafting.MOD_ID + ":" + key);
            block.setCreativeTab(FcItems.TAB_AE2FC);
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        // TODO some way to handle blocks with custom ItemBlock
        for (Pair<String, Block> entry : blocks) {
            event.getRegistry().register(initItem(entry.getLeft(), new ItemBlock(entry.getRight())));
        }
        for (Pair<String, Item> entry : items) {
            event.getRegistry().register(initItem(entry.getLeft(), entry.getRight()));
        }
    }

    private static Item initItem(String key, Item item) {
        item.setRegistryName(key);
        item.setTranslationKey(Ae2FluidCrafting.MOD_ID + ":" + key);
        item.setCreativeTab(FcItems.TAB_AE2FC);
        return item;
    }

}
