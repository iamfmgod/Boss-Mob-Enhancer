package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SpecialAbilities {
    private static final Random rand = new Random();
    private static final String NBT_KEY = "bossmobenhancer.loadout";

    public static void applyLoadout(EntityLiving mob, int tier) {
        // If the mob doesn't yet have a saved loadout, choose 3 random abilities.
        if (!mob.getEntityData().hasKey(NBT_KEY)) {
            List<Ability> options = new ArrayList<>();
            Collections.addAll(options, Ability.values());
            Collections.shuffle(options, rand);

            List<String> selected = new ArrayList<>();
            for (int i = 0; i < Math.min(3, options.size()); i++) {
                selected.add(options.get(i).name());
            }

            mob.getEntityData().setString(NBT_KEY, String.join(",", selected));
        }

        // Retrieve and apply each saved ability.
        String[] traits = mob.getEntityData().getString(NBT_KEY).split(",");
        for (String name : traits) {
            try {
                Ability.valueOf(name).apply(mob, tier);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    enum Ability {
        // Basic buffs
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

        // Environmental effects
        BURN_ZONE((mob, tier) -> {
            if (tier >= 3 && !mob.world.isRemote) {
                // Ignite the block beneath the mob.
                mob.world.setBlockState(mob.getPosition().down(), Blocks.FIRE.getDefaultState());
            }
        }),

        WITHER_AURA((mob, tier) -> {
            World world = mob.world;
            AxisAlignedBB area = new AxisAlignedBB(mob.posX - 6, mob.posY - 1, mob.posZ - 6,
                    mob.posX + 6, mob.posY + 2, mob.posZ + 6);
            for (EntityPlayer p : world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 1));
            }
        }),

        // Mobility ability
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

        // New Ability: Spawn webs beneath nearby players
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

        // New Ability: Launch a fireball at a nearby player
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

        // New Ability: Call down lightning on a nearby player
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

        // New Ability: Create a cloud that poisons nearby players
        POISON_CLOUD((mob, tier) -> {
            if (!mob.world.isRemote && tier >= 2) {
                AxisAlignedBB area = new AxisAlignedBB(mob.posX - 8, mob.posY - 2, mob.posZ - 8,
                        mob.posX + 8, mob.posY + 2, mob.posZ + 8);
                for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                    p.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0));
                }
            }
        }),

        // New Ability: Teleport away randomly (dodge)
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