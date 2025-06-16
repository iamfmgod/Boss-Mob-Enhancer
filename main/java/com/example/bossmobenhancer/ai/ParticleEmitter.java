package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Random;

public class ParticleEmitter {
    private static final Random rand = new Random();

    public static void emitParticles(EntityLiving mob, int tier) {
        World world = mob.world;
        if (world == null || !world.isRemote) return; // Only run on the client

        EnumParticleTypes[] particlePool = {
                EnumParticleTypes.SPELL_MOB,
                EnumParticleTypes.CRIT_MAGIC,
                EnumParticleTypes.FLAME,
                EnumParticleTypes.SPELL_WITCH,
                EnumParticleTypes.SMOKE_LARGE,
                EnumParticleTypes.PORTAL,
                EnumParticleTypes.DRAGON_BREATH
        };

        int count = Math.min(40, Math.max(10, tier * 10));
        double radius = 1.0 + 0.2 * tier;

        for (int i = 0; i < count; i++) {
            double angle = rand.nextDouble() * 2 * Math.PI;
            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);
            double yOffset = rand.nextDouble();

            world.spawnParticle(
                    particlePool[i % particlePool.length],
                    mob.posX + xOffset,
                    mob.posY + yOffset + 0.5,
                    mob.posZ + zOffset,
                    0, 0.01, 0
            );
        }
    }
}