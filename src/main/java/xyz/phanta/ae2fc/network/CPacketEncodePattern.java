package xyz.phanta.ae2fc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xyz.phanta.ae2fc.inventory.ContainerFluidPatternEncoder;

import javax.annotation.Nullable;

public class CPacketEncodePattern implements IMessage {

    @Override
    public void toBytes(ByteBuf buf) {
        // NO-OP
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // NO-OP
    }

    public static class Handler implements IMessageHandler<CPacketEncodePattern, IMessage> {

        @Nullable
        @Override
        public IMessage onMessage(CPacketEncodePattern message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (player.openContainer instanceof ContainerFluidPatternEncoder) {
                    ((ContainerFluidPatternEncoder)player.openContainer).encodePattern();
                }
            });
            return null;
        }

    }

}
