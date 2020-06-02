package xyz.phanta.ae2fc.constant;

import net.minecraft.util.ResourceLocation;
import xyz.phanta.ae2fc.Ae2FluidCrafting;

public class NameConst {

    public static final String BLOCK_FLUID_DISCRETIZER = "fluid_discretizer";
    public static final String BLOCK_FLUID_PATTERN_ENCODER = "fluid_pattern_encoder";
    public static final String BLOCK_FLUID_PACKET_DECODER = "fluid_packet_decoder";

    public static final String ITEM_FLUID_DROP = "fluid_drop";
    public static final String ITEM_FLUID_PACKET = "fluid_packet";
    public static final String ITEM_DENSE_ENCODED_PATTERN = "dense_encoded_pattern";

    public static final String TT_KEY = Ae2FluidCrafting.MOD_ID + ".tooltip.";
    public static final String TT_INVALID_FLUID = TT_KEY + "invalid_fluid";
    public static final String TT_PROCESSING_RECIPE_ONLY = TT_KEY + "processing_recipe_only";

    private static final String GUI_KEY = Ae2FluidCrafting.MOD_ID + ".gui.";
    public static final String GUI_FLUID_PATTERN_ENCODER = GUI_KEY + BLOCK_FLUID_PATTERN_ENCODER;
    public static final String GUI_FLUID_PACKET_DECODER = GUI_KEY + BLOCK_FLUID_PACKET_DECODER;

    public static final ResourceLocation MODEL_DENSE_ENCODED_PATTERN = Ae2FluidCrafting.resource("builtin/dense_encoded_pattern");

}
