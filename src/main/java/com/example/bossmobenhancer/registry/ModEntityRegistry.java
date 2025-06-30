// src/main/java/com/example/bossmobenhancer/registry/ModEntityRegistry.java
package com.example.bossmobenhancer.registry;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.entities.EntityBossMinion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class ModEntityRegistry {

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        // register the boss_minion entity
        event.getRegistry().register(
                EntityEntryBuilder.create()
                        .entity(EntityBossMinion.class)
                        .id(new ResourceLocation(MainMod.MODID, "boss_minion"), 0)
                        .name("boss_minion")
                        .tracker(64, 3, true)
                        .build()
        );
    }
}