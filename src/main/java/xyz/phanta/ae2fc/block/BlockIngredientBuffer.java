package xyz.phanta.ae2fc.block;

import appeng.block.AEBaseTileBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.phanta.ae2fc.inventory.GuiType;
import xyz.phanta.ae2fc.inventory.InventoryHandler;
import xyz.phanta.ae2fc.tile.TileIngredientBuffer;

public class BlockIngredientBuffer extends AEBaseTileBlock {

    public BlockIngredientBuffer() {
        super(Material.IRON);
        setTileEntity(TileIngredientBuffer.class);
        setOpaque(false);
        this.lightOpacity = 4;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        TileIngredientBuffer tile = getTileEntity(world, pos);
        if (tile != null) {
            if (!world.isRemote) {
                InventoryHandler.openGui(player, world, pos, facing, GuiType.INGREDIENT_BUFFER);
            }
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

}
