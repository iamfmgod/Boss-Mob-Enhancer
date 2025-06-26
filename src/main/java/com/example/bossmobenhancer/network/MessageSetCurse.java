package com.example.bossmobenhancer.network;

import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageSetCurse implements IMessage {
    private int curseId;

    // Default constructor for Forge
    public MessageSetCurse() {}

    public MessageSetCurse(int curseId) {
        this.curseId = curseId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.curseId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(curseId);
    }

    public static class Handler implements IMessageHandler<MessageSetCurse, IMessage> {
        @Override
        public IMessage onMessage(MessageSetCurse msg, MessageContext ctx) {
            // Schedule on the main server thread
            ctx.getServerHandler()
                    .player
                    .getServerWorld()
                    .addScheduledTask(() -> {
                        BlockPos pos = ctx.getServerHandler().player.getPosition();
                        TileEntity te = ctx.getServerHandler()
                                .player
                                .getServerWorld()
                                .getTileEntity(pos);
                        if (te instanceof TileEntityEnshroudedBeacon) {
                            ((TileEntityEnshroudedBeacon) te)
                                    .setActiveCurse(msg.curseId);
                        }
                    });
            return null; // no response packet
        }
    }
}