package xyz.phanta.ae2fc.tile;

import appeng.tile.misc.TileInterface;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import xyz.phanta.ae2fc.util.DualityDInterface;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TileDualInterface extends TileInterface {

    public TileDualInterface() {
        super();
        //modify "private final DualityInterface duality" to "DualityDInterface"
        try {
            Field field = ReflectionHelper.findField(TileInterface.class, "duality");
            field.setAccessible(true);
            Field modifiersField = ReflectionHelper.findField(Field.class, "modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(this, new DualityDInterface(this.getProxy(), this));
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
