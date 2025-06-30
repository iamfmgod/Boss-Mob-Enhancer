package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;

public class PhysicalAbilities {
    private static boolean isServer(EntityLiving mob) {
        return mob.world != null && !mob.world.isRemote;
    }

    public static void earthquake(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.GENERIC, 4.0F * tier);
            p.motionX += (p.posX - mob.posX) * 0.5;
            p.motionZ += (p.posZ - mob.posZ) * 0.5;
        }
    }

    public static void forceWave(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.motionX += (p.posX - mob.posX) * 0.7;
            p.motionZ += (p.posZ - mob.posZ) * 0.7;
        }
    }

    public static void ragingBlow(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int dur = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, dur, tier));
    }

    public static void furyRush(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (10 + tier * 2);
        mob.addPotionEffect(new PotionEffect(
                MobEffects.SPEED, d, 1
        ));
        mob.addPotionEffect(new PotionEffect(
                MobEffects.STRENGTH, d, 1
        ));
    }

    public static void shockwave(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.causeMobDamage(mob), 3.0F * tier);
            p.motionX += (p.posX - mob.posX) * 0.8;
            p.motionZ += (p.posZ - mob.posZ) * 0.8;
        }
    }

    public static void voidRift(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        mob.world.createExplosion(mob, mob.posX, mob.posY, mob.posZ, 2.0F + tier, false);
    }

    public static void gravityWell(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 7.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            double dx = mob.posX - p.posX;
            double dz = mob.posZ - p.posZ;
            p.motionX += dx * 0.05;
            p.motionZ += dz * 0.05;
        }
    }

    public static void arcaneMystery(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        java.util.Random RAND = new java.util.Random();
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            if (RAND.nextBoolean()) {
                p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0));
            } else {
                p.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100, 0));
            }
        }
    }

    public static void etherealBlade(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        int d = 20 * (8 + tier);
        mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, d, 1));
        double r = 4.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, d, 1));
        }
    }

    public static void piercingGale(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.causeMobDamage(mob), 2.0F * tier);
            p.motionX += (p.posX - mob.posX) * 0.6;
            p.motionZ += (p.posZ - mob.posZ) * 0.6;
        }
    }

    public static void silence(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 4.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.MINING_FATIGUE, 100 + tier * 10, 2
            ));
        }
    }

    public static void crowstorm(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 8.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 3, mob.posZ - r,
                mob.posX + r, mob.posY + 3, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.NAUSEA, 80 + tier * 10, 0
            ));
        }
    }

    public static void shadowVeil(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        mob.addPotionEffect(new PotionEffect(
                MobEffects.INVISIBILITY, 60 + tier * 10, 0
        ));
        mob.addPotionEffect(new PotionEffect(
                MobEffects.SPEED, 60 + tier * 10, 1
        ));
    }
}
