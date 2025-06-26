package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class LordSpawnHandler {
    private static final Logger LOGGER = LogManager.getLogger(MainMod.MODID);

    @SubscribeEvent
    public static void onSpecialSpawn(SpecialSpawn event) {
        // --- Safety guards ---
        EntityLivingBase base = event.getEntityLiving();
        if (!(base instanceof EntityLiving)) return;

        World world = base.world;
        if (world == null || world.isRemote) return;

        EntityLiving mob = (EntityLiving) base;
        try {
            // Skip already-named or custom-tagged
            String currentName = mob.getCustomNameTag();
            if (currentName != null && !currentName.isEmpty()) return;

            // Chance & tuning per dimension
            Random rand = mob.getRNG();
            int dim = world.provider.getDimension();

            double spawnChance;
            int bossTier;
            double hpScale, atkScale, spdScale;
            String prefix;

            switch (dim) {
                case 0:
                    spawnChance = 0.10;
                    bossTier     = 1;
                    hpScale      = 2.0;
                    atkScale     = 1.3;
                    spdScale     = 1.15;
                    prefix       = "Overworld";
                    break;
                case -1:
                    spawnChance = 0.20;
                    bossTier     = 2;
                    hpScale      = 3.0;
                    atkScale     = 1.6;
                    spdScale     = 1.20;
                    prefix       = "Nether";
                    break;
                case 1:
                    spawnChance = 0.35;
                    bossTier     = 3;
                    hpScale      = 4.5;
                    atkScale     = 2.0;
                    spdScale     = 1.25;
                    prefix       = "End";
                    break;
                default:
                    spawnChance = 0.08;
                    bossTier     = 1;
                    hpScale      = 2.0;
                    atkScale     = 1.2;
                    spdScale     = 1.10;
                    prefix       = "Unknown";
            }

            if (rand.nextDouble() >= spawnChance) return;

            // --- Name the boss ---
            String name = NameGenerator.generateFantasyName(mob, bossTier);
            String bossName = TextFormatting.DARK_PURPLE +
                    "Lord " + prefix + " " + (name != null ? name : "Unknown");
            mob.setCustomNameTag(bossName);
            mob.setAlwaysRenderNameTag(true);

            // --- Scale HP safely ---
            if (mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null) {
                double baseHP = mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                double newHP  = baseHP * hpScale;
                mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newHP);
                mob.setHealth((float) newHP);
            } else {
                LOGGER.warn("Entity {} has no MAX_HEALTH attribute!", mob);
            }

            // --- Scale Attack Damage safely ---
            if (mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
                double baseAtk = mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(baseAtk * atkScale);
            }

            // --- Scale Movement Speed safely ---
            if (mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null) {
                double baseSpd = mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue();
                mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(baseSpd * spdScale);
            }

            // --- Give starting buffs ---
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,   200, 1));
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,     200, 1));
            mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));

            // --- Tag for other systems ---
            NBTTagCompound data = mob.getEntityData();
            data.setBoolean("isLordBoss", true);
            data.setInteger("bossTier",    bossTier);
            data.setInteger("bossPhase",   1);

            // --- Dramatic effects ---
            world.playSound(null, mob.getPosition(),
                    SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.HOSTILE, 2.0F, 0.8F);

            EntityLightningBolt bolt = new EntityLightningBolt(world,
                    mob.posX, mob.posY, mob.posZ, false);
            world.spawnEntity(bolt);

        } catch (Exception ex) {
            // Prevent any error in here from killing the server tick
            LOGGER.error("Exception in LordSpawnHandler for entity {}", mob, ex);
        }
    }
}