package com.example.bossmobenhancer.client;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BossRegistry {
    // A list to hold active boss mobs.
    private static final List<EntityLiving> activeBossMobs = new ArrayList<>();

    // Call this whenever a mob becomes a boss.
    public static void addBossMob(EntityLiving boss) {
        if (!activeBossMobs.contains(boss)) {
            activeBossMobs.add(boss);
        }
    }

    // Remove the mob when it is no longer valid (for instance, when killed or unloaded).
    public static void removeBossMob(EntityLiving boss) {
        activeBossMobs.remove(boss);
    }

    // Get a valid list of boss mobs from the current world.
    public static List<EntityLiving> getActiveBossMobs(World world) {
        // Clear any boss that is no longer in the world.
        Iterator<EntityLiving> it = activeBossMobs.iterator();
        while (it.hasNext()) {
            EntityLiving mob = it.next();
            if (mob.isDead || mob.world != world) {
                it.remove();
            }
        }
        return activeBossMobs;
    }
}