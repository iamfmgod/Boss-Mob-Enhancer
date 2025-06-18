package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber
public class LordSpawnHandler {

    @SubscribeEvent
    public static void onSpecialSpawn(SpecialSpawn event) {
        // Get the entity as EntityLivingBase.
        EntityLivingBase entity = event.getEntityLiving();
        // Only continue if the entity is indeed an instance of EntityLiving.
        if (!(entity instanceof EntityLiving)) {
            return;
        }
        EntityLiving mob = (EntityLiving) entity;

        World world = event.getWorld();
        int dim = world.provider.getDimension();
        Random rand = world.rand;

        // Only transform mobs that haven't already been promoted.
        if (mob.getCustomNameTag() != null && !mob.getCustomNameTag().isEmpty()) {
            return;
        }

        // Configure chance, boss tier, and health multiplier based on dimension.
        double spawnChance;
        int bossTier;
        double healthMultiplier;
        String dimensionPrefix;

        if (dim == 0) { // Overworld
            spawnChance = 0.10;  // 10% chance in the Overworld.
            bossTier = 1;
            healthMultiplier = 1.5;
            dimensionPrefix = "Overworld";
        } else if (dim == -1) { // Nether
            spawnChance = 0.25;  // 25% chance in the Nether.
            bossTier = 2;
            healthMultiplier = 2.5;
            dimensionPrefix = "Nether";
        } else if (dim == 1) { // The End
            spawnChance = 0.35;  // 35% chance in The End.
            bossTier = 3;
            healthMultiplier = 3.5;
            dimensionPrefix = "End";
        } else {
            // Fallback for other dimensions.
            spawnChance = 0.10;
            bossTier = 1;
            healthMultiplier = 1.5;
            dimensionPrefix = "Unknown";
        }

        // Check the chance and if it passes, transform the mob into a Lord-class boss.
        if (rand.nextDouble() < spawnChance) {
            String bossName = "§6Lord " + dimensionPrefix + " " + NameGenerator.generateFantasyName(mob, bossTier);
            mob.setCustomNameTag(bossName);
            mob.setAlwaysRenderNameTag(true);

            // Increase the mob's maximum health.
            mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                    .setBaseValue(mob.getMaxHealth() * healthMultiplier);

            // Heal the mob to full health.
            mob.setHealth(mob.getMaxHealth());
        }
    }
}