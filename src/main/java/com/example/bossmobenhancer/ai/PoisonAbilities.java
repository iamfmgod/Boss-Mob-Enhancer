package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;

public class PoisonAbilities {
    private static boolean isServer(EntityLiving mob) {
        return mob.world != null && !mob.world.isRemote;
    }

    public static void witherAura(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 4.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.WITHER, 100 + tier * 20, 1
            ));
        }
    }

    public static void poisonCloud(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.POISON, 100 + tier * 10, 1
            ));
        }
    }

    public static void venomousCloud(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.POISON, 120 + tier * 10, 1
            ));
            p.addPotionEffect(new PotionEffect(
                    MobEffects.SLOWNESS, 120 + tier * 10, 2
            ));
        }
    }

    public static void naturesWrath(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.5;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.POISON, 100 + tier * 10, 1));
        }
    }

    public static void toxicSpit(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.POISON, 80 + tier * 10, 2));
        }
    }

    public static void plagueAura(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 7.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.POISON, 100 + tier * 10, 1));
            p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100 + tier * 10, 1));
        }
    }

    public static void blight(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60 + tier * 10, 2));
        }
    }

    public static void necroticTouch(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 4.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100 + tier * 10, 1));
            p.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100 + tier * 10, 1));
        }
    }

    public static void pestilence(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.POISON, 120 + tier * 10, 2));
            p.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 60 + tier * 10, 0));
        }
    }

    public static void corrosiveSpray(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.attackEntityFrom(DamageSource.MAGIC, 2.0F * tier);
            p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80 + tier * 10, 1));
        }
    }

    public static void rot(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 4.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 120 + tier * 10, 1));
        }
    }

    public static void miasma(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.POISON, 100 + tier * 10, 1));
            p.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60 + tier * 10, 0));
        }
    }
}
