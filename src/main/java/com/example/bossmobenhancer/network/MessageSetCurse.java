package com.example.bossmobenhancer.network;

import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageSetCurse implements IMessage {

    private int curseId;

    public MessageSetCurse() {}  // Needed by Forge

    public MessageSetCurse(int curseId) {
        this.curseId = curseId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(curseId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.curseId = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MessageSetCurse, IMessage> {
        @Override
        public IMessage onMessage(MessageSetCurse message, MessageContext ctx) {
            World world = ctx.getServerHandler().player.world;
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                TileEntity tile = world.getTileEntity(ctx.getServerHandler().player.getPosition());
                if (tile instanceof TileEntityEnshroudedBeacon) {
                    ((TileEntityEnshroudedBeacon) tile).setActiveCurse(message.curseId);
                }
            });
            return null;
        }
    }
}