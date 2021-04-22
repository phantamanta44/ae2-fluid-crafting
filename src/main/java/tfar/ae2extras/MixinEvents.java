package tfar.ae2extras;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.tile.crafting.CraftingStorageTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class MixinEvents {
    public static void getStorageBytes(CraftingStorageTileEntity te, CallbackInfoReturnable<Integer> cir) {
        if (te.getWorld() != null && !te.notLoaded() && !te.isRemoved()) {
            World world = te.getWorld();
            BlockPos pos = te.getPos();
            AbstractCraftingUnitBlock unit = (AbstractCraftingUnitBlock) world.getBlockState(pos).getBlock();
            if (unit.type == AE2Extras.STORAGE_256K) {
                cir.setReturnValue(65536 * 4);
            } else if (unit.type == AE2Extras.STORAGE_1M) {
                cir.setReturnValue(65536 * 16);
            } else if (unit.type == AE2Extras.STORAGE_4M) {
                cir.setReturnValue(65536 * 64);
            } else if (unit.type == AE2Extras.STORAGE_16M) {
                cir.setReturnValue(65536 * 256);
            }
        }
    }
}
