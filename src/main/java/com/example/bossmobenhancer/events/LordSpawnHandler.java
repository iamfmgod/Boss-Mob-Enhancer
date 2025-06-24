package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber
public class LordSpawnHandler {

    @SubscribeEvent
    public static void onSpecialSpawn(SpecialSpawn event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityLiving)) {
            return;
        }
        EntityLiving mob = (EntityLiving) entity;
        World world = mob.world;
        Random rand = world.rand;
        int dim = world.provider.getDimension();

        // Skip if this mob has already been modified into a boss.
        if (mob.getCustomNameTag() != null && !mob.getCustomNameTag().isEmpty()) {
            return;
        }

        // Determine multipliers based on dimension.
        double spawnChance;
        int bossTier;
        double hpScale;
        double atkScale;
        double spdScale;
        String dimensionPrefix;

        if (dim == 0) { // Overworld
            spawnChance = 0.10;
            bossTier = 1;
            hpScale = 2.0;
            atkScale = 1.3;
            spdScale = 1.15;
            dimensionPrefix = "Overworld";
        } else if (dim == -1) { // Nether
            spawnChance = 0.20;
            bossTier = 2;
            hpScale = 3.0;
            atkScale = 1.6;
            spdScale = 1.2;
            dimensionPrefix = "Nether";
        } else if (dim == 1) { // The End
            spawnChance = 0.35;
            bossTier = 3;
            hpScale = 4.5;
            atkScale = 2.0;
            spdScale = 1.25;
            dimensionPrefix = "End";
        } else {
            spawnChance = 0.08;
            bossTier = 1;
            hpScale = 2.0;
            atkScale = 1.2;
            spdScale = 1.1;
            dimensionPrefix = "Unknown";
        }

        if (rand.nextDouble() < spawnChance) {
            String bossName = "§5Lord " + dimensionPrefix + " " + NameGenerator.generateFantasyName(mob, bossTier);
            mob.setCustomNameTag(bossName);
            mob.setAlwaysRenderNameTag(true);
            // Removed the glowing outline:
            // mob.setGlowing(true);

            // Increase health drastically.
            net.minecraft.entity.ai.attributes.IAttributeInstance healthAttr = mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            double newHealth = healthAttr.getBaseValue() * hpScale;
            healthAttr.setBaseValue(newHealth);
            mob.setHealth((float) newHealth);

            // Boost attack damage if applicable.
            net.minecraft.entity.ai.attributes.IAttributeInstance attackAttr = mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (attackAttr != null) {
                attackAttr.setBaseValue(attackAttr.getBaseValue() * atkScale);
            }

            // Increase movement speed if applicable.
            net.minecraft.entity.ai.attributes.IAttributeInstance speedAttr = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.setBaseValue(speedAttr.getBaseValue() * spdScale);
            }

            // Apply temporary buffs to help the boss during initial combat.
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 10 * 20, 1));
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 10 * 20, 1));
            mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 10 * 20, 1));

            // Tag the boss for potential phase-based behavior.
            mob.getEntityData().setBoolean("isLordBoss", true);
            mob.getEntityData().setInteger("bossTier", bossTier);
            mob.getEntityData().setInteger("bossPhase", 1);

            // Play an epic boss roar.
            world.playSound(null, mob.getPosition(), SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 2.0F, 0.8F);

            // Spawn a lightning bolt for dramatic effect.
            if (!world.isRemote && world instanceof WorldServer) {
                EntityLightningBolt lightning = new EntityLightningBolt(world, mob.posX, mob.posY, mob.posZ, false);
                world.spawnEntity(lightning);
            }
        }
    }
}