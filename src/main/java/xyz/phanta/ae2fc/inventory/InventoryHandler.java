package xyz.phanta.ae2fc.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import xyz.phanta.ae2fc.client.gui.GuiBurette;
import xyz.phanta.ae2fc.client.gui.GuiFluidPacketDecoder;
import xyz.phanta.ae2fc.client.gui.GuiFluidPatternEncoder;
import xyz.phanta.ae2fc.client.gui.GuiIngredientBuffer;
import xyz.phanta.ae2fc.tile.TileBurette;
import xyz.phanta.ae2fc.tile.TileFluidPacketDecoder;
import xyz.phanta.ae2fc.tile.TileFluidPatternEncoder;
import xyz.phanta.ae2fc.tile.TileIngredientBuffer;
import xyz.phanta.ae2fc.util.Ae2GuiUtils;

import javax.annotation.Nullable;

public class InventoryHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileFluidPatternEncoder) {
                    return new ContainerFluidPatternEncoder(player.inventory, (TileFluidPatternEncoder) tile);
                }
                break;
            }
            case 1: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileFluidPacketDecoder) {
                    return new ContainerFluidPacketDecoder(player.inventory, (TileFluidPacketDecoder) tile);
                }
                break;
            }
            case 2: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileIngredientBuffer) {
                    return new ContainerIngredientBuffer(player.inventory, (TileIngredientBuffer) tile);
                }
                break;
            }
            case 3: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileBurette) {
                    return new ContainerBurette(player.inventory, (TileBurette) tile);
                }
                break;
            }
            default: {
                return Ae2GuiUtils.getServerGuiElement(id, player, world, x, y, z);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileFluidPatternEncoder) {
                    return new GuiFluidPatternEncoder(player.inventory, (TileFluidPatternEncoder) tile);
                }
                break;
            }
            case 1: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileFluidPacketDecoder) {
                    return new GuiFluidPacketDecoder(player.inventory, (TileFluidPacketDecoder) tile);
                }
                break;
            }
            case 2: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileIngredientBuffer) {
                    return new GuiIngredientBuffer(player.inventory, (TileIngredientBuffer) tile);
                }
                break;
            }
            case 3: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileBurette) {
                    return new GuiBurette(player.inventory, (TileBurette) tile);
                }
                break;
            }
            default: {
                return Ae2GuiUtils.getClientGuiElement(id, player, world, x, y, z);
            }
        }
        return null;
    }

}
