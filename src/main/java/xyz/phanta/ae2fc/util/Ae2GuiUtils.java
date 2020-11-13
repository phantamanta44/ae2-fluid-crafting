package xyz.phanta.ae2fc.util;

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
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.client.gui.GuiFluidDualInterface;
import xyz.phanta.ae2fc.client.gui.GuiItemDualInterface;
import xyz.phanta.ae2fc.client.gui.GuiMyPriority;
import xyz.phanta.ae2fc.network.CPacketSwitchGuis;

import javax.annotation.Nullable;

public enum Ae2GuiUtils {
    DUAL_ITEM_INTERFACE,
    DUAL_FLUID_INTERFACE,
    MY_PRIORITY;
    private static int offset = 10;

    public static void switchGui(Ae2GuiUtils newGui) {
        Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketSwitchGuis(newGui.ordinal()));
    }

    public static void openGui(EntityPlayer p, TileEntity tileEntity, Ae2GuiUtils gui, AEPartLocation side) {
        BlockPos pos = tileEntity.getPos();
        p.openGui(Ae2FluidCrafting.INSTANCE, (gui.ordinal() + 1) << offset | side.ordinal(),
                tileEntity.getWorld(), pos.getX(),
                pos.getY(), pos.getZ());
    }

    public static Ae2GuiUtils valueOf(int index) {
        return Ae2GuiUtils.values()[index];
    }

    @Nullable
    private static Object getPartOrTile(World world, BlockPos pos, AEPartLocation side) {
        Object item = world.getTileEntity(pos);
        if (item instanceof IPartHost) {
            return ((IPartHost) item).getPart(side);
        }
        return item;
    }

    @Nullable
    public static Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        // Ae2 GUI!! id = "(gui + 1) << offset | part side"
        final AEPartLocation side = AEPartLocation.fromOrdinal(id & 0x07);
        final Ae2GuiUtils gui = Ae2GuiUtils.valueOf((id >> offset) -1);
        AEBaseContainer container = null;
        Object myItem = null;
        switch (gui) {
            case DUAL_ITEM_INTERFACE:  // ITEM
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof IInterfaceHost) {
                    container = new ContainerInterface(player.inventory, (IInterfaceHost) myItem);
                }
                break;
            case DUAL_FLUID_INTERFACE: // FLUID
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof IFluidInterfaceHost) {
                    container = new ContainerFluidInterface(player.inventory, (IFluidInterfaceHost) myItem);
                }
                break;
            case MY_PRIORITY: // PRIORITY
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof IPriorityHost) {
                    container = new ContainerPriority(player.inventory, (IPriorityHost) myItem);
                }
                break;
            default:
                return null;
        }
        if (container != null) {
            container.setOpenContext(new ContainerOpenContext(myItem));
            container.getOpenContext().setWorld(world);
            container.getOpenContext().setX(x);
            container.getOpenContext().setY(y);
            container.getOpenContext().setZ(z);
            container.getOpenContext().setSide(side);
            return container;
        }
        return null;
    }

    @Nullable
    public static Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        // Ae2 GUI!! id = "(gui + 1) << offset | part side"
        final AEPartLocation side = AEPartLocation.fromOrdinal(id & 0x07);
        final Ae2GuiUtils gui = Ae2GuiUtils.valueOf((id >> offset) -1);
        Object myItem = null;
        switch (gui) {
            case DUAL_ITEM_INTERFACE: // ITEM
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof IInterfaceHost) {
                    return new GuiItemDualInterface(player.inventory, (IInterfaceHost) myItem);
                }
                break;
            case DUAL_FLUID_INTERFACE: // FLUID
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof IFluidInterfaceHost) {
                    return new GuiFluidDualInterface(player.inventory, (IFluidInterfaceHost) myItem);
                }
                break;
            case MY_PRIORITY: // PRIORITY
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof IPriorityHost) {
                    return new GuiMyPriority(player.inventory, (IPriorityHost) myItem);
                }
                break;
            default:
                return null;
        }
        return null;
    }

}
