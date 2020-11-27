package xyz.phanta.ae2fc.util;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.storage.ITerminalHost;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import appeng.container.implementations.ContainerCraftingStatus;
import appeng.container.implementations.ContainerInterface;
import appeng.container.implementations.ContainerPriority;
import appeng.fluids.container.ContainerFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.IPriorityHost;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.client.gui.*;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternTerminal;
import xyz.phanta.ae2fc.network.CPacketSwitchGuis;

import javax.annotation.Nullable;

public enum Ae2GuiUtils {
    DUAL_ITEM_INTERFACE(null),
    DUAL_FLUID_INTERFACE(null),
    FLUID_PATTERN_TERMINAL(SecurityPermissions.CRAFT),
    MY_PRIORITY(null),
    MY_CRAFTING_STATUS(null);

    private static final int OFFSET = 10;
    @Nullable
    private final SecurityPermissions requiredPermission;

    Ae2GuiUtils(@Nullable SecurityPermissions requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public static void switchGui(Ae2GuiUtils newGui) {
        Ae2FluidCrafting.PROXY.getNetHandler().sendToServer(new CPacketSwitchGuis(newGui.ordinal()));
    }

    public static void openGui(EntityPlayer p, @Nullable TileEntity tileEntity, Ae2GuiUtils gui, AEPartLocation side) {
        if (tileEntity != null) {
            BlockPos pos = tileEntity.getPos();
            p.openGui(Ae2FluidCrafting.INSTANCE, (gui.ordinal() + 1) << OFFSET | side.ordinal(),
                    tileEntity.getWorld(), pos.getX(),
                    pos.getY(), pos.getZ());
        }
    }

    public static Ae2GuiUtils valueOf(int index) {
        return Ae2GuiUtils.values()[index];
    }

    public boolean hasPermissions(@Nullable final TileEntity te,
                                  final AEPartLocation side,
                                  final EntityPlayer player) {
        final World w = player.getEntityWorld();
        BlockPos pos = new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ);
        if (Platform.hasPermissions(te != null ? new DimensionalCoord(te) : new DimensionalCoord(player.world, pos),
                player)) {
            if (te != null) {
                pos = te.getPos();
            }
            final TileEntity tile = w.getTileEntity(pos);
            if (tile instanceof IPartHost) {
                final IPart part = ((IPartHost) tile).getPart(side);
                if (part instanceof IActionHost && this.requiredPermission != null) {
                    final IGridNode gn = ((IActionHost) part).getActionableNode();
                    final IGrid g = gn.getGrid();
                    final ISecurityGrid sg = g.getCache(ISecurityGrid.class);
                    return sg.hasPermission(player, this.requiredPermission);
                }
                return true;
            }
        }
        return false;
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
        final Ae2GuiUtils gui = Ae2GuiUtils.valueOf((id >> OFFSET) - 1);
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
            case FLUID_PATTERN_TERMINAL: // PATTERN_TERMINAL
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof ITerminalHost) {
                    container = new ContainerFluidPatternTerminal(player.inventory, (ITerminalHost) myItem);
                }
                break;
            case MY_CRAFTING_STATUS: // CRAFTING_STATUS
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof ITerminalHost) {
                    container = new ContainerCraftingStatus(player.inventory, (ITerminalHost) myItem);
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
        final Ae2GuiUtils gui = Ae2GuiUtils.valueOf((id >> OFFSET) - 1);
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
            case FLUID_PATTERN_TERMINAL: // PRIORITY
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof ITerminalHost) {
                    return new GuiFluidPatternTerminal(player.inventory, (ITerminalHost) myItem);
                }
                break;
            case MY_CRAFTING_STATUS: // CRAFTING_STATUS
                myItem = getPartOrTile(world, new BlockPos(x, y, z), side);
                if (myItem instanceof ITerminalHost) {
                    return new GuiMyCraftingStatus(player.inventory, (ITerminalHost) myItem);
                }
                break;
            default:
                return null;
        }
        return null;
    }

}
