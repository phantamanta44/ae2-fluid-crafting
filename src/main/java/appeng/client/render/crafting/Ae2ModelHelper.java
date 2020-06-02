package appeng.client.render.crafting;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.common.model.TRSRTransformation;

// package private stuff :)
public class Ae2ModelHelper {

    public static IBakedModel bakeEncodedPatternModel(IBakedModel baseModel,
                                                      ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
        return new ItemEncodedPatternBakedModel(baseModel, transforms);
    }

}
