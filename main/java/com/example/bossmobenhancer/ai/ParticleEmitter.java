package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Random;

public class ParticleEmitter {
    private static final Random rand = new Random();

    /**
     * Emits a base burst of particles around the given mob.
     * If the mob’s custom name contains "Lord", also emits swirling orbit particles.
     *
     * @param mob  The boss mob entity.
     * @param tier The boss tier, which scales the number, spread, and velocity of the particles.
     */
    public static void emitParticles(EntityLiving mob, int tier) {
        World world = mob.world;
        if (world == null || !world.isRemote) return; // Only run on the client side

        // Expanded pool of particle types for the burst effect.
        EnumParticleTypes[] particlePool = {
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

        int count = Math.min(40, Math.max(10, tier * 10));
        double radius = 1.0 + 0.2 * tier;

        // Emit random burst particles.
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

        // If this mob's custom name contains "Lord", add orbiting elemental swirls.
        String customName = mob.getCustomNameTag();
        if (customName != null && customName.toLowerCase().contains("lord")) {
            emitElementalSwirl(mob, tier);
        }
    }

    /**
     * Emits swirling, orbiting particles around the mob.
     * Chooses one of several random elemental types and uses its associated particle effects.
     *
     * @param mob  The boss mob entity.
     * @param tier The boss tier, which scales the orbit radius.
     */
    public static void emitElementalSwirl(EntityLiving mob, int tier) {
        World world = mob.world;
        if (world == null || !world.isRemote) return;

        // Randomly pick an elemental type.
        String[] elementalTypes = {"Fire", "Ice", "Lightning", "Shadow", "Celestial"};
        String chosenElement = elementalTypes[rand.nextInt(elementalTypes.length)];

        // Assign swirl particle types according to the chosen element.
        EnumParticleTypes[] swirlParticles;
        switch (chosenElement) {
            case "Fire":
                swirlParticles = new EnumParticleTypes[]{EnumParticleTypes.FLAME, EnumParticleTypes.SMOKE_LARGE};
                break;
            case "Ice":
                swirlParticles = new EnumParticleTypes[]{EnumParticleTypes.SPELL_WITCH, EnumParticleTypes.CRIT_MAGIC};
                break;
            case "Lightning":
                swirlParticles = new EnumParticleTypes[]{EnumParticleTypes.CRIT, EnumParticleTypes.CRIT_MAGIC};
                break;
            case "Shadow":
                swirlParticles = new EnumParticleTypes[]{EnumParticleTypes.PORTAL, EnumParticleTypes.SMOKE_NORMAL};
                break;
            case "Celestial":
                swirlParticles = new EnumParticleTypes[]{EnumParticleTypes.DRAGON_BREATH, EnumParticleTypes.PORTAL};
                break;
            default:
                swirlParticles = new EnumParticleTypes[]{EnumParticleTypes.SPELL_MOB};
        }

        // Parameters for the orbiting effect.
        int swirlCount = 20;
        long time = world.getTotalWorldTime(); // Use world time for continuous rotation.
        double orbitRadius = 1.5 + 0.1 * tier; // Larger for higher tiers.
        double orbitSpeed = 0.1;              // Adjust this value to make the swirl rotate faster or slower.

        // Emit the orbiting particles.
        for (int i = 0; i < swirlCount; i++) {
            // Each particle is placed at an evenly-spaced angle, with a rotation offset based on world time.
            double angle = ((2 * Math.PI) / swirlCount) * i + (time * orbitSpeed);
            double offsetX = orbitRadius * Math.cos(angle);
            double offsetZ = orbitRadius * Math.sin(angle);
            double offsetY = 1.0; // Set a fixed height relative to the mob's position.

            // Randomly choose a particle from the elemental swirl array.
            EnumParticleTypes particleType = swirlParticles[rand.nextInt(swirlParticles.length)];

            world.spawnParticle(
                    particleType,
                    mob.posX + offsetX,
                    mob.posY + offsetY,
                    mob.posZ + offsetZ,
                    0, 0.05, 0
            );
        }
    }
}