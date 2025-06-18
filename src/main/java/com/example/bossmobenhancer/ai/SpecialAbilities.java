package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.*;

public class SpecialAbilities {
    private static final Random rand = new Random();
    private static final String NBT_KEY = "bossmobenhancer.loadout";

    public static void applyLoadout(EntityLiving mob, int tier) {
        if (!mob.getEntityData().hasKey(NBT_KEY)) {
            List<Ability> options = new ArrayList<>(Arrays.asList(Ability.values()));
            Collections.shuffle(options, rand);

            List<String> selected = new ArrayList<>();
            for (int i = 0; i < Math.min(3, options.size()); i++) {
                selected.add(options.get(i).name());
            }

            mob.getEntityData().setString(NBT_KEY, String.join(",", selected));
        }

        String[] traits = mob.getEntityData().getString(NBT_KEY).split(",");
        for (String name : traits) {
            try {
                Ability.valueOf(name).apply(mob, tier);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public static String getPersonalityElement(EntityLiving mob) {
        if (!mob.getEntityData().hasKey(NBT_KEY)) return "neutral";
        String[] traits = mob.getEntityData().getString(NBT_KEY).split(",");

        for (String name : traits) {
            switch (name) {
                case "FIREBALL_SHOT":
                case "BURN_ZONE":
                    return "fire";
                case "WITHER_AURA":
                case "POISON_CLOUD":
                    return "decay";
                case "WEB_SPAWN":
                    return "web";
                case "LIGHTNING_STRIKE":
                    return "storm";
                case "BLINK_STEP":
                case "TELEPORT_DODGE":
                    return "void";
                case "HEALTH_BOOST":
                    return "life";
                default:
                    break;
            }
        }
        return "neutral";
    }

    enum Ability {
        STRENGTH_AURA((mob, tier) -> {
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 60, tier - 1));
        }),

        RESISTANCE_CORE((mob, tier) -> {
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 60, tier - 1));
        }),

        SPEED_SURGE((mob, tier) -> {
            mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 60, 1));
        }),

        HEALTH_BOOST((mob, tier) -> {
            double boost = tier * 10.0;
            mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                    .setBaseValue(mob.getMaxHealth() + boost);
            mob.setHealth(mob.getMaxHealth());
        }),

        BURN_ZONE((mob, tier) -> {
            if (tier >= 3 && !mob.world.isRemote) {
                mob.world.setBlockState(mob.getPosition().down(), Blocks.FIRE.getDefaultState());
            }
        }),

        WITHER_AURA((mob, tier) -> {
            AxisAlignedBB area = new AxisAlignedBB(mob.posX - 6, mob.posY - 1, mob.posZ - 6,
                    mob.posX + 6, mob.posY + 2, mob.posZ + 6);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 1));
            }
        }),

        BLINK_STEP((mob, tier) -> {
            if (!mob.world.isRemote && rand.nextFloat() < 0.15f) {
                List<EntityPlayer> targets = mob.world.getEntitiesWithinAABB(EntityPlayer.class,
                        new AxisAlignedBB(mob.posX - 8, mob.posY - 2, mob.posZ - 8,
                                mob.posX + 8, mob.posY + 2, mob.posZ + 8));
                if (!targets.isEmpty()) {
                    EntityPlayer player = targets.get(rand.nextInt(targets.size()));
                    mob.setPositionAndUpdate(player.posX, player.posY, player.posZ);
                }
            }
        }),

        WEB_SPAWN((mob, tier) -> {
            if (!mob.world.isRemote && tier >= 2) {
                AxisAlignedBB area = new AxisAlignedBB(mob.posX - 6, mob.posY - 1, mob.posZ - 6,
                        mob.posX + 6, mob.posY + 2, mob.posZ + 6);
                for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                    if (mob.world.isAirBlock(p.getPosition())) {
                        mob.world.setBlockState(p.getPosition(), Blocks.WEB.getDefaultState());
                    }
                }
            }
        }),

        FIREBALL_SHOT((mob, tier) -> {
            if (!mob.world.isRemote && tier >= 2) {
                AxisAlignedBB area = new AxisAlignedBB(mob.posX - 10, mob.posY - 10, mob.posZ - 10,
                        mob.posX + 10, mob.posY + 10, mob.posZ + 10);
                List<EntityPlayer> targets = mob.world.getEntitiesWithinAABB(EntityPlayer.class, area);
                if (!targets.isEmpty()) {
                    EntityPlayer target = targets.get(rand.nextInt(targets.size()));
                    double dx = target.posX - mob.posX;
                    double dy = (target.posY + target.getEyeHeight()) - (mob.posY + mob.getEyeHeight());
                    double dz = target.posZ - mob.posZ;
                    EntitySmallFireball fireball = new EntitySmallFireball(mob.world, mob, dx, dy, dz);
                    fireball.setPosition(mob.posX, mob.posY + mob.getEyeHeight(), mob.posZ);
                    mob.world.spawnEntity(fireball);
                }
            }
        }),

        LIGHTNING_STRIKE((mob, tier) -> {
            if (!mob.world.isRemote && tier >= 3) {
                AxisAlignedBB area = new AxisAlignedBB(mob.posX - 8, mob.posY - 8, mob.posZ - 8,
                        mob.posX + 8, mob.posY + 8, mob.posZ + 8);
                List<EntityPlayer> targets = mob.world.getEntitiesWithinAABB(EntityPlayer.class, area);
                if (!targets.isEmpty()) {
                    EntityPlayer target = targets.get(rand.nextInt(targets.size()));
                    mob.world.addWeatherEffect(new EntityLightningBolt(mob.world, target.posX, target.posY, target.posZ, false));
                }
            }
        }),

        POISON_CLOUD((mob, tier) -> {
            if (!mob.world.isRemote && tier >= 2) {
                AxisAlignedBB area = new AxisAlignedBB(mob.posX - 8, mob.posY - 2, mob.posZ - 8,
                        mob.posX + 8, mob.posY + 2, mob.posZ + 8);
                for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                    p.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0));
                }
            }
        }),

        TELEPORT_DODGE((mob, tier) -> {
            if (!mob.world.isRemote && tier >= 2 && rand.nextFloat() < 0.20f) {
                double newX = mob.posX + (rand.nextDouble() * 8 - 4);
                double newZ = mob.posZ + (rand.nextDouble() * 8 - 4);
                mob.setPositionAndUpdate(newX, mob.posY, newZ);
            }
        });

        public interface Action {
            void apply(EntityLiving mob, int tier);
        }

        private final Action logic;

        Ability(Action logic) {
            this.logic = logic;
        }

        public void apply(EntityLiving mob, int tier) {
            logic.apply(mob, tier);
        }
    }
}