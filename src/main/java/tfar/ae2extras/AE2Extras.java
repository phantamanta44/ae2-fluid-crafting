package tfar.ae2extras;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.CraftingStorageItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AE2Extras.MODID)
public class AE2Extras
{
    // Directly reference a log4j logger.

    public static final String MODID = "ae2extras";

    public static AbstractCraftingUnitBlock.CraftingUnitType STORAGE_256K;
    public static AbstractCraftingUnitBlock.CraftingUnitType STORAGE_1M;
    public static AbstractCraftingUnitBlock.CraftingUnitType STORAGE_4M;
    public static AbstractCraftingUnitBlock.CraftingUnitType STORAGE_16M;

    public static Block STORAGE256K;
    public static Block STORAGE1M;
    public static Block STORAGE4M;
    public static Block STORAGE16M;

    public static final ItemGroup TAB = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return STORAGE16M.asItem().getDefaultInstance();
        }
    };

    static Item.Properties props = new Item.Properties().group(TAB);
    static Item.Properties props_nostack = new Item.Properties().group(TAB).maxStackSize(1);

    public static Item CELL_COMPONENT_256K = new Item(props);
    public static Item ITEM_CELL_256K = new AdvancedStorageCellItem(props_nostack,256,2.5,() -> CELL_COMPONENT_256K);
    public static Item CELL_COMPONENT_1M = new Item(props);
    public static Item ITEM_CELL_1M = new AdvancedStorageCellItem(props_nostack,1024,3,() -> CELL_COMPONENT_1M);
    public static Item CELL_COMPONENT_4M = new Item(props);
    public static Item ITEM_CELL_4M = new AdvancedStorageCellItem(props_nostack,4096,3.5,() -> CELL_COMPONENT_4M);
    public static Item CELL_COMPONENT_16M = new Item(props);
    public static Item ITEM_CELL_16M = new AdvancedStorageCellItem(props_nostack,16384,4,() -> CELL_COMPONENT_16M);

    public static Item FLUID_CELL_COMPONENT_256K = new Item(props);
    public static Item FLUID_CELL_256K = new AdvancedFluidStorageCellItem(props_nostack,256,2.5,() -> CELL_COMPONENT_256K);
    public static Item FLUID_CELL_COMPONENT_1M = new Item(props);
    public static Item FLUID_CELL_1M = new AdvancedFluidStorageCellItem(props_nostack,1024,3,() -> CELL_COMPONENT_1M);
    public static Item FLUID_CELL_COMPONENT_4M = new Item(props);
    public static Item FLUID_CELL_4M = new AdvancedFluidStorageCellItem(props_nostack,4096,3.5,() -> CELL_COMPONENT_4M);
    public static Item FLUID_CELL_COMPONENT_16M = new Item(props);
    public static Item FLUID_CELL_16M = new AdvancedFluidStorageCellItem(props_nostack,16384,4,() -> CELL_COMPONENT_16M);


    public AE2Extras() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        bus.addGenericListener(Block.class,this::blocks);
        bus.addGenericListener(Item.class,this::items);
        bus.addGenericListener(IRecipeSerializer.class,this::recipes);
        bus.addListener(this::client);
    }

    private void client(FMLClientSetupEvent t) {
        RenderTypeLookup.setRenderLayer(STORAGE256K, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(STORAGE1M, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(STORAGE4M, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(STORAGE16M, RenderType.getCutout());

    }

    private void blocks(final RegistryEvent.Register<Block> event) {

        AbstractBlock.Properties craftingBlockProps = defaultProps(Material.IRON,MaterialColor.GRAY);

        STORAGE256K = register(event.getRegistry(), "256k_crafting_storage",new CraftingStorageBlockEx(craftingBlockProps,STORAGE_256K));
        STORAGE1M = register(event.getRegistry(), "1m_crafting_storage",new CraftingStorageBlockEx(craftingBlockProps, STORAGE_1M));
        STORAGE4M = register(event.getRegistry(), "4m_crafting_storage",new CraftingStorageBlockEx(craftingBlockProps, STORAGE_4M));
        STORAGE16M = register(event.getRegistry(), "16m_crafting_storage",new CraftingStorageBlockEx(craftingBlockProps, STORAGE_16M));

    }

    private void recipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        register(event.getRegistry(),"disassemble",DisassembleRecipe.SERIALIZER);
    }

    private void items(final RegistryEvent.Register<Item> event) {
        register(event.getRegistry(),STORAGE256K.getRegistryName(),new CraftingStorageItem(STORAGE256K,props));
        register(event.getRegistry(), STORAGE1M.getRegistryName(),new CraftingStorageItem(STORAGE1M,props));
        register(event.getRegistry(), STORAGE4M.getRegistryName(),new CraftingStorageItem(STORAGE4M,props));
        register(event.getRegistry(), STORAGE16M.getRegistryName(),new CraftingStorageItem(STORAGE16M,props));

        register(event.getRegistry(),"256k_cell_component",CELL_COMPONENT_256K);
        register(event.getRegistry(), "1m_cell_component",CELL_COMPONENT_1M);
        register(event.getRegistry(), "4m_cell_component",CELL_COMPONENT_4M);
        register(event.getRegistry(), "16m_cell_component",CELL_COMPONENT_16M);

        register(event.getRegistry(),"256k_storage_cell",ITEM_CELL_256K);
        register(event.getRegistry(), "1m_storage_cell",ITEM_CELL_1M);
        register(event.getRegistry(), "4m_storage_cell",ITEM_CELL_4M);
        register(event.getRegistry(), "16m_storage_cell",ITEM_CELL_16M);

        register(event.getRegistry(),"256k_fluid_cell_component",FLUID_CELL_COMPONENT_256K);
        register(event.getRegistry(), "1m_fluid_cell_component",FLUID_CELL_COMPONENT_1M);
        register(event.getRegistry(), "4m_fluid_cell_component",FLUID_CELL_COMPONENT_4M);
        register(event.getRegistry(), "16m_fluid_cell_component",FLUID_CELL_COMPONENT_16M);

        register(event.getRegistry(),"256k_fluid_storage_cell",FLUID_CELL_256K);
        register(event.getRegistry(), "1m_fluid_storage_cell",FLUID_CELL_1M);
        register(event.getRegistry(), "4m_fluid_storage_cell",FLUID_CELL_4M);
        register(event.getRegistry(), "16m_fluid_storage_cell",FLUID_CELL_16M);
    }

    private static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, ResourceLocation name, T obj) {
        registry.register(obj.setRegistryName(name));
        return obj;
    }

    private static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, String name, T obj) {
        register(registry, new ResourceLocation(MODID, name), obj);
        return obj;
    }

    /**
     * Utility function to create block properties with some sensible defaults for
     * AE blocks.
     */
    public static Block.Properties defaultProps(Material material, MaterialColor color) {
        return Block.Properties.create(material, color)
                // These values previousls were encoded in AEBaseBlock
                .hardnessAndResistance(2.2f, 11.f).harvestTool(ToolType.PICKAXE).harvestLevel(0)
                .sound(SoundType.METAL);
    }
}
