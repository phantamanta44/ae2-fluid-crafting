package xyz.phanta.ae2fc.block;

import appeng.block.misc.BlockInterface;
import appeng.tile.misc.TileInterface;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import xyz.phanta.ae2fc.tile.TileDualInterface;

public class BlockDualInterface extends BlockInterface {
    private static final PropertyBool OMNIDIRECTIONAL = PropertyBool.create("omnidirectional");
    private static final PropertyDirection FACING = PropertyDirection.create("facing");
    public BlockDualInterface() {
        super();
        this.setTileEntity(TileDualInterface.class);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isRemote) {
            return;
        }
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile == null) {
            return;
        }
        if (tile instanceof TileDualInterface && placer instanceof EntityPlayer) {
            ((TileDualInterface) tile).setPlacer((EntityPlayer) placer);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OMNIDIRECTIONAL, FACING);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        // Determine whether the interface is omni-directional or not
        TileDualInterface te = this.getTileEntity(world, pos);
        boolean omniDirectional = true; // The default
        EnumFacing facing = EnumFacing.NORTH;
        if (te != null) {
            omniDirectional = te.isOmniDirectional();
            facing = te.getForward();
        }

        return super.getActualState(state, world, pos)
                .withProperty(OMNIDIRECTIONAL, omniDirectional).withProperty(FACING, facing);
    }
}
