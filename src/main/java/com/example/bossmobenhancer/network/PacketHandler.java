package com.example.bossmobenhancer.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import com.example.bossmobenhancer.MainMod;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("bossmobenhancer");

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(MessageSetCurse.Handler.class, MessageSetCurse.class, id++, Side.SERVER);
    }
}