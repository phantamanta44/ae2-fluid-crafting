package xyz.phanta.ae2fc.block;

import appeng.block.AEBaseTileBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.tile.TileBurette;

public class BlockBurette extends AEBaseTileBlock {

    public BlockBurette() {
        super(Material.IRON);
        setTileEntity(TileBurette.class);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        TileBurette tile = getTileEntity(world, pos);
        if (tile != null) {
            if (!world.isRemote) {
                player.openGui(Ae2FluidCrafting.INSTANCE, 3, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
    }

}
