package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles multi-phase transitions for Lord-tier bosses based on health %.
 */
@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class BossPhaseHandler {
    private static final String NBT_IS_LORD  = "isLordBoss";
    private static final String NBT_PHASE    = "bossPhase";

    @SubscribeEvent
    public static void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase base = event.getEntityLiving();
        if (!(base instanceof EntityLiving)) return;
        EntityLiving boss = (EntityLiving) base;
        if (boss.world.isRemote) return;          // server only

        NBTTagCompound data = boss.getEntityData();
        if (!data.getBoolean(NBT_IS_LORD)) return;

        float pct = boss.getHealth() / boss.getMaxHealth();
        int newPhase = pct > 0.75F ? 1
                : pct > 0.50F ? 2
                : pct > 0.25F ? 3
                : 4;

        int oldPhase = data.getInteger(NBT_PHASE);
        if (oldPhase == 0) {
            oldPhase = 1;
            data.setInteger(NBT_PHASE, 1);
        }

        if (newPhase != oldPhase) {
            data.setInteger(NBT_PHASE, newPhase);
            applyPhaseEffects(boss, newPhase);
        }
    }

    private static void applyPhaseEffects(EntityLiving boss, int phase) {
        double x = boss.posX, y = boss.posY, z = boss.posZ;

        switch (phase) {
            case 2:
                // Speed II + Regen I for 10s + Wither spawn sound
                boss.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 1));
                boss.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
                boss.world.playSound(
                        null, x, y, z,
                        SoundEvents.ENTITY_WITHER_SPAWN,
                        SoundCategory.HOSTILE,
                        1.0F, 1.0F
                );
                break;

            case 3:
                // Strength II + Resistance II + Ender Dragon growl
                boss.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 1));
                boss.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 1));
                boss.world.playSound(
                        null, x, y, z,
                        SoundEvents.ENTITY_ENDERDRAGON_GROWL,
                        SoundCategory.HOSTILE,
                        1.5F, 0.8F
                );
                break;

            case 4:
                // Strength III + Speed III + Fire Resistance + Wither death roar
                boss.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 2));
                boss.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200, 2));
                boss.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 0));
                boss.world.playSound(
                        null, x, y, z,
                        SoundEvents.ENTITY_WITHER_DEATH,
                        SoundCategory.HOSTILE,
                        2.0F, 0.5F
                );
                break;

            default:
                // Phase 1: no special effects
                break;
        }
    }
}