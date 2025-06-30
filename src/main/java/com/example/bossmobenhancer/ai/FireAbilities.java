package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.entity.effect.EntityLightningBolt;
import java.util.List;
import java.util.Random;

public class FireAbilities {
    private static final Random RAND = new Random();

    private static boolean isServer(EntityLiving mob) {
        return mob.world != null && !mob.world.isRemote;
    }

    public static void flameWhirlwind(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0 + tier;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.setFire(5);
            p.attackEntityFrom(DamageSource.IN_FIRE, 4.0F * tier);
        }
    }

    public static void emberBurst(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.setFire(4);
        }
    }

    public static void solarFlare(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 6.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.addPotionEffect(new PotionEffect(
                    MobEffects.BLINDNESS, 60 + tier * 10, 0
            ));
        }
    }

    public static void inferno(EntityLiving mob, int tier) {
        if (!isServer(mob)) return;
        double r = 5.0;
        AxisAlignedBB area = new AxisAlignedBB(
                mob.posX - r, mob.posY - 2, mob.posZ - r,
                mob.posX + r, mob.posY + 2, mob.posZ + r
        );
        for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
            p.setFire(6);
            p.attackEntityFrom(DamageSource.IN_FIRE, 5.0F * tier);
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
}
