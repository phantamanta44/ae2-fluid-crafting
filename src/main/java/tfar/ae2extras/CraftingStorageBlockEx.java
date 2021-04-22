package tfar.ae2extras;

import appeng.block.crafting.CraftingStorageBlock;
import appeng.core.AppEng;
import appeng.tile.crafting.CraftingStorageTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftingStorageBlockEx extends CraftingStorageBlock {
    public CraftingStorageBlockEx(Properties props, CraftingUnitType type) {
        super(props, type);
        setTileEntity(CraftingStorageTileEntity.class, () -> new
                CraftingStorageTileEntity(ForgeRegistries.TILE_ENTITIES.getValue(new ResourceLocation(AppEng.MOD_ID,"crafting_storage"))));
    }
}
