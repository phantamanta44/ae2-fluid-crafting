package xyz.phanta.ae2fc.block;

import appeng.block.misc.BlockInterface;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import xyz.phanta.ae2fc.tile.TileDualInterface;

public class BlockDualInterface extends BlockInterface {
    private static final PropertyBool OMNIDIRECTIONAL = PropertyBool.create("omnidirectional");
    private static final PropertyDirection FACING = PropertyDirection.create("facing");
    public BlockDualInterface() {
        super();
        this.setTileEntity(TileDualInterface.class);
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
