package com.example.bossmobenhancer.events;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class PassiveMobHostilityHandler {

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        // Only run on the server side
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityLiving)) {
            return;
        }
        EntityLiving mob = (EntityLiving) entity;

        // Only consider passive mobs: animals and villagers.
        if (!(mob instanceof EntityAnimal || mob instanceof EntityVillager)) {
            return;
        }

        String customName = mob.getCustomNameTag();
        if (customName == null || customName.trim().isEmpty()) {
            return;
        }

        // Check if the custom name matches a boss name pattern and if the mob isn't already hostile.
        if (isNameGeneratedByNameGenerator(customName) && !alreadyHostile(mob)) {
            System.out.println("PassiveMobHostilityHandler: Converting mob " + mob.getName() +
                    " with custom name '" + customName + "' to hostile.");
            addHostileAI(mob);
            customizeHostileBehavior(mob);
        }
    }

    /**
     * Determines if the given name matches one of the patterns produced by the NameGenerator.
     */
    private static boolean isNameGeneratedByNameGenerator(String name) {
        if (name.startsWith("§7Wanderer ") ||
                name.startsWith("§7Challenger ") ||
                name.startsWith("§7Champion ") ||
                name.startsWith("§7Overlord ") ||
                name.startsWith("§7Eclipsebound ") ||
                name.startsWith("§6Lord ")) {
            return true;
        }
        // Fallback: if the name begins with one of the common color codes used by the NameGenerator.
        return name.matches("^§[abceABCE].+");
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
     * Adds a hostile-targeting AI task so that the mob will begin attacking nearby players.
     */
    private static void addHostileAI(EntityLiving mob) {
        if (mob instanceof EntityCreature) {
            EntityCreature creature = (EntityCreature) mob;
            creature.targetTasks.addTask(1,
                    new EntityAINearestAttackableTarget<EntityPlayer>(creature, EntityPlayer.class, true));
        }
    }

    /**
     * Customizes the mob's behavior by removing certain passive tasks and boosting movement speed.
     */
    private static void customizeHostileBehavior(EntityLiving mob) {
        removePassiveTasks(mob);
        boostMovementSpeed(mob);
    }

    /**
     * Iterates through the mob's AI tasks and removes certain passive behaviors (wandering, idle looking, mating)
     * to emphasize the hostile state.
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
     * Increases the mob's movement speed by 50% to signal its hostile state.
     */
    private static void boostMovementSpeed(EntityLiving mob) {
        double baseSpeed = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
        mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(baseSpeed * 1.5);
    }
}