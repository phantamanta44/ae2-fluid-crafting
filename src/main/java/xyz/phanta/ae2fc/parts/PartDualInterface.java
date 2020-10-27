package xyz.phanta.ae2fc.parts;

import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.misc.PartInterface;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.init.FcItems;
import xyz.phanta.ae2fc.util.DualityDInterface;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PartDualInterface extends PartInterface {
    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_base");
    public static final ResourceLocation MODEL_ON = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_on");
    public static final ResourceLocation MODEL_OFF = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_off");
    public static final ResourceLocation MODEL_HAS_CHANNEL = new ResourceLocation(Ae2FluidCrafting.MOD_ID, "part/interface_has_channel");
    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF);

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON);

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_HAS_CHANNEL);

    public PartDualInterface(ItemStack is) {
        super(is);
        //modify "private final DualityInterface duality" to "DualityDInterface"
        try {
            Field field = ReflectionHelper.findField(PartInterface.class, "duality");
            field.setAccessible(true);
            Field modifiersField = ReflectionHelper.findField(Field.class, "modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(this, new DualityDInterface(this.getProxy(), this));
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return new ItemStack(FcItems.PART_DUAL_INTERFACE, 1);
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }
}
