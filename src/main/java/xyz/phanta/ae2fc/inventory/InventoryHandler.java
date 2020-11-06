package xyz.phanta.ae2fc.inventory;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.AEPartLocation;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import appeng.container.implementations.ContainerInterface;
import appeng.container.implementations.ContainerPriority;
import appeng.fluids.container.ContainerFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.IPriorityHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import xyz.phanta.ae2fc.client.gui.*;
import xyz.phanta.ae2fc.parts.PartDualInterface;
import xyz.phanta.ae2fc.tile.*;

import javax.annotation.Nullable;

public class InventoryHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (id) {
            case 0: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileFluidPatternEncoder) {
                    return new ContainerFluidPatternEncoder(player.inventory, (TileFluidPatternEncoder)tile);
                }
                break;
            }
            case 1: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileFluidPacketDecoder) {
                    return new ContainerFluidPacketDecoder(player.inventory, (TileFluidPacketDecoder)tile);
                }
                break;
            }
            case 2: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileIngredientBuffer) {
                    return new ContainerIngredientBuffer(player.inventory, (TileIngredientBuffer)tile);
                }
                break;
            }
            case 3: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileBurette) {
                    return new ContainerBurette(player.inventory, (TileBurette)tile);
                }
                break;
            }
        }
        // Dual GUI!! ID = "id << 4 | side"
        final AEPartLocation side = AEPartLocation.fromOrdinal( id & 0x07 );
        final int ID = id >> 4;
        switch (ID) {
            case 4: { // ITEM
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                IInterfaceHost te = null;
                if (tile instanceof IPartHost) { // PART
                    IPart part = ((IPartHost) tile).getPart(side);
                    if(part instanceof PartDualInterface)
                        te = (PartDualInterface)part;
                } else if (tile instanceof TileDualInterface) // BLOCK
                    te = (TileDualInterface)tile;
                if (te == null) break;
                AEBaseContainer bc =  new ContainerInterface(player.inventory, te);
                bc.setOpenContext(new ContainerOpenContext(te));
                bc.getOpenContext().setWorld( world );
                bc.getOpenContext().setX( x );
                bc.getOpenContext().setY( y );
                bc.getOpenContext().setZ( z );
                bc.getOpenContext().setSide( side );
                return bc;
            }
            case 5: { // FLUID
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                IFluidInterfaceHost te = null;
                if (tile instanceof IPartHost) { // PART
                    IPart part = ((IPartHost) tile).getPart(side);
                    if(part instanceof PartDualInterface)
                        te = (PartDualInterface)part;
                } else if (tile instanceof TileDualInterface) // BLOCK
                    te = (TileDualInterface)tile;
                if (te == null) break;
                AEBaseContainer bc =  new ContainerFluidInterface(player.inventory, te);
                bc.setOpenContext(new ContainerOpenContext(te));
                bc.getOpenContext().setWorld( world );
                bc.getOpenContext().setX( x );
                bc.getOpenContext().setY( y );
                bc.getOpenContext().setZ( z );
                bc.getOpenContext().setSide( side );
                return bc;
            }
            case 6: { // PRIORITY
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                IPriorityHost te = null;
                if (tile instanceof IPartHost) { // PART
                    IPart part = ((IPartHost) tile).getPart(side);
                    if(part instanceof PartDualInterface)
                        te = (PartDualInterface)part;
                } else if (tile instanceof TileDualInterface) // BLOCK
                    te = (TileDualInterface)tile;
                if (te == null) break;
                AEBaseContainer bc =  new ContainerPriority(player.inventory, te);
                bc.setOpenContext(new ContainerOpenContext(te));
                bc.getOpenContext().setWorld( world );
                bc.getOpenContext().setX( x );
                bc.getOpenContext().setY( y );
                bc.getOpenContext().setZ( z );
                bc.getOpenContext().setSide( side );
                return bc;
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
                    return new GuiFluidPatternEncoder(player.inventory, (TileFluidPatternEncoder)tile);
                }
                break;
            }
            case 1: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileFluidPacketDecoder) {
                    return new GuiFluidPacketDecoder(player.inventory, (TileFluidPacketDecoder)tile);
                }
                break;
            }
            case 2: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileIngredientBuffer) {
                    return new GuiIngredientBuffer(player.inventory, (TileIngredientBuffer)tile);
                }
                break;
            }
            case 3: {
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof TileBurette) {
                    return new GuiBurette(player.inventory, (TileBurette)tile);
                }
                break;
            }
        }
        // Dual GUI!! ID = "id << 4 | side"
        final AEPartLocation side = AEPartLocation.fromOrdinal( id & 0x07 );
        final int ID = id >> 4;
        switch (ID) {
            case 4: { // ITEM
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof IPartHost) { // PART
                    IPart part = ((IPartHost) tile).getPart(side);
                    if(part instanceof PartDualInterface)
                        return new GuiItemDualInterface(player.inventory, (PartDualInterface)part);
                } else if (tile instanceof TileDualInterface) // BLOCK
                    return new GuiItemDualInterface(player.inventory, (TileDualInterface)tile);
                break;
            }
            case 5: { // FLUID
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof IPartHost) { // PART
                    IPart part = ((IPartHost) tile).getPart(side);
                    if(part instanceof PartDualInterface)
                        return new GuiFluidDualInterface(player.inventory, (PartDualInterface)part);
                } else if (tile instanceof TileDualInterface) // BLOCK
                    return new GuiFluidDualInterface(player.inventory, (TileDualInterface)tile);
                break;
            }
            case 6: { // PRIORITY
                TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                if (tile instanceof IPartHost) { // PART
                    IPart part = ((IPartHost) tile).getPart(side);
                    if(part instanceof PartDualInterface)
                        return new GuiPriority(player.inventory, (PartDualInterface)part);
                } else if (tile instanceof TileDualInterface) // BLOCK
                    return new GuiPriority(player.inventory, (TileDualInterface)tile);
                break;
            }
        }
        return null;
    }

}
