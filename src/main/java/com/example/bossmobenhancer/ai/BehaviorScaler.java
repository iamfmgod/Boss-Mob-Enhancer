package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import com.example.bossmobenhancer.data.ScalingProfileLoader.Profile;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class BehaviorScaler {

    public static int calculateTier(EntityLiving mob, World world) {
        if (!ConfigHandler.enableScaling) return 1;

        Profile profile = ScalingProfileLoader.get(ConfigHandler.difficultyPreset);
        double score = 1.0;

        // Health-based scaling: the weaker the mob, the higher the bonus
        double healthRatio = mob.getHealth() / mob.getMaxHealth();
        score += profile.healthWeight * (1.0 - healthRatio);

        // Player count scaling
        int playerCount = world.playerEntities.size();
        score += profile.playerCountWeight * (playerCount / 2.0);

        return Math.min((int) Math.ceil(score), profile.maxTier);
    }
}