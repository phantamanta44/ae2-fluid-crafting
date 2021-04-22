package tfar.ae2extras.mixin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.ae2extras.AE2Extras;

@Mixin(AbstractCraftingUnitBlock.CraftingUnitType.class)
public class CraftingUnitTypeMixin {
	@Final
	@Mutable
	@Shadow(remap = false)
	@SuppressWarnings("target")
	private static AbstractCraftingUnitBlock.CraftingUnitType[] $VALUES;

	@Inject(at = @At("RETURN"), method = "<clinit>")
	private static void init(CallbackInfo info) throws Throwable {
		MethodHandle ctor = MethodHandles.lookup().findConstructor(AbstractCraftingUnitBlock.CraftingUnitType.class, MethodType.methodType(void.class, String.class, int.class));
		AE2Extras.STORAGE_256K = (AbstractCraftingUnitBlock.CraftingUnitType) ctor.invoke("STORAGE_256K", $VALUES.length);
		AE2Extras.STORAGE_1M = (AbstractCraftingUnitBlock.CraftingUnitType) ctor.invoke("STORAGE_1M", $VALUES.length + 1);
		AE2Extras.STORAGE_4M = (AbstractCraftingUnitBlock.CraftingUnitType) ctor.invoke("STORAGE_4M", $VALUES.length + 2);
		AE2Extras.STORAGE_16M = (AbstractCraftingUnitBlock.CraftingUnitType) ctor.invoke("STORAGE_16M", $VALUES.length + 3);
		$VALUES = ArrayUtils.addAll($VALUES, AE2Extras.STORAGE_256K, AE2Extras.STORAGE_1M, AE2Extras.STORAGE_4M, AE2Extras.STORAGE_16M);
	}
}
