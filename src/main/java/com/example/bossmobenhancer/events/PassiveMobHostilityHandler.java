package com.example.bossmobenhancer.events;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;  // Updated import
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class PassiveMobHostilityHandler {

    // Key for marking a mob as converted to hostile.
    private static final String CONVERTED_FLAG = "convertedToHostile";

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        try {
            EntityLivingBase entity = event.getEntityLiving();
            if (entity == null || entity.world.isRemote) {
                return;
            }

            // Process only passive mobs (animals and villagers).
            if (!(entity instanceof EntityAnimal || entity instanceof EntityVillager)) {
                return;
            }

            // Cast to EntityLiving.
            EntityLiving mob = (EntityLiving) entity;

            // Process only mobs that have been given a custom name.
            String customName = mob.getCustomNameTag();
            if (customName == null || customName.trim().isEmpty()) {
                return;
            }

            // Skip if already converted.
            if (mob.getEntityData().getBoolean(CONVERTED_FLAG)) {
                return;
            }

            // Ensure there is a valid attacker.
            DamageSource source = event.getSource();
            EntityLivingBase attacker = null;
            if (source.getTrueSource() instanceof EntityLivingBase) {
                attacker = (EntityLivingBase) source.getTrueSource();
            }
            if (attacker == null) {
                return;
            }

            System.out.println("PassiveMobHostilityHandler: Converting mob " + mob.getName() +
                    " with custom name '" + customName + "' to hostile due to being attacked.");

            addHostileAI(mob, attacker);
            customizeHostileBehavior(mob);
            // Mark this mob as converted so that it won't be re-processed.
            mob.getEntityData().setBoolean(CONVERTED_FLAG, true);
        } catch (Exception e) {
            System.err.println("Error during conversion in onLivingHurt:");
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the mob already has a hostile targeting AI task.
     */
    private static boolean alreadyHostile(EntityLiving mob) {
        for (EntityAITasks.EntityAITaskEntry entry : mob.targetTasks.taskEntries) {
            if (entry.action instanceof EntityAINearestAttackableTarget) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds hostile AI tasks so that the mob will target the attacker and perform melee attacks.
     * Mimics wolf-like behavior by using a melee attack speed of 1.25D.
     */
    private static void addHostileAI(EntityLiving mob, EntityLivingBase attacker) {
        if (mob instanceof EntityCreature) {
            EntityCreature creature = (EntityCreature) mob;
            creature.setAttackTarget(attacker);
            // Add targeting AI.
            creature.targetTasks.addTask(1,
                    new EntityAINearestAttackableTarget<>(creature, EntityLivingBase.class, true));
            // Add melee attack AI at high priority (priority 0).
            creature.tasks.addTask(0, new EntityAIAttackMelee(creature, 1.25D, true));
        }
    }

    /**
     * Adjusts the mob's behavior by removing passive tasks, boosting movement,
     * and setting its attack damage to a wolf-like value.
     */
    private static void customizeHostileBehavior(EntityLiving mob) {
        removePassiveTasks(mob);
        boostMovementSpeed(mob);
        setWolfAttackDamage(mob);
    }

    /**
     * Removes passive AI tasks that cause wandering, idling, mating, panic, or even eating grass.
     */
    private static void removePassiveTasks(EntityLiving mob) {
        for (EntityAITasks.EntityAITaskEntry entry : mob.tasks.taskEntries.toArray(
                new EntityAITasks.EntityAITaskEntry[0])) {
            EntityAIBase task = entry.action;
            if (task instanceof EntityAIWander ||
                    task instanceof EntityAILookIdle ||
                    task instanceof EntityAIMate ||
                    task instanceof EntityAIPanic ||
                    task instanceof EntityAIEatGrass) {
                mob.tasks.removeTask(task);
            }
        }
    }

    /**
     * Boosts the mob's movement speed by 50% (if it has the attribute).
     */
    private static void boostMovementSpeed(EntityLiving mob) {
        if (mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null) {
            double baseSpeed = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
            mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(baseSpeed * 1.5);
        }
    }

    /**
     * Sets the mob's attack damage to 2.0 (which is roughly wolf-like).
     */
    private static void setWolfAttackDamage(EntityLiving mob) {
        if (mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
            mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
            System.out.println("PassiveMobHostilityHandler: Set attack damage for " + mob.getName() + " to 2.0");
        }
    }

    /**
     * Applies a constant AOE effect around converted mobs.
     * Every 10 ticks, any living entity (except the mob itself) within a 3-block radius
     * will be damaged (1.0 point) and knocked back slightly.
     */
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        try {
            EntityLivingBase entity = event.getEntityLiving();
            if (entity == null || entity.world.isRemote) {
                return;
            }
            // Process only passive mobs (animals or villagers).
            if (!(entity instanceof EntityAnimal || entity instanceof EntityVillager)) {
                return;
            }
            EntityLiving mob = (EntityLiving) entity;
            String customName = mob.getCustomNameTag();
            if (customName == null || customName.trim().isEmpty()) {
                return;
            }
            // Only process mobs that have been converted.
            if (!mob.getEntityData().getBoolean(CONVERTED_FLAG)) {
                return;
            }

            // Manage aura cooldown.
            int auraCooldown = mob.getEntityData().getInteger("auraCooldown");
            if (auraCooldown > 0) {
                mob.getEntityData().setInteger("auraCooldown", auraCooldown - 1);
                return;
            }

            // Define an area around the mob (3 blocks in each direction).
            double radius = 3.0;
            AxisAlignedBB aabb = new AxisAlignedBB(
                    mob.posX - radius, mob.posY, mob.posZ - radius,
                    mob.posX + radius, mob.posY + mob.height, mob.posZ + radius);

            // Get all living entities within this AABB.
            for (EntityLivingBase target : mob.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb)) {
                if (target == mob) {
                    continue;
                }
                // Apply AOE damage.
                float auraDamage = 1.0F;
                target.attackEntityFrom(DamageSource.causeMobDamage(mob), auraDamage);

                // Apply slight knockback.
                double dx = target.posX - mob.posX;
                double dz = target.posZ - mob.posZ;
                float knockBackStrength = 0.4F;
                target.knockBack(mob, knockBackStrength, dx, dz);

                System.out.println("AOE: " + mob.getName() + " hit " + target.getName() + " for " + auraDamage + " damage.");
            }
            // Reset aura cooldown (10 ticks = 0.5 seconds at 20 ticks per second).
            mob.getEntityData().setInteger("auraCooldown", 10);
        } catch (Exception e) {
            System.err.println("Error during aura effect in onLivingUpdate:");
            e.printStackTrace();
        }
    }
}