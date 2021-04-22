package tfar.ae2extras.mixin;

import appeng.block.crafting.AbstractCraftingUnitBlock;
import tfar.ae2extras.GrossAE2Hacks;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.ae2extras.AE2Extras;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(targets = "appeng.client.render.crafting.CraftingCubeModel")
public abstract class CraftingCubeModelMixin {

    @Shadow(remap = false)
    @Final
    private AbstractCraftingUnitBlock.CraftingUnitType type;

    @Shadow(remap = false)
    @Final
    private static RenderMaterial LIGHT_BASE;

    @Shadow(remap = false)
    @Final
    private static RenderMaterial RING_CORNER;

    @Shadow(remap = false)
    @Final
    private static RenderMaterial RING_SIDE_HOR;

    @Shadow(remap = false)
    @Final
    private static RenderMaterial RING_SIDE_VER;

    @Inject(method = "bake(Lnet/minecraftforge/client/model/IModelConfiguration;Lnet/minecraft/client/renderer/model/ModelBakery;Ljava/util/function/Function;Lnet/minecraft/client/renderer/model/IModelTransform;Lnet/minecraft/client/renderer/model/ItemOverrideList;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/model/IBakedModel;"
            , at = @At("HEAD"), cancellable = true,remap = false)
    private void bakeAe2Extras(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation, CallbackInfoReturnable<IBakedModel> cir) {
        if (this.type == AE2Extras.STORAGE_256K || this.type == AE2Extras.STORAGE_1M | this.type == AE2Extras.STORAGE_4M || this.type == AE2Extras.STORAGE_16M) {
            TextureAtlasSprite ringCorner = spriteGetter.apply(RING_CORNER);
            TextureAtlasSprite ringSideHor = spriteGetter.apply(RING_SIDE_HOR);
            TextureAtlasSprite ringSideVer = spriteGetter.apply(RING_SIDE_VER);
            cir.setReturnValue(GrossAE2Hacks.getModel(this,ringCorner, ringSideHor,ringSideVer, spriteGetter,LIGHT_BASE,this.type));
        }
    }
    @Inject(method = "getAdditionalTextures()Ljava/util/stream/Stream;",at = @At("RETURN"),cancellable = true,remap = false)
    private void addTextures(CallbackInfoReturnable<Stream<RenderMaterial>> cir) {
        Stream<RenderMaterial> materialStream = cir.getReturnValue();
        List<RenderMaterial> materialList = materialStream.collect(Collectors.toList());
        materialList.addAll(GrossAE2Hacks.materials);
        cir.setReturnValue(materialList.stream());
    }
}
