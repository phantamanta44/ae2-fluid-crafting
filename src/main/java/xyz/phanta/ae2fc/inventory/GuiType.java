package xyz.phanta.ae2fc.inventory;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.storage.ITerminalHost;
import appeng.api.util.AEPartLocation;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import appeng.container.implementations.ContainerCraftingStatus;
import appeng.container.implementations.ContainerInterface;
import appeng.container.implementations.ContainerPriority;
import appeng.fluids.container.ContainerFluidInterface;
import appeng.fluids.helper.IFluidInterfaceHost;
import appeng.helpers.IInterfaceHost;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.phanta.ae2fc.client.gui.*;
import xyz.phanta.ae2fc.parts.PartFluidPatternTerminal;
import xyz.phanta.ae2fc.tile.TileBurette;
import xyz.phanta.ae2fc.tile.TileFluidPacketDecoder;
import xyz.phanta.ae2fc.tile.TileFluidPatternEncoder;
import xyz.phanta.ae2fc.tile.TileIngredientBuffer;
import xyz.phanta.ae2fc.tile.base.FcPriorityHost;

import javax.annotation.Nullable;
import java.util.List;

public enum GuiType {

    FLUID_PATTERN_ENCODER(new TileGuiFactory<TileFluidPatternEncoder>(TileFluidPatternEncoder.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, TileFluidPatternEncoder inv) {
            return new ContainerFluidPatternEncoder(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, TileFluidPatternEncoder inv) {
            return new GuiFluidPatternEncoder(player.inventory, inv);
        }
    }),

    FLUID_PACKET_DECODER(new TileGuiFactory<TileFluidPacketDecoder>(TileFluidPacketDecoder.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, TileFluidPacketDecoder inv) {
            return new ContainerFluidPacketDecoder(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, TileFluidPacketDecoder inv) {
            return new GuiFluidPacketDecoder(player.inventory, inv);
        }
    }),

    INGREDIENT_BUFFER(new TileGuiFactory<TileIngredientBuffer>(TileIngredientBuffer.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, TileIngredientBuffer inv) {
            return new ContainerIngredientBuffer(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, TileIngredientBuffer inv) {
            return new GuiIngredientBuffer(player.inventory, inv);
        }
    }),

    PRECISION_BURETTE(new TileGuiFactory<TileBurette>(TileBurette.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, TileBurette inv) {
            return new ContainerBurette(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, TileBurette inv) {
            return new GuiBurette(player.inventory, inv);
        }
    }),

    DUAL_ITEM_INTERFACE(new PartOrTileGuiFactory<IInterfaceHost>(IInterfaceHost.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, IInterfaceHost inv) {
            return new ContainerInterface(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, IInterfaceHost inv) {
            return new GuiItemDualInterface(player.inventory, inv);
        }
    }),

    DUAL_FLUID_INTERFACE(new PartOrTileGuiFactory<IFluidInterfaceHost>(IFluidInterfaceHost.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, IFluidInterfaceHost inv) {
            return new ContainerFluidInterface(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, IFluidInterfaceHost inv) {
            return new GuiFluidDualInterface(player.inventory, inv);
        }
    }),

    FLUID_PATTERN_TERMINAL(new PartGuiFactory<ITerminalHost>(ITerminalHost.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, ITerminalHost inv) {
            return new ContainerFluidPatternTerminal(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, ITerminalHost inv) {
            return new GuiFluidPatternTerminal(player.inventory, inv);
        }
    }),

    PRIORITY(new PartOrTileGuiFactory<FcPriorityHost>(FcPriorityHost.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, FcPriorityHost inv) {
            return new ContainerPriority(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, FcPriorityHost inv) {
            return new GuiFcPriority(player.inventory, inv);
        }
    }),

    FLUID_PAT_TERM_CRAFTING_STATUS(new PartGuiFactory<PartFluidPatternTerminal>(PartFluidPatternTerminal.class) {
        @Override
        protected Object createServerGui(EntityPlayer player, PartFluidPatternTerminal inv) {
            return new ContainerCraftingStatus(player.inventory, inv);
        }

        @Override
        protected Object createClientGui(EntityPlayer player, PartFluidPatternTerminal inv) {
            return new GuiFluidPatternTerminalCraftingStatus(player.inventory, inv);
        }
    });

    public static final List<GuiType> VALUES = ImmutableList.copyOf(values());

    @Nullable
    public static GuiType getByOrdinal(int ordinal) {
        return ordinal < 0 || ordinal >= VALUES.size() ? null : VALUES.get(ordinal);
    }

    final GuiFactory guiFactory;

    GuiType(GuiFactory guiFactory) {
        this.guiFactory = guiFactory;
    }

    public interface GuiFactory {

        @Nullable
        Object createServerGui(EntityPlayer player, World world, int x, int y, int z, EnumFacing face);

        @SideOnly(Side.CLIENT)
        @Nullable
        Object createClientGui(EntityPlayer player, World world, int x, int y, int z, EnumFacing face);

    }

    private static abstract class TileGuiFactory<T> implements GuiFactory {

        protected final Class<T> invClass;

        TileGuiFactory(Class<T> invClass) {
            this.invClass = invClass;
        }

        @Nullable
        protected T getInventory(TileEntity tile, EnumFacing face) {
            return invClass.isInstance(tile) ? invClass.cast(tile) : null;
        }

        @Nullable
        @Override
        public Object createServerGui(EntityPlayer player, World world, int x, int y, int z, EnumFacing face) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile == null) {
                return null;
            }
            T inv = getInventory(tile, face);
            if (inv == null) {
                return null;
            }
            Object gui = createServerGui(player, inv);
            if (gui instanceof AEBaseContainer) {
                ContainerOpenContext ctx = new ContainerOpenContext(inv);
                ctx.setWorld(world);
                ctx.setX(x);
                ctx.setY(y);
                ctx.setZ(z);
                ctx.setSide(AEPartLocation.fromFacing(face));
                ((AEBaseContainer)gui).setOpenContext(ctx);
            }
            return gui;
        }

        @Nullable
        protected abstract Object createServerGui(EntityPlayer player, T inv);

        @Nullable
        @Override
        public Object createClientGui(EntityPlayer player, World world, int x, int y, int z, EnumFacing face) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile == null) {
                return null;
            }
            T inv = getInventory(tile, face);
            return inv != null ? createClientGui(player, inv) : null;
        }

        @Nullable
        protected abstract Object createClientGui(EntityPlayer player, T inv);

    }

    private static abstract class PartOrTileGuiFactory<T> extends TileGuiFactory<T> {

        PartOrTileGuiFactory(Class<T> invClass) {
            super(invClass);
        }

        @Nullable
        @Override
        protected T getInventory(TileEntity tile, EnumFacing face) {
            if (tile instanceof IPartHost) {
                IPart part = ((IPartHost)tile).getPart(face);
                if (invClass.isInstance(part)) {
                    return invClass.cast(part);
                }
            }
            return super.getInventory(tile, face);
        }

    }

    private static abstract class PartGuiFactory<T> extends TileGuiFactory<T> {

        PartGuiFactory(Class<T> invClass) {
            super(invClass);
        }

        @Nullable
        @Override
        protected T getInventory(TileEntity tile, EnumFacing face) {
            if (tile instanceof IPartHost) {
                IPart part = ((IPartHost)tile).getPart(face);
                if (invClass.isInstance(part)) {
                    return invClass.cast(part);
                }
            }
            return null;
        }

    }

}
