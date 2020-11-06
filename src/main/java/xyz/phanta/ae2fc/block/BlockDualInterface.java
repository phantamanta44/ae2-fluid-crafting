package xyz.phanta.ae2fc.block;

import appeng.api.util.AEPartLocation;
import appeng.api.util.IOrientable;
import appeng.block.AEBaseTileBlock;
import appeng.block.misc.BlockInterface;
import appeng.core.sync.GuiBridge;
import appeng.tile.misc.TileInterface;
import appeng.util.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.tile.TileDualInterface;

import javax.annotation.Nullable;

public class BlockDualInterface extends AEBaseTileBlock {
    private static final PropertyBool OMNIDIRECTIONAL = PropertyBool.create("omnidirectional");
    private static final PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockDualInterface() {
        super(Material.IRON);
        this.setTileEntity(TileDualInterface.class);
    }

    @Override
    protected IProperty[] getAEStates() {
        return new IProperty[]{OMNIDIRECTIONAL};
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, OMNIDIRECTIONAL, FACING);
    }

    @Override
    public boolean onActivated(final World w, final BlockPos pos, final EntityPlayer p, final EnumHand hand, final @Nullable ItemStack heldItem, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (p.isSneaking()) {
            return false;
        }
        final TileDualInterface tg = this.getTileEntity(w, pos);
        if (tg != null) {
            if (Platform.isServer()) {
                p.openGui(Ae2FluidCrafting.INSTANCE, 4 << 4 | AEPartLocation.INTERNAL.ordinal(), w, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
        return false;
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

    @Override
    protected boolean hasCustomRotation() {
        return true;
    }

    @Override
    protected void customRotateBlock(final IOrientable rotatable, final EnumFacing axis) {
        if (rotatable instanceof TileDualInterface) {
            ((TileDualInterface) rotatable).setSide(axis);
        }
    }
}
