package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

public class UtilityAbilities {
    private static boolean isServer(EntityLiving mob) {
        return mob.world != null && !mob.world.isRemote;
    }

    public static void blinkStep(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double range = 4.0;
        double newX = mob.posX + (mob.world.rand.nextDouble() * range - range / 2);
        double newZ = mob.posZ + (mob.world.rand.nextDouble() * range - range / 2);
        mob.setPositionAndUpdate(newX, mob.posY, newZ);
        mob.addPotionEffect(new PotionEffect(
                MobEffects.INVISIBILITY, 40 + 10 * tier, 0
        ));
    }

    public static void webSpawn(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 3.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 1, mob.posZ - r,
                mob.posX + r, mob.posY + 1, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 2));
        }
    }

    public static void mirrorImage(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.NAUSEA, 80 + tier * 10, 0
            ));
        }
    }

    public static void phantomDash(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double range = 5.0;
        double x = mob.posX + (mob.world.rand.nextDouble() * range - range / 2);
        double z = mob.posZ + (mob.world.rand.nextDouble() * range - range / 2);
        mob.setPositionAndUpdate(x, mob.posY, z);
    }

    public static void timeDilation(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 7.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.SLOWNESS, 100 + tier * 10, 2
            ));
        }
    }
}
