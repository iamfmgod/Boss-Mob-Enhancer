package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.*;

public class SpecialAbilities {
    private static final Random rand = new Random();
    private static final String NBT_KEY = "bossmobenhancer.loadout";

    public static void applyLoadout(EntityLiving mob, int tier) {
        if (!mob.getEntityData().hasKey(NBT_KEY)) {
            List<Ability> options = new ArrayList<>(Arrays.asList(Ability.values()));
            Collections.shuffle(options, rand);

            // Pick three abilities (adjust selection count as desired)
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
                case "FINAL_CATACLYSM":
                case "FLAME_WHIRLWIND":
                case "SOLAR_FLARE":
                    return "fire";
                case "WITHER_AURA":
                case "POISON_CLOUD":
                case "BLIGHT_CLOUD":
                case "POISON_FOG":
                case "CURSE_OF_WOE":
                    return "decay";
                case "WEB_SPAWN":
                    return "web";
                case "LIGHTNING_STRIKE":
                case "ARCING_LIGHTNING":
                case "THUNDER_ROAR":
                    return "storm";
                case "BLINK_STEP":
                case "TELEPORT_DODGE":
                case "PHASE_SHIFT":
                case "WARPING_REALM":
                    return "void";
                case "HEALTH_BOOST":
                case "LIFE_DRAIN":
                case "NATURE_EMBRACE":
                    return "life";
                default:
                    break;
            }
        }
        return "neutral";
    }

    enum Ability {
        // --- Original 24 Abilities (example implementations for first 4, rest are placeholders) ---
        ORIG_1((mob, tier) -> {
            int duration = 20 * (60 + tier * 5);
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, tier));
        }),
        ORIG_2((mob, tier) -> {
            int duration = 20 * (60 + tier * 5);
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, tier));
        }),
        ORIG_3((mob, tier) -> {
            int duration = 20 * (60 + tier * 5);
            int amplifier = 1 + (tier / 3);
            mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, amplifier));
        }),
        ORIG_4((mob, tier) -> {
            double boost = tier * 12.0;
            mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                    .setBaseValue(mob.getMaxHealth() + boost);
            mob.setHealth(mob.getMaxHealth());
        }),
        ORIG_5((mob, tier) -> {}), ORIG_6((mob, tier) -> {}), ORIG_7((mob, tier) -> {}), ORIG_8((mob, tier) -> {}),
        ORIG_9((mob, tier) -> {}), ORIG_10((mob, tier) -> {}), ORIG_11((mob, tier) -> {}), ORIG_12((mob, tier) -> {}),
        ORIG_13((mob, tier) -> {}), ORIG_14((mob, tier) -> {}), ORIG_15((mob, tier) -> {}), ORIG_16((mob, tier) -> {}),
        ORIG_17((mob, tier) -> {}), ORIG_18((mob, tier) -> {}), ORIG_19((mob, tier) -> {}), ORIG_20((mob, tier) -> {}),
        ORIG_21((mob, tier) -> {}), ORIG_22((mob, tier) -> {}), ORIG_23((mob, tier) -> {}), ORIG_24((mob, tier) -> {}),

        // --- New Abilities (NEW_ABILITY_25 to NEW_ABILITY_84: 60 abilities, placeholders) ---
        NEW_ABILITY_25((mob, tier) -> {}), NEW_ABILITY_26((mob, tier) -> {}), NEW_ABILITY_27((mob, tier) -> {}), NEW_ABILITY_28((mob, tier) -> {}),
        NEW_ABILITY_29((mob, tier) -> {}), NEW_ABILITY_30((mob, tier) -> {}), NEW_ABILITY_31((mob, tier) -> {}), NEW_ABILITY_32((mob, tier) -> {}),
        NEW_ABILITY_33((mob, tier) -> {}), NEW_ABILITY_34((mob, tier) -> {}), NEW_ABILITY_35((mob, tier) -> {}), NEW_ABILITY_36((mob, tier) -> {}),
        NEW_ABILITY_37((mob, tier) -> {}), NEW_ABILITY_38((mob, tier) -> {}), NEW_ABILITY_39((mob, tier) -> {}), NEW_ABILITY_40((mob, tier) -> {}),
        NEW_ABILITY_41((mob, tier) -> {}), NEW_ABILITY_42((mob, tier) -> {}), NEW_ABILITY_43((mob, tier) -> {}), NEW_ABILITY_44((mob, tier) -> {}),
        NEW_ABILITY_45((mob, tier) -> {}), NEW_ABILITY_46((mob, tier) -> {}), NEW_ABILITY_47((mob, tier) -> {}), NEW_ABILITY_48((mob, tier) -> {}),
        NEW_ABILITY_49((mob, tier) -> {}), NEW_ABILITY_50((mob, tier) -> {}), NEW_ABILITY_51((mob, tier) -> {}), NEW_ABILITY_52((mob, tier) -> {}),
        NEW_ABILITY_53((mob, tier) -> {}), NEW_ABILITY_54((mob, tier) -> {}), NEW_ABILITY_55((mob, tier) -> {}), NEW_ABILITY_56((mob, tier) -> {}),
        NEW_ABILITY_57((mob, tier) -> {}), NEW_ABILITY_58((mob, tier) -> {}), NEW_ABILITY_59((mob, tier) -> {}), NEW_ABILITY_60((mob, tier) -> {}),
        NEW_ABILITY_61((mob, tier) -> {}), NEW_ABILITY_62((mob, tier) -> {}), NEW_ABILITY_63((mob, tier) -> {}), NEW_ABILITY_64((mob, tier) -> {}),
        NEW_ABILITY_65((mob, tier) -> {}), NEW_ABILITY_66((mob, tier) -> {}), NEW_ABILITY_67((mob, tier) -> {}), NEW_ABILITY_68((mob, tier) -> {}),
        NEW_ABILITY_69((mob, tier) -> {}), NEW_ABILITY_70((mob, tier) -> {}), NEW_ABILITY_71((mob, tier) -> {}), NEW_ABILITY_72((mob, tier) -> {}),
        NEW_ABILITY_73((mob, tier) -> {}), NEW_ABILITY_74((mob, tier) -> {}), NEW_ABILITY_75((mob, tier) -> {}), NEW_ABILITY_76((mob, tier) -> {}),
        NEW_ABILITY_77((mob, tier) -> {}), NEW_ABILITY_78((mob, tier) -> {}), NEW_ABILITY_79((mob, tier) -> {}), NEW_ABILITY_80((mob, tier) -> {}),
        NEW_ABILITY_81((mob, tier) -> {}), NEW_ABILITY_82((mob, tier) -> {}), NEW_ABILITY_83((mob, tier) -> {}), NEW_ABILITY_84((mob, tier) -> {}),

        // --- Final 5 Abilities ---
        FINAL_CATACLYSM((mob, tier) -> {
            if (!mob.world.isRemote) {
                double radius = 10.0;
                AxisAlignedBB expArea = new AxisAlignedBB(
                        mob.posX - radius, mob.posY - radius, mob.posZ - radius,
                        mob.posX + radius, mob.posY + radius, mob.posZ + radius);
                for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, expArea)) {
                    p.attackEntityFrom(DamageSource.causeMobDamage(mob), 10.0F + 2.0F * tier);
                }
                mob.world.createExplosion(mob, mob.posX, mob.posY, mob.posZ, 4.0F + tier, false);
            }
        }),
        ETHEREAL_WARP((mob, tier) -> {
            if (!mob.world.isRemote && rand.nextFloat() < 0.3f) {
                double newX = mob.posX + (rand.nextDouble() * 6 - 3);
                double newY = mob.posY;
                double newZ = mob.posZ + (rand.nextDouble() * 6 - 3);
                mob.setPositionAndUpdate(newX, newY, newZ);
                mob.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 60 + 10 * tier, 0));
            }
        }),
        VOID_EXPLOSION((mob, tier) -> {
            if (!mob.world.isRemote) {
                AxisAlignedBB area = new AxisAlignedBB(
                        mob.posX - 7, mob.posY - 2, mob.posZ - 7,
                        mob.posX + 7, mob.posY + 3, mob.posZ + 7);
                for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                    p.attackEntityFrom(DamageSource.causeMobDamage(mob), 5.0F + tier);
                    p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100 + 10 * tier, 1));
                    p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100 + 10 * tier, 1));
                }
                mob.world.createExplosion(mob, mob.posX, mob.posY, mob.posZ, 3.0F + 0.5F * tier, false);
            }
        }),
        CELESTIAL_RADIANCE((mob, tier) -> {
            mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 120 + 10 * tier, 1));
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - 6, mob.posY - 1, mob.posZ - 6,
                    mob.posX + 6, mob.posY + 2, mob.posZ + 6);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60 + 5 * tier, 0));
            }
        }),
        FINAL_CONVERGENCE((mob, tier) -> {
            if (!mob.world.isRemote) {
                mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 200 + 20 * tier, tier));
                AxisAlignedBB area = new AxisAlignedBB(
                        mob.posX - 8, mob.posY - 2, mob.posZ - 8,
                        mob.posX + 8, mob.posY + 2, mob.posZ + 8);
                for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                    p.attackEntityFrom(DamageSource.causeMobDamage(mob), 8.0F + 2.0F * tier);
                }
            }
        }),
        // --- Additional 11 Abilities to reach 100 total ---
        ADDITIONAL_1((mob, tier) -> {}),
        ADDITIONAL_2((mob, tier) -> {}),
        ADDITIONAL_3((mob, tier) -> {}),
        ADDITIONAL_4((mob, tier) -> {}),
        ADDITIONAL_5((mob, tier) -> {}),
        ADDITIONAL_6((mob, tier) -> {}),
        ADDITIONAL_7((mob, tier) -> {}),
        ADDITIONAL_8((mob, tier) -> {}),
        ADDITIONAL_9((mob, tier) -> {}),
        ADDITIONAL_10((mob, tier) -> {}),
        ADDITIONAL_11((mob, tier) -> {});

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