package com.example.bossmobenhancer.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BossExperienceHandler {

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null) {
            return;
        }
        String customName = entity.getCustomNameTag();
        // Check if the mob is a Lord-tier enemy using your naming convention ("§6Lord ")
        if (customName != null && customName.startsWith("§6Lord ")) {
            int originalXp = event.getDroppedExperience();
            // Multiply the XP by 20x.
            event.setDroppedExperience(originalXp * 20);
        }
    }
}