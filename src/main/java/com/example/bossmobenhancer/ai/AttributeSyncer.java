package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.UUID;

/**
 * Utility to sync and clamp boss stats consistently.
 * Health and speed are derived from tier plus a pseudo‐random offset
 * seeded by the boss's UUID so stats remain stable across restarts.
 */
public class AttributeSyncer {
    private static final Logger LOGGER      = LogManager.getLogger(MainMod.MODID);
    /** Minimum and maximum allowable movement speeds. */
    private static final double MIN_SPEED  = 0.20;
    private static final double MAX_SPEED  = 0.35;

    /**
     * Adjusts the mob's Max Health and Movement Speed attributes based on its tier.
     * Stats are seeded by the mob's UUID (xor'ed with tier) for consistency.
     *
     * @param mob  The boss entity whose stats will be adjusted.
     * @param tier The boss tier (1,2,3,4+), scales base values and random variance.
     */
    public static void syncStats(EntityLiving mob, int tier) {
        if (mob == null) {
            LOGGER.warn("AttributeSyncer.syncStats called with null mob, tier={}", tier);
            return;
        }

        // Seed RNG with mob's UUID + tier so each boss has stable but varied stats.
        UUID uuid = mob.getUniqueID();
        long seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits() ^ tier;
        Random random = new Random(seed);

        // Base health: 30 + 10*tier plus up to +9 random variance
        double baseHealth = 30 + tier * 10 + random.nextInt(10);

        // Movement speed: base 0.25 plus up to +0.05 variance, then clamped
        double speed = 0.25 + random.nextDouble() * 0.05;
        speed = MathHelper.clamp(speed, MIN_SPEED, MAX_SPEED);

        // --- Apply MAX_HEALTH safely ---
        IAttributeInstance hpAttr = mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.setBaseValue(baseHealth);
        } else {
            LOGGER.warn("Missing MAX_HEALTH attribute on {}, skipping health sync", mob);
        }

        // --- Apply MOVEMENT_SPEED safely ---
        IAttributeInstance spdAttr = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (spdAttr != null) {
            spdAttr.setBaseValue(speed);
        } else {
            LOGGER.warn("Missing MOVEMENT_SPEED attribute on {}, skipping speed sync", mob);
        }

        // Heal up to the (possibly) new max health
        float newMax = mob.getMaxHealth();
        mob.setHealth(MathHelper.clamp(mob.getHealth(), 1.0F, newMax));
    }
}