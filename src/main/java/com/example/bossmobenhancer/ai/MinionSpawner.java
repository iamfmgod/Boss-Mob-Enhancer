package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.entities.EntityBossMinion;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

/**
 * Spawns boss minions around a boss mob based on its tier and configuration.
 */
public class MinionSpawner {

    /**
     * Attempts to spawn minions around the given boss.
     *
     * @param mob  The boss entity.
     * @param tier The boss tier (used to scale spawn chance and count).
     */
    public static void trySpawnMinions(EntityLiving mob, int tier) {
        World world = mob.world;
        // Only on the server side
        if (world == null || world.isRemote) return;
        if (!ConfigHandler.enableMinions) return;

        // Base spawn chance (%) and extra per tier
        double baseChance  = ConfigHandler.baseMinionSpawnChance;       // e.g. 20.0
        double spawnChance = baseChance + tier * ConfigHandler.spawnChancePerTier;

        // Early exit if below tier threshold or roll fails
        if (tier < ConfigHandler.minionTierThreshold
                || world.rand.nextDouble() * 100.0 >= spawnChance) {
            return;
        }

        // Determine how many to spawn
        int rawCount   = tier - ConfigHandler.minionTierThreshold + 1;
        int spawnCount = MathHelper.clamp(rawCount, 1, ConfigHandler.maxMinionsPerSpawn);

        // Seeded RNG per‐boss so each boss behaves consistently
        UUID uuid = mob.getUniqueID();
        long seed = uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits() ^ tier;
        Random rand = new Random(seed);

        double radius = ConfigHandler.minionSpawnRadius;

        for (int i = 0; i < spawnCount; i++) {
            // Random offset in a circle of radius
            double dx = (rand.nextDouble() - 0.5) * 2 * radius;
            double dz = (rand.nextDouble() - 0.5) * 2 * radius;
            double dist = MathHelper.sqrt(dx * dx + dz * dz);
            if (dist > radius) {
                dx *= radius / dist;
                dz *= radius / dist;
            }

            double spawnX = mob.posX + dx;
            double spawnY = mob.posY;
            double spawnZ = mob.posZ + dz;

            EntityBossMinion minion = new EntityBossMinion(world);
            minion.setPosition(spawnX, spawnY, spawnZ);
            minion.setBossOwner(mob);
            minion.setTier(tier);
            world.spawnEntity(minion);
        }
    }
}