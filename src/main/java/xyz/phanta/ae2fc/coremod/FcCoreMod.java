package xyz.phanta.ae2fc.coremod;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

public class FcCoreMod implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        String pkgName = FcCoreMod.class.getPackage().getName() + ".";
        return new String[] {
                pkgName + "CraftingCpuTransformer",
                pkgName + "DualityInterfaceTransformer",
                pkgName + "CraftingTreeNodeTransformer",
<<<<<<< HEAD
                pkgName + "ContainerInterfaceTerminalTransformer"
=======
                pkgName + "TileUnpackagerTransformer",
                pkgName + "PackageCraftingPatternHelperTransformer"
>>>>>>> 2f4ff15b3ffaaffd4eac1e6e6609b774f990f72f
        };
    }

    @Nullable
    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // NO-OP
    }

    @Nullable
    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
