package com.example.bossmobenhancer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry for bosses currently active in the client world.
 * Tracks boss entities so overlays can render health bars, names, etc.
 */
@net.minecraftforge.fml.relauncher.SideOnly(net.minecraftforge.fml.relauncher.Side.CLIENT)
public final class BossRegistry {
    // Thread-safe list so we can add/remove while iterating in render loops
    private static final List<EntityLiving> activeBossMobs = new CopyOnWriteArrayList<>();

    private BossRegistry() { /* no instantiation */ }

    /**
     * Call this whenever a mob becomes a boss.
     */
    public static void addBossMob(EntityLiving boss) {
        if (boss != null && !activeBossMobs.contains(boss)) {
            activeBossMobs.add(boss);
        }
    }

    /**
     * Remove the mob when it is no longer valid (killed, unloaded, or lost boss status).
     */
    public static void removeBossMob(EntityLiving boss) {
        activeBossMobs.remove(boss);
    }

    /**
     * Returns true if there's at least one valid boss in the current client world.
     */
    public static boolean hasActiveBoss() {
        return getCurrentBoss() != null;
    }

    /**
     * Get the display name of the current boss, or an empty string if none.
     */
    public static String getBossName() {
        EntityLiving boss = getCurrentBoss();
        return boss != null ? boss.getDisplayName().getFormattedText() : "";
    }

    /**
     * Get the current boss's health percentage (0.0–1.0), or 0 if none.
     */
    public static float getBossHealthPercent() {
        EntityLiving boss = getCurrentBoss();
        if (boss == null) return 0f;
        float max = boss.getMaxHealth();
        return max > 0 ? boss.getHealth() / max : 0f;
    }

    // --- Internals ---

    /**
     * Purges dead or out‐of‐world bosses, and returns the first valid boss.
     */
    private static EntityLiving getCurrentBoss() {
        World world = Minecraft.getMinecraft().world;
        if (world == null) return null;

        Iterator<EntityLiving> it = activeBossMobs.iterator();
        while (it.hasNext()) {
            EntityLiving mob = it.next();
            if (mob == null || mob.isDead || mob.world != world) {
                it.remove();
                continue;
            }
            return mob;
        }
        return null;
    }
}