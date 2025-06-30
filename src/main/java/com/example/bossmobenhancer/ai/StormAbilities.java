package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.List;
import java.util.Random;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;

public class StormAbilities {
    private static final Random RAND = new Random();

    // All abilities should check for server-side execution
    private static boolean isServer(EntityLiving mob) {
        return mob.world != null && !mob.world.isRemote;
    }

    public static void lightningStrike(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 8.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 3, mob.posZ - r,
                mob.posX + r, mob.posY + 3, mob.posZ + r
        );
        List<EntityPlayer> players = mob.world.getEntitiesWithinAABB(EntityPlayer.class, area);
        if (!players.isEmpty() && RAND.nextFloat() < 0.5f) {
            EntityPlayer target = players.get(RAND.nextInt(players.size()));
            mob.world.addWeatherEffect(new EntityLightningBolt(
                    mob.world, target.posX, target.posY, target.posZ, false
            ));
        }
    }

    public static void stormCall(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 8.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 3, mob.posZ - r,
                mob.posX + r, mob.posY + 3, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            mob.world.addWeatherEffect(new EntityLightningBolt(
                    mob.world, p.posX, p.posY, p.posZ, false
            ));
        }
    }

    public static void solarBeam(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 7.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 3, mob.posZ - r,
                mob.posX + r, mob.posY + 3, mob.posZ + r
        );
        List<EntityPlayer> ps = mob.world.getEntitiesWithinAABB(EntityPlayer.class, area);
        if (!ps.isEmpty()) {
            EntityPlayer t = ps.get(RAND.nextInt(ps.size()));
            mob.world.addWeatherEffect(new EntityLightningBolt(
                    mob.world, t.posX, t.posY, t.posZ, false
            ));
        }
    }

    public static void thunderClap(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0 + tier;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 4.0F * tier);
        }
    }

    public static void staticField(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 7.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80 + tier * 10, 1));
        }
    }

    public static void chainLightning(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 8.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 3, mob.posZ - r,
                mob.posX + r, mob.posY + 3, mob.posZ + r
        );
        List<EntityPlayer> players = mob.world.getEntitiesWithinAABB(EntityPlayer.class, area);
        int hits = Math.min(players.size(), 2 + tier);
        for (int i = 0; i < hits; i++) {
            EntityPlayer target = players.get(RAND.nextInt(players.size()));
            mob.world.addWeatherEffect(new EntityLightningBolt(
                    mob.world, target.posX, target.posY, target.posZ, false
            ));
            target.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 3.0F * tier);
        }
    }

    public static void windBurst(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 7.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.motionX += (p.posX - mob.posX) * 0.8;
            p.motionZ += (p.posZ - mob.posZ) * 0.8;
        }
    }

    public static void eyeOfTheStorm(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100 + tier * 20, 2));
        mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100 + tier * 10, 1));
    }

    public static void tempestShield(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 120 + tier * 20, 2));
    }

    public static void downpour(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 8.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 3, mob.posZ - r,
                mob.posX + r, mob.posY + 3, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60 + tier * 10, 0));
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60 + tier * 10, 1));
        }
    }

    public static void galeForce(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 7.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.motionX += (p.posX - mob.posX) * 1.2;
            p.motionZ += (p.posZ - mob.posZ) * 1.2;
        }
    }
}
