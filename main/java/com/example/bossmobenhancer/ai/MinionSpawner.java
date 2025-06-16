package com.example.bossmobenhancer.ai;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import com.example.bossmobenhancer.config.ConfigHandler;

public class MinionSpawner {
    private static final Random rand = new Random();

    public static void trySpawnMinions(EntityLiving mob, int tier) {
        if (!ConfigHandler.enableMinions) return;

        if (rand.nextInt(10) < 2 && tier >= 3) {
            // summon logic goes here...
            // e.g., mob.world.spawnEntity(new YourMinionEntity(...));
        }
    }
}