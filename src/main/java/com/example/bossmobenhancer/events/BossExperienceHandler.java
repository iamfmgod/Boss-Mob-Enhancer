package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BossExperienceHandler {
    // If you want these in config, move them into ConfigHandler and swap the references below.
    private static final int BASE_BOSS_XP_MULTIPLIER = 5;
    private static final int LORD_BOSS_XP_MULTIPLIER = 20;

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null) return;

        String customName = entity.getCustomNameTag();
        if (customName == null || customName.trim().isEmpty()) return;

        // Strip color codes before parsing
        String cleanName = TextFormatting.getTextWithoutFormattingCodes(customName);
        int xp = event.getDroppedExperience();

        // Lord‐tier bosses named "Lord …"
        if (cleanName.startsWith("Lord ")) {
            xp *= LORD_BOSS_XP_MULTIPLIER;
        }
        // Other bosses from your NameGenerator
        else if (NameGenerator.isBossName(cleanName)) {
            int tier = extractTier(cleanName);
            xp *= BASE_BOSS_XP_MULTIPLIER * tier;
        }

        event.setDroppedExperience(xp);
    }

    /**
     * Parses "[Tier X]" from the name; returns X or 1 if not found/invalid.
     */
    private static int extractTier(String name) {
        int defaultTier = 1;
        int start = name.indexOf("[Tier ");
        if (start < 0) return defaultTier;
        int end = name.indexOf(']', start);
        if (end < 0) return defaultTier;

        try {
            return Integer.parseInt(name.substring(start + 6, end).trim());
        } catch (NumberFormatException ex) {
            return defaultTier;
        }
    }
}