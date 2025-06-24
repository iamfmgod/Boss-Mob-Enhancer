package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BossExperienceHandler {

    // Base XP multipliers can eventually be moved to your config.
    private static final int BASE_BOSS_XP_MULTIPLIER = 5;    // For non-lord bosses, multiplied by tier.
    private static final int LORD_BOSS_XP_MULTIPLIER = 20;     // For Lord-tier bosses.

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null) {
            return;
        }

        String customName = entity.getCustomNameTag();
        if (customName == null || customName.isEmpty()) {
            return;
        }

        int xp = event.getDroppedExperience();

        // Check if the mob is a Lord-tier boss.
        if (customName.startsWith("§6Lord ")) {
            xp *= LORD_BOSS_XP_MULTIPLIER;
        }
        // For other boss mobs (as determined by the name generator) apply a tier-scaled multiplier.
        else if (NameGenerator.isBossName(customName)) {
            int tier = extractTierFromName(customName);
            xp *= BASE_BOSS_XP_MULTIPLIER * tier;
        }

        event.setDroppedExperience(xp);
    }

    /**
     * Extracts the boss tier from the display name.
     * Expected format: "... [Tier X]" where X is an integer.
     * If no valid tier is found, defaults to 1.
     *
     * @param name The boss's custom name.
     * @return The parsed tier, or 1 if not found.
     */
    private static int extractTierFromName(String name) {
        int tier = 1;
        int start = name.indexOf("[Tier ");
        if (start != -1) {
            int end = name.indexOf("]", start);
            if (end != -1) {
                String tierStr = name.substring(start + 6, end).trim();
                try {
                    tier = Integer.parseInt(tierStr);
                } catch (NumberFormatException e) {
                    // Default to tier 1 if parsing fails.
                    tier = 1;
                }
            }
        }
        return tier;
    }
}