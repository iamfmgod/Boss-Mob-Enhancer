package com.example.bossmobenhancer.registry;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.items.ItemLunarBlessedApple;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class ItemRegistry {
    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> ev) {
        ev.getRegistry().register(
                new ItemLunarBlessedApple()
                        .setRegistryName(MainMod.MODID, "lunar_blessed_apple")
        );
    }
}