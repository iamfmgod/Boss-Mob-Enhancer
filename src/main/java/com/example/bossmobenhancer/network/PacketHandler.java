package com.example.bossmobenhancer.network;

import com.example.bossmobenhancer.MainMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler {
    // Use your mod’s actual modid as the channel name
    public static final SimpleNetworkWrapper INSTANCE =
            NetworkRegistry.INSTANCE.newSimpleChannel(MainMod.MODID);

    private PacketHandler() { /* no instances */ }

    public static void init() {
        int disc = 0;
        // Register server‐side message handler
        INSTANCE.registerMessage(
                MessageSetCurse.Handler.class,
                MessageSetCurse.class,
                disc++,
                Side.SERVER
        );

        // If you later add client‐bound packets, register them here:
        // INSTANCE.registerMessage(ClientMessage.Handler.class, ClientMessage.class, disc++, Side.CLIENT);

        // Removed corruption particles message registration
    }
}