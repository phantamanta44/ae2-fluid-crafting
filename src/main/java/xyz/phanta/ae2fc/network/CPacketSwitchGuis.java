package xyz.phanta.ae2fc.network;

import appeng.api.parts.IPartHost;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import appeng.util.Platform;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.phanta.ae2fc.Ae2FluidCrafting;
import xyz.phanta.ae2fc.tile.TileDualInterface;

import javax.annotation.Nullable;

public class CPacketSwitchGuis implements IMessage {
    private int newGui;

    public CPacketSwitchGuis(final int newGui) {
        this.newGui = newGui;
    }

    public CPacketSwitchGuis() {

    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        newGui = byteBuf.readInt();
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        byteBuf.writeInt(newGui);
    }

    public static class Handler implements IMessageHandler<CPacketSwitchGuis, IMessage> {
        @Nullable
        @Override
        public IMessage onMessage(CPacketSwitchGuis message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            final Container c = player.openContainer;
            if (c instanceof AEBaseContainer) {
                if (Platform.isServer()){
                    AEBaseContainer bc = (AEBaseContainer) c;
                    ContainerOpenContext context = bc.getOpenContext();
                    if (context != null){
                        TileEntity te = context.getTile();
                        BlockPos blockPos = te.getPos();
                        player.openGui(Ae2FluidCrafting.INSTANCE,
                                message.newGui << 4 | context.getSide().ordinal(),
                                te.getWorld(),
                                blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    }

                }
            }
            return null;
        }

    }
}
