package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;

import java.util.Random;

public class AttributeSyncer {
    private static final Random rand = new Random();

    public static void syncStats(EntityLiving mob, int tier) {
        double baseHealth = 30 + tier * 10 + rand.nextInt(10);
        double speed = 0.25 + rand.nextDouble() * 0.05;

        mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(baseHealth);
        mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
        mob.setHealth(mob.getMaxHealth());
    }
}