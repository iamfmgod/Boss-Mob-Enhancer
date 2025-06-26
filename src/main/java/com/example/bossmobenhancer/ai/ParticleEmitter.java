package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * Client-only helper for emitting burst and swirl particles around boss mobs.
 */
@SideOnly(Side.CLIENT)
public class ParticleEmitter {
    private static final Random RAND = new Random();

    /**
     * Emits a burst of particles around the mob. If its custom name contains "lord",
     * also triggers an elemental swirl.
     *
     * @param mob  The boss entity.
     * @param tier The boss tier (controls count and spread).
     */
    public static void emitParticles(EntityLiving mob, int tier) {
        World world = mob.world;
        if (world == null || !world.isRemote) return;

        // Pool of burst particle types
        EnumParticleTypes[] pool = {
                EnumParticleTypes.SPELL_MOB,
                EnumParticleTypes.CRIT_MAGIC,
                EnumParticleTypes.FLAME,
                EnumParticleTypes.SPELL_WITCH,
                EnumParticleTypes.SMOKE_LARGE,
                EnumParticleTypes.PORTAL,
                EnumParticleTypes.DRAGON_BREATH,
                EnumParticleTypes.CRIT,
                EnumParticleTypes.SMOKE_NORMAL
        };

        // Count: tier*10, clamped between 10 and 40
        int count = MathHelper.clamp(tier * 10, 10, 40);
        double radius = 1.0 + 0.2 * tier;

        for (int i = 0; i < count; i++) {
            double angle = RAND.nextDouble() * Math.PI * 2;
            double xOff  = radius * Math.cos(angle);
            double zOff  = radius * Math.sin(angle);
            double yOff  = RAND.nextDouble();

            world.spawnParticle(
                    pool[i % pool.length],
                    mob.posX + xOff,
                    mob.posY + yOff + 0.5,
                    mob.posZ + zOff,
                    0, 0.01, 0
            );
        }

        String name = mob.getCustomNameTag();
        if (name != null && name.toLowerCase().contains("lord")) {
            emitElementalSwirl(mob, tier);
        }
    }

    /**
     * Emits a rotating ring of elemental particles around the mob.
     *
     * @param mob  The boss entity.
     * @param tier The boss tier (controls swirl radius).
     */
    private static void emitElementalSwirl(EntityLiving mob, int tier) {
        World world = mob.world;
        if (world == null || !world.isRemote) return;

        String[] elements = {"Fire","Ice","Lightning","Shadow","Celestial"};
        String chosen     = elements[RAND.nextInt(elements.length)];

        EnumParticleTypes[] swirl;
        switch (chosen) {
            case "Fire":
                swirl = new EnumParticleTypes[]{ EnumParticleTypes.FLAME, EnumParticleTypes.SMOKE_LARGE };
                break;
            case "Ice":
                swirl = new EnumParticleTypes[]{ EnumParticleTypes.SPELL_WITCH, EnumParticleTypes.CRIT_MAGIC };
                break;
            case "Lightning":
                swirl = new EnumParticleTypes[]{ EnumParticleTypes.CRIT, EnumParticleTypes.CRIT_MAGIC };
                break;
            case "Shadow":
                swirl = new EnumParticleTypes[]{ EnumParticleTypes.PORTAL, EnumParticleTypes.SMOKE_NORMAL };
                break;
            case "Celestial":
                swirl = new EnumParticleTypes[]{ EnumParticleTypes.DRAGON_BREATH, EnumParticleTypes.PORTAL };
                break;
            default:
                swirl = new EnumParticleTypes[]{ EnumParticleTypes.SPELL_MOB };
        }

        int swirlCount    = 20;
        double orbitRadius = 1.5 + 0.1 * tier;
        double speed      = 0.05;
        long time         = world.getTotalWorldTime();

        for (int i = 0; i < swirlCount; i++) {
            double angle = (Math.PI * 2 / swirlCount) * i + time * 0.1;
            double xOff  = orbitRadius * Math.cos(angle);
            double zOff  = orbitRadius * Math.sin(angle);

            world.spawnParticle(
                    swirl[RAND.nextInt(swirl.length)],
                    mob.posX + xOff,
                    mob.posY + 1.0,
                    mob.posZ + zOff,
                    0, speed, 0
            );
        }
    }
}