package com.example.bossmobenhancer.ai;

import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.entities.EntityBossMinion;  // Use your custom minion entity

public class MinionSpawner {
    private static final Random rand = new Random();

    public static void trySpawnMinions(EntityLiving mob, int tier) {
        World world = mob.world;
        if (world == null || world.isRemote) return;

        if (!ConfigHandler.enableMinions) return;

        int baseChance = ConfigHandler.baseMinionSpawnChance; // e.g., 20%
        int spawnChance = baseChance + (tier * 5);              // Increase chance with tier

        if (tier < 3 || rand.nextInt(100) >= spawnChance) {
            return;
        }

        // Determine the number of minions to spawn.
        int spawnCount = Math.min(ConfigHandler.maxMinionsPerSpawn, tier - 2);

        for (int i = 0; i < spawnCount; i++) {
            double radius = 2.0 + tier * 0.5;
            double offsetX = (rand.nextDouble() - 0.5) * radius;
            double offsetZ = (rand.nextDouble() - 0.5) * radius;
            double spawnY = mob.posY;

            EntityBossMinion minion = new EntityBossMinion(world);
            minion.setPosition(mob.posX + offsetX, spawnY, mob.posZ + offsetZ);
            minion.setBossOwner(mob);
            minion.setTier(tier);
            world.spawnEntity(minion);
        }
    }
}