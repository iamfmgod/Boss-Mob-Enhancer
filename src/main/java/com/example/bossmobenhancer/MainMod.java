package com.example.bossmobenhancer;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import com.example.bossmobenhancer.network.PacketHandler;
import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;

@Mod(
        modid      = MainMod.MODID,
        name       = MainMod.NAME,
        version    = MainMod.VERSION,
        guiFactory = "com.example.bossmobenhancer.client.GuiFactory"
)
public class MainMod {
    public static final String MODID   = "bossmobenhancer";
    public static final String NAME    = "Boss Mob Enhancer";
    public static final String VERSION = "1.1.4";

    @SidedProxy(
            clientSide = "com.example.bossmobenhancer.client.ClientProxy",
            serverSide = "com.example.bossmobenhancer.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        // load scaling profiles & config
        File cfgDir = event.getModConfigurationDirectory();
        ScalingProfileLoader.load(cfgDir);
        ConfigHandler.init(new File(cfgDir, "bossmobenhancer.cfg"));

        // register our beacon TileEntity (silence deprecation warning)
        @SuppressWarnings("deprecation")
        String teName = new ResourceLocation(MODID, "enshrouded_beacon").toString();
        GameRegistry.registerTileEntity(TileEntityEnshroudedBeacon.class, teName);

        // initialize networking & GUI handler
        PacketHandler.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy.getGuiHandler());

        // register proxy‐side event handlers
        if (proxy != null) {
            MinecraftForge.EVENT_BUS.register(proxy);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (proxy != null) {
            proxy.init(event);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (proxy != null) {
            proxy.postInit(event);
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        // If you need server commands, register them here.
        // No need to call proxy.onServerStarting(...)
    }
}