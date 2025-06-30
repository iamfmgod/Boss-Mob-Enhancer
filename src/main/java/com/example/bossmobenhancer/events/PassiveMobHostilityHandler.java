package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(
        modid = MainMod.MODID,
        value = Side.SERVER          // only subscribe on the dedicated/server side
)
public class PassiveMobHostilityHandler {
    private static final Logger LOGGER         = LogManager.getLogger(MainMod.MODID);
    private static final String CONVERTED_FLAG = "convertedToHostile";
    private static final String AURA_COOLDOWN  = "auraCooldown";

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!isEligible(entity, false)) return;

        EntityLiving mob = (EntityLiving) entity;
        if (!(event.getSource().getTrueSource() instanceof EntityLivingBase)) return;
        EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();

        LOGGER.info("Converting {} ('{}') to hostile (attacked by {})",
                mob.getName(), mob.getCustomNameTag(), attacker.getName());
        convertToHostile(mob, attacker);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!isEligible(entity, true)) return;

        EntityLiving mob = (EntityLiving) entity;
        NBTTagCompound data = mob.getEntityData();
        int cd = data.getInteger(AURA_COOLDOWN);
        if (cd > 0) {
            data.setInteger(AURA_COOLDOWN, cd - 1);
            return;
        }

        applyAura(mob);
        data.setInteger(AURA_COOLDOWN, 10);
    }

    private static boolean isEligible(EntityLivingBase entity, boolean mustBeConverted) {
        if (entity == null || entity.world.isRemote) return false;
        if (!(entity instanceof EntityAnimal || entity instanceof EntityVillager)) return false;

        EntityLiving mob = (EntityLiving) entity;
        if (mob.getCustomNameTag().isEmpty()) return false;

        boolean converted = mob.getEntityData().getBoolean(CONVERTED_FLAG);
        return mustBeConverted ? converted : !converted;
    }

    private static void convertToHostile(EntityLiving mob, EntityLivingBase attacker) {
        // --- Remove passive AI safely ---
        if (mob.tasks != null && mob.tasks.taskEntries != null) {
            List<EntityAIBase> toRemove = mob.tasks.taskEntries.stream()
                    .map(entry -> entry.action)
                    .filter(a ->
                            a instanceof EntityAIWander ||
                                    a instanceof EntityAILookIdle ||
                                    a instanceof EntityAIMate ||
                                    a instanceof EntityAIPanic ||
                                    a instanceof EntityAIEatGrass
                    )
                    .collect(Collectors.toList());

            toRemove.forEach(ai -> {
                try {
                    mob.tasks.removeTask(ai);
                } catch (Exception e) {
                    LOGGER.warn("Failed to remove AI task {} from {}", ai, mob);
                }
            });
        }

        // --- Add hostile AI ---
        if (mob instanceof EntityCreature) {
            EntityCreature creature = (EntityCreature) mob;
            creature.setAttackTarget(attacker);
            if (creature.targetTasks != null)
                creature.targetTasks.addTask(1,
                        new EntityAINearestAttackableTarget<>(creature, EntityLivingBase.class, true));
            if (creature.tasks != null)
                creature.tasks.addTask(2,
                        new EntityAIAttackMelee(creature, 1.25D, true));
        }

        // --- Buff attributes with null‐checks ---
        IAttributeInstance speedAttr =
                mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(speedAttr.getBaseValue() * 1.5D);
        } else {
            LOGGER.warn("Missing MOVEMENT_SPEED on {}, skipping speed buff", mob);
        }

        IAttributeInstance dmgAttr =
                mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.setBaseValue(2.0D);
        }

        // --- Mark as converted ---
        NBTTagCompound data = mob.getEntityData();
        data.setBoolean(CONVERTED_FLAG, true);
        data.setInteger(AURA_COOLDOWN, 0);
    }

    private static void applyAura(EntityLiving mob) {
        double x = mob.posX, y = mob.posY, z = mob.posZ;
        AxisAlignedBB box = new AxisAlignedBB(x - 3, y, z - 3, x + 3, y + mob.height, z + 3);

        mob.world.getEntitiesWithinAABB(EntityLivingBase.class, box)
                .stream()
                .filter(t -> t != mob)
                .forEach(t -> {
                    t.attackEntityFrom(DamageSource.causeMobDamage(mob), 1.0F);
                    double dx = t.posX - x, dz = t.posZ - z;
                    t.knockBack(mob, 0.4F, dx, dz);
                });
    }
}