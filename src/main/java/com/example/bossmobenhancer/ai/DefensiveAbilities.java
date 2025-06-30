package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class DefensiveAbilities {
    private static boolean isServer(EntityLiving mob) {
        return mob.world != null && !mob.world.isRemote;
    }

    public static void arcaneShield(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int dur = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, dur, tier));
    }

    public static void healthBoost(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double boost = 10.0 * tier;
        mob.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH)
                .setBaseValue(mob.getMaxHealth() + boost);
        mob.setHealth(mob.getMaxHealth());
    }

    public static void lunarBlessing(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (15 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, d, 1));
        mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, d, tier));
    }

    public static void steelFortress(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, tier));
        mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, d, tier));
    }

    public static void divineAegis(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 3);
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, 2));
        mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, d, 1));
    }

    public static void stoneform(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (8 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, 3));
        mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, d, 1));
    }

    public static void bulwark(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (12 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, d, 3));
    }

    public static void guardiansGrace(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, d, 2));
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, 1));
    }

    public static void ironSkin(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, 2));
    }

    public static void magicWard(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, d, 0));
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, 1));
    }

    public static void fortitude(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, d, 2));
        mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, d, 1));
    }

    public static void lastStand(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (8 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, 4));
        mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, d, 2));
    }

    public static void sanctuary(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, d, 2));
        mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, d, 2));
    }

    public static void reflectiveBarrier(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, d, 1));
        mob.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, d, 0));
    }
}
