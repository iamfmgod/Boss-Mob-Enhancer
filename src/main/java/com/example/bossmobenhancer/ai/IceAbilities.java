package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;

public class IceAbilities {
    private static boolean isServer(EntityLiving mob) {
        return mob.world != null && !mob.world.isRemote;
    }

    public static void frostNova(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.SLOWNESS, 100 + tier * 10, 2
            ));
            p.addPotionEffect(new PotionEffect(
                    MobEffects.MINING_FATIGUE, 100 + tier * 10, 1
            ));
        }
    }

    public static void chillingTouch(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.SLOWNESS, 120 + tier * 10, 2
            ));
            p.addPotionEffect(new PotionEffect(
                    MobEffects.MINING_FATIGUE, 120 + tier * 10, 1
            ));
        }
    }

    public static void glacialPrison(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 4.0 + tier;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200 + tier * 20, 4));
            p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100 + tier * 10, 1));
        }
    }

    public static void hailstorm(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0 + tier;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 3, mob.posZ - r,
                mob.posX + r, mob.posY + 3, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.GENERIC, 2.0F * tier);
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60 + tier * 10, 1));
        }
    }

    public static void permafrost(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0 + tier;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200 + tier * 20, 3));
        }
    }

    public static void iceSpikes(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 4.0 + tier;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.GENERIC, 3.0F * tier);
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40 + tier * 10, 2));
        }
    }

    public static void frozenArmor(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200 + tier * 20, 1));
        mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200 + tier * 10, 0));
    }

    public static void absoluteZero(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0 + tier;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 300, 5));
            p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 2));
        }
    }

    public static void shatter(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.GENERIC, 6.0F * tier);
        }
    }

    public static void snowblind(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100 + tier * 10, 0));
        }
    }

    public static void wintersGrasp(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120 + tier * 10, 3));
        }
    }

    public static void frostbite(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 4.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100 + tier * 10, 1));
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100 + tier * 10, 2));
        }
    }
}
