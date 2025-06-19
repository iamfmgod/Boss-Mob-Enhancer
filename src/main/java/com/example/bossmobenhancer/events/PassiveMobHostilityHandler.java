package com.example.bossmobenhancer.events;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class PassiveMobHostilityHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.world.isRemote) {
            return;
        }

        // Process only passive mobs.
        if (!(entity instanceof EntityAnimal || entity instanceof EntityVillager)) {
            return;
        }

        // Only process mobs that have been given a custom name.
        String customName = entity.getCustomNameTag();
        if (customName == null || customName.trim().isEmpty()) {
            return;
        }

        // Check if the mob is already hostile.
        EntityLiving mob = (EntityLiving) entity;
        if (alreadyHostile(mob)) {
            return;
        }

        // Ensure there is an attacker from the damage source.
        DamageSource source = event.getSource();
        EntityLivingBase attacker = null;
        if (source.getTrueSource() instanceof EntityLivingBase) {
            attacker = (EntityLivingBase) source.getTrueSource();
        }
        if (attacker == null) {
            return;
        }

        // Convert this peaceful mob to a hostile one.
        System.out.println("PassiveMobHostilityHandler: Converting mob " + mob.getName() +
                " with custom name '" + customName + "' to hostile due to being attacked.");
        addHostileAI(mob, attacker);
        customizeHostileBehavior(mob);
    }

    /**
     * Checks whether the mob already has a hostile AI pointing to a target.
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
     * Adds a hostile AI task to the mob and sets its attack target to the attacker.
     *
     * @param mob      The passive mob being converted.
     * @param attacker The entity that attacked.
     */
    private static void addHostileAI(EntityLiving mob, EntityLivingBase attacker) {
        if (mob instanceof EntityCreature) {
            EntityCreature creature = (EntityCreature) mob;
            // Set the direct attack target.
            creature.setAttackTarget(attacker);
            // Add a hostile targeting AI if not already present.
            creature.targetTasks.addTask(1,
                    new EntityAINearestAttackableTarget<EntityLivingBase>(creature, EntityLivingBase.class, true));
        }
    }

    /**
     * Removes certain passive behaviors and boosts the mob's movement speed.
     *
     * @param mob The mob to be customized.
     */
    private static void customizeHostileBehavior(EntityLiving mob) {
        removePassiveTasks(mob);
        boostMovementSpeed(mob);
    }

    /**
     * Iterates through the mob's AI tasks and removes passive behaviors such as wandering, idling, and mating.
     */
    private static void removePassiveTasks(EntityLiving mob) {
        List<EntityAITasks.EntityAITaskEntry> tasksToRemove = new ArrayList<>();
        for (EntityAITasks.EntityAITaskEntry entry : mob.tasks.taskEntries) {
            EntityAIBase task = entry.action;
            if (task instanceof EntityAIWander ||
                    task instanceof EntityAILookIdle ||
                    task instanceof EntityAIMate) {
                tasksToRemove.add(entry);
            }
        }
        for (EntityAITasks.EntityAITaskEntry entry : tasksToRemove) {
            mob.tasks.taskEntries.remove(entry);
        }
    }

    /**
     * Boosts the mob's movement speed by 50% to help it engage more fiercely.
     */
    private static void boostMovementSpeed(EntityLiving mob) {
        double baseSpeed = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
        mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(baseSpeed * 1.5);
    }
}