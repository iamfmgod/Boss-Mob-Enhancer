package com.example.bossmobenhancer;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy {
    /** Called during FMLPreInitializationEvent */
    public void preInit(FMLPreInitializationEvent evt) {
        // server‐side setup (config reload listeners, world gen, etc.)
    }

    /** Called during FMLInitializationEvent */
    public void init(FMLInitializationEvent evt) {
        // server‐side setup (register commands, data managers, etc.)
    }

    /** Called during FMLPostInitializationEvent */
    public void postInit(FMLPostInitializationEvent evt) {
        // server‐side final tweaks or compat hooks
    }

    /**
     * Forge GUI handler gets registered on both sides,
     * but this implementation only provides the server container.
     */
    public IGuiHandler getGuiHandler() {
        return new IGuiHandler() {
            @Override
            public Object getServerGuiElement(int id,
                                              net.minecraft.entity.player.EntityPlayer player,
                                              net.minecraft.world.World world,
                                              int x, int y, int z) {
                // return your Container here, or null if none
                return null;
            }
            @Override
            public Object getClientGuiElement(int id,
                                              net.minecraft.entity.player.EntityPlayer player,
                                              net.minecraft.world.World world,
                                              int x, int y, int z) {
                // server side should never call this
                return null;
            }
        };
    }
}