package xyz.phanta.ae2fc.client.model;

import appeng.client.render.crafting.Ae2ModelHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.constant.NameConst;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class DenseEncodedPatternModel implements IModel {

    private static final ResourceLocation BASE_MODEL = Ae2FluidCrafting.resource("item/dense_encoded_pattern");

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.singletonList(BASE_MODEL);
    }

    // adapted from ae2's ItemEncodedPatternModel#bake
    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IBakedModel baseModel;
        try {
            baseModel = ModelLoaderRegistry.getModel(BASE_MODEL).bake(state, format, bakedTextureGetter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Ae2ModelHelper.bakeEncodedPatternModel(baseModel, PerspectiveMapWrapper.getTransforms(state));
    }

    public static class Loader implements ICustomModelLoader {

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            // NO-OP
        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            // modelLocation will probably be a ModelResourceLocation, so using compareTo lets us bypass the
            // ModelResourceLocation equality behaviour and fall back to that of ResourceLocation
            return modelLocation.compareTo(NameConst.MODEL_DENSE_ENCODED_PATTERN) == 0;
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) {
            return new DenseEncodedPatternModel();
        }

    }

}
