package xyz.phanta.ae2fc.block;

import appeng.block.AEBaseTileBlock;
import net.minecraft.block.material.Material;
import xyz.phanta.ae2fc.tile.TileFluidDiscretizer;

public class BlockFluidDiscretizer extends AEBaseTileBlock {

    public BlockFluidDiscretizer() {
        super(Material.IRON);
        setTileEntity(TileFluidDiscretizer.class);
    }

}
