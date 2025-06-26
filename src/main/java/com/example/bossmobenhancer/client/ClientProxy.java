package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Client-only proxy: wires up all client-side renderers, overlays, model registrations, GUIs, etc.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        super.preInit(evt);
        MinecraftForge.EVENT_BUS.register(this); // for ModelRegistryEvent
    }

    @Override
    public void init(FMLInitializationEvent evt) {
        super.init(evt);
        MinecraftForge.EVENT_BUS.register(new BossOverlayHandler());
        MinecraftForge.EVENT_BUS.register(new CustomBossBarRenderer());
        // TESRs, keybindings, reload listeners, etc.
    }

    @Override
    public void postInit(FMLPostInitializationEvent evt) {
        super.postInit(evt);
        // any post-init client work
    }

    @Override
    public IGuiHandler getGuiHandler() {
        // returns the shared handler that opens containers / GUIs
        return new com.example.bossmobenhancer.GuiHandler();
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent evt) {
        // ModelLoader.setCustomModelResourceLocation(...)
    }
}