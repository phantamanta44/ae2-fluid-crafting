package xyz.phanta.ae2fc.block;

import appeng.api.util.AEPartLocation;
import appeng.block.AEBaseTileBlock;
import appeng.core.sync.GuiBridge;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.phanta.ae2fc.tile.TileFluidDiscretizer;

public class BlockFluidDiscretizer extends AEBaseTileBlock {

    public BlockFluidDiscretizer() {
        super(Material.IRON);
        setTileEntity(TileFluidDiscretizer.class);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        TileFluidDiscretizer tile = getTileEntity(world, pos);
        if (tile != null) {
            if (!world.isRemote) {
                Platform.openGUI(player, tile, AEPartLocation.fromFacing(facing), GuiBridge.GUI_PRIORITY);
            }
            return true;
        }
        return false;
    }

}
