package xyz.phanta.ae2fc.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.block.*;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.handler.RegistryHandler;

public class FcBlocks {

    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_FLUID_DISCRETIZER)
    public static BlockFluidDiscretizer FLUID_DISCRETIZER;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_FLUID_PATTERN_ENCODER)
    public static BlockFluidPatternEncoder FLUID_PATTERN_ENCODER;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_FLUID_PACKET_DECODER)
    public static BlockFluidPacketDecoder FLUID_PACKET_DECODER;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_INGREDIENT_BUFFER)
    public static BlockIngredientBuffer INGREDIENT_BUFFER;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_BURETTE)
    public static BlockBurette BURETTE;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BlOCK_DUAL_INTERFACE)
    public static BlockDualInterface Dial_Interface;

    public static void init(RegistryHandler regHandler) {
        regHandler.block(NameConst.BLOCK_FLUID_DISCRETIZER, new BlockFluidDiscretizer());
        regHandler.block(NameConst.BLOCK_FLUID_PATTERN_ENCODER, new BlockFluidPatternEncoder());
        regHandler.block(NameConst.BLOCK_FLUID_PACKET_DECODER, new BlockFluidPacketDecoder());
        regHandler.block(NameConst.BLOCK_INGREDIENT_BUFFER, new BlockIngredientBuffer());
        regHandler.block(NameConst.BLOCK_BURETTE, new BlockBurette());
        regHandler.block(NameConst.BlOCK_DUAL_INTERFACE, new BlockDualInterface());
    }

}
