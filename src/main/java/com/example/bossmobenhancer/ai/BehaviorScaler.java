package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import com.example.bossmobenhancer.data.ScalingProfileLoader.Profile;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Determines boss tier based on health, player count, and configured scaling profile.
 */
public class BehaviorScaler {

    /**
     * Calculates which tier (1…maxTier) a boss mob should be, given its current health
     * and the number of players in the world.
     *
     * @param mob   The boss entity whose tier we're computing.
     * @param world The world the boss is in (used to count players).
     * @return An integer tier between 1 and profile.maxTier.
     */
    public static int calculateTier(EntityLiving mob, World world) {
        // If scaling is globally disabled, always return tier 1.
        if (!ConfigHandler.enableScaling) {
            return 1;
        }

        // Lookup the active profile (fall back to "Balanced" if missing).
        Profile profile = ScalingProfileLoader.get(ConfigHandler.difficultyPreset);
        if (profile == null) {
            profile = ScalingProfileLoader.get("Balanced");
            if (profile == null) {
                // Last resort: no profiles available—just return baseline 1.
                return 1;
            }
        }

        double score = 1.0;

        // 1) Health-based scaling: weaker = higher bonus.
        double maxHealth = mob.getMaxHealth();
        if (maxHealth > 0) {
            double healthRatio = mob.getHealth() / maxHealth;
            score += profile.healthWeight * (1.0 - healthRatio);
        }

        // 2) Player-count scaling: more players → more difficulty.
        int playerCount = world.playerEntities.size();
        score += profile.playerCountWeight * ((double) playerCount / 2.0);

        // Round up, clamp to [1, maxTier]
        int tier = (int) Math.ceil(score);
        tier = MathHelper.clamp(tier, 1, profile.maxTier);

        return tier;
    }
}