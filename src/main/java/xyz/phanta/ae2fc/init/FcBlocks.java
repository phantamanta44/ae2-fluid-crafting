package xyz.phanta.ae2fc.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.block.BlockFluidDiscretizer;
import xyz.phanta.ae2fc.block.BlockFluidPacketDecoder;
import xyz.phanta.ae2fc.block.BlockFluidPatternEncoder;
import xyz.phanta.ae2fc.constant.NameConst;
import xyz.phanta.ae2fc.handler.RegistryHandler;

@SuppressWarnings("NotNullFieldNotInitialized")
public class FcBlocks {

    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_FLUID_DISCRETIZER)
    public static BlockFluidDiscretizer FLUID_DISCRETIZER;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_FLUID_PATTERN_ENCODER)
    public static BlockFluidPatternEncoder FLUID_PATTERN_ENCODER;
    @GameRegistry.ObjectHolder(Ae2FluidCrafting.MOD_ID + ":" + NameConst.BLOCK_FLUID_PACKET_DECODER)
    public static BlockFluidPacketDecoder FLUID_PACKET_DECODER;

    public static void init(RegistryHandler regHandler) {
        regHandler.block(NameConst.BLOCK_FLUID_DISCRETIZER, new BlockFluidDiscretizer());
        regHandler.block(NameConst.BLOCK_FLUID_PATTERN_ENCODER, new BlockFluidPatternEncoder());
        regHandler.block(NameConst.BLOCK_FLUID_PACKET_DECODER, new BlockFluidPacketDecoder());
    }

}
