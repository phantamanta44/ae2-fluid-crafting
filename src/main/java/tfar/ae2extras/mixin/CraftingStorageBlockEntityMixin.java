package tfar.ae2extras.mixin;

import appeng.tile.crafting.CraftingStorageTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.ae2extras.MixinEvents;

@Mixin(CraftingStorageTileEntity.class)
public abstract class CraftingStorageBlockEntityMixin extends TileEntity {
    public CraftingStorageBlockEntityMixin(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Inject(method = "getStorageBytes", at = @At("HEAD"), cancellable = true,remap = false)
    private void biggerCraftingCpus$getStorageBytes(CallbackInfoReturnable<Integer> cir) {
        MixinEvents.getStorageBytes((CraftingStorageTileEntity)(Object)this,cir);
    }
}
