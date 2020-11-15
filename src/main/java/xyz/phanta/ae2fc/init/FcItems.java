package xyz.phanta.ae2fc.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.handler.RegistryHandler;
import xyz.phanta.ae2fc.item.*;

public class FcItems {

    public static final CreativeTabs TAB_AE2FC = new CreativeTabs(Ae2FluidCrafting.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(DENSE_ENCODED_PATTERN);
        }
    };

    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.ITEM_FLUID_DROP)
    public static ItemFluidDrop FLUID_DROP;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.ITEM_FLUID_PACKET)
    public static ItemFluidPacket FLUID_PACKET;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.ITEM_DENSE_ENCODED_PATTERN)
    public static ItemDenseEncodedPattern DENSE_ENCODED_PATTERN;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.ITEM_PART_DUAL_INTERFACE)
    public static ItemPartDualInterface PART_DUAL_INTERFACE;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.ITEM_PART_FLUID_PATTERN_TERMINAL)
    public static ItemPartFluidPatternTerminal PART_FLUID_PATTERN_TERMINAL;

    public static void init(RegistryHandler regHandler) {
        regHandler.item(NameConst.ITEM_FLUID_DROP, new ItemFluidDrop());
        regHandler.item(NameConst.ITEM_FLUID_PACKET, new ItemFluidPacket());
        regHandler.item(NameConst.ITEM_DENSE_ENCODED_PATTERN, new ItemDenseEncodedPattern());
        regHandler.item(NameConst.ITEM_PART_DUAL_INTERFACE, new ItemPartDualInterface());
        regHandler.item(NameConst.ITEM_PART_FLUID_PATTERN_TERMINAL, new ItemPartFluidPatternTerminal());
    }

}
