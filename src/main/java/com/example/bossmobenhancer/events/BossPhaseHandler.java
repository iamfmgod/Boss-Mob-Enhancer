package com.example.bossmobenhancer.events;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * BossPhaseHandler handles multi-phase transitions for lord boss entities.
 * It calculates the boss’ health percentage and updates its phase accordingly:
 *
 *   - Phase 1: Health > 75%
 *   - Phase 2: Health between 50% and 75%
 *   - Phase 3: Health between 25% and 50%
 *   - Phase 4: Health ≤ 25%
 *
 * On a phase transition, temporary potion effects are applied and a sound is played,
 * helping communicate the boss’ shifting battle state.
 */
@Mod.EventBusSubscriber
public class BossPhaseHandler {

    @SubscribeEvent
    public static void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        // Process only if the entity is an instance of EntityLiving.
        if (!(entity instanceof EntityLiving)) {
            return;
        }
        EntityLiving mob = (EntityLiving) entity;

        // Check if this mob is flagged as a lord boss.
        if (!mob.getEntityData().getBoolean("isLordBoss")) {
            return;
        }

        // Calculate current health percentage.
        float currentHealth = mob.getHealth();
        float maxHealth = mob.getMaxHealth();
        float healthPercent = currentHealth / maxHealth;

        // Determine new phase based on health percentage:
        //   Phase 1: >75%
        //   Phase 2: 50–75%
        //   Phase 3: 25–50%
        //   Phase 4: ≤25%
        int newPhase;
        if (healthPercent > 0.75F) {
            newPhase = 1;
        } else if (healthPercent > 0.50F) {
            newPhase = 2;
        } else if (healthPercent > 0.25F) {
            newPhase = 3;
        } else {
            newPhase = 4;
        }

        // Retrieve and set the current phase from NBT (default to phase 1 if not set).
        int currentPhase = mob.getEntityData().getInteger("bossPhase");
        if (currentPhase == 0) {
            currentPhase = 1;
            mob.getEntityData().setInteger("bossPhase", 1);
        }

        // If a phase transition occurred, update the phase and apply effects.
        if (newPhase != currentPhase) {
            mob.getEntityData().setInteger("bossPhase", newPhase);
            applyPhaseEffects(mob, newPhase);
        }
    }

    /**
     * Applies temporary potion effects and plays sounds to mark the phase transition.
     *
     * @param mob   The lord boss entity.
     * @param phase The new phase number (2, 3, or 4).
     */
    private static void applyPhaseEffects(EntityLiving mob, int phase) {
        switch (phase) {
            case 2:
                // Phase 2: Increase speed (Speed II) and add regeneration (Regen I) for 10 seconds.
                mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 1));
                mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
                mob.world.playSound(null, mob.getPosition(), net.minecraft.init.SoundEvents.ENTITY_WITHER_SPAWN,
                        net.minecraft.util.SoundCategory.HOSTILE, 1.0F, 1.0F);
                break;
            case 3:
                // Phase 3: Boost strength (Strength II) and resistance (Resistance II) for 10 seconds.
                mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 1));
                mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 1));
                mob.world.playSound(null, mob.getPosition(), net.minecraft.init.SoundEvents.ENTITY_ENDERDRAGON_GROWL,
                        net.minecraft.util.SoundCategory.HOSTILE, 1.5F, 0.8F);
                break;
            case 4:
                // Phase 4: Final frenzy mode – boost strength (Strength III), speed (Speed III), and grant fire resistance for 10 seconds.
                mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 2));
                mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 2));
                mob.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 0));
                mob.world.playSound(null, mob.getPosition(), net.minecraft.init.SoundEvents.ENTITY_WITHER_DEATH,
                        net.minecraft.util.SoundCategory.HOSTILE, 2.0F, 0.5F);
                break;
            default:
                // Phase 1 is the default state on spawn with no extra transitional effects.
                break;
        }
    }
}