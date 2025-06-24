package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;


import java.util.*;

public class SpecialAbilities {
    private static final Random rand = new Random();
    private static final String NBT_KEY = "bossmobenhancer.loadout";

    // Preset boss name to ability loadout mapping.
    private static final Map<String, List<Ability>> presetLoadouts = new HashMap<>();
    static {
        // Boss with the name "Flame Lord" always gets these abilities.
        presetLoadouts.put("Flame Lord", Arrays.asList(
                Ability.FLAME_WHIRLWIND,
                Ability.EMBER_BURST,
                Ability.SOLAR_FLARE
        ));
        // Boss with the name "Frost King" always gets these abilities.
        presetLoadouts.put("Frost King", Arrays.asList(
                Ability.FROST_NOVA,
                Ability.ARCANE_SHIELD,
                Ability.EARTHQUAKE
        ));
        // You can add additional preset mappings as needed.
    }

    /**
     * Applies a loadout of abilities to the given mob.
     * If the mob’s custom name matches a preset loadout, that preset is used;
     * otherwise, three random abilities from the full pool are selected.
     *
     * @param mob  The EntityLiving to enhance.
     * @param tier The tier to scale the abilities.
     */
    public static void applyLoadout(EntityLiving mob, int tier) {
        List<Ability> selectedAbilities;

        String bossName = mob.getCustomNameTag();
        if (bossName != null && presetLoadouts.containsKey(bossName)) {
            selectedAbilities = presetLoadouts.get(bossName);
        } else {
            List<Ability> options = new ArrayList<>(Arrays.asList(Ability.values()));
            Collections.shuffle(options, rand);
            selectedAbilities = options.subList(0, Math.min(3, options.size()));
        }

        // Store the chosen abilities as a comma-separated string in the mob's NBT.
        StringBuilder loadout = new StringBuilder();
        for (Ability a : selectedAbilities) {
            loadout.append(a.name()).append(",");
        }
        if (loadout.length() > 0) {
            loadout.setLength(loadout.length() - 1);  // remove trailing comma
        }
        mob.getEntityData().setString(NBT_KEY, loadout.toString());

        // Apply each ability in the stored loadout.
        String[] traits = mob.getEntityData().getString(NBT_KEY).split(",");
        for (String abilityName : traits) {
            try {
                Ability.valueOf(abilityName).apply(mob, tier);
            } catch (IllegalArgumentException ignored) {
                // Unknown ability—ignore it.
            }
        }
    }

    /**
     * Returns a personality element based on the mob's loaded abilities.
     * For example, if the loadout contains fire-related abilities, returns "fire".
     *
     * @param mob The mob whose abilities should be evaluated.
     * @return A string representing the personality element.
     */
    public static String getPersonalityElement(EntityLiving mob) {
        if (!mob.getEntityData().hasKey(NBT_KEY))
            return "neutral";
        String[] traits = mob.getEntityData().getString(NBT_KEY).split(",");
        for (String name : traits) {
            switch (name) {
                case "FLAME_WHIRLWIND":
                case "EMBER_BURST":
                case "SOLAR_FLARE":
                    return "fire";
                case "FROST_NOVA":
                case "ARCANE_SHIELD":
                case "EARTHQUAKE":
                    return "ice";
                case "WITHER_AURA":
                case "POISON_CLOUD":
                    return "decay";
                case "LIGHTNING_STRIKE":
                case "STORM_CALL":
                    return "storm";
                case "SHADOW_VEIL":
                case "MIRROR_IMAGE":
                    return "shade";
                default:
                    break;
            }
        }
        return "neutral";
    }

    public enum Ability {
        // --- Original 18 Abilities ---
        FLAME_WHIRLWIND((mob, tier) -> {
            double radius = 5.0 + tier;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.setFire(5);
                p.attackEntityFrom(DamageSource.IN_FIRE, 4.0F * tier);
            }
        }),
        SOLAR_FLARE((mob, tier) -> {
            double radius = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60 + tier * 10, 0));
            }
        }),
        WITHER_AURA((mob, tier) -> {
            double radius = 4.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.WITHER, 100 + tier * 20, 1));
            }
        }),
        POISON_CLOUD((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.POISON, 100 + tier * 10, 1));
            }
        }),
        WEB_SPAWN((mob, tier) -> {
            double radius = 3.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 1, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 1, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 2));
            }
        }),
        LIGHTNING_STRIKE((mob, tier) -> {
            double radius = 8.0;
            List<EntityPlayer> players = mob.world.getEntitiesWithinAABB(EntityPlayer.class,
                    new AxisAlignedBB(
                            mob.posX - radius, mob.posY - 3, mob.posZ - radius,
                            mob.posX + radius, mob.posY + 3, mob.posZ + radius));
            if (!players.isEmpty() && mob.world.rand.nextFloat() < 0.5f) {
                EntityPlayer target = players.get(mob.world.rand.nextInt(players.size()));
                mob.world.addWeatherEffect(new EntityLightningBolt(mob.world, target.posX, target.posY, target.posZ, false));
            }
        }),
        BLINK_STEP((mob, tier) -> {
            double range = 4.0;
            double newX = mob.posX + (mob.world.rand.nextDouble() * range - range / 2);
            double newY = mob.posY;
            double newZ = mob.posZ + (mob.world.rand.nextDouble() * range - range / 2);
            mob.setPositionAndUpdate(newX, newY, newZ);
            mob.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 40 + 10 * tier, 0));
        }),
        HEALTH_BOOST((mob, tier) -> {
            double boost = 10.0 * tier;
            mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                    .setBaseValue(mob.getMaxHealth() + boost);
            mob.setHealth(mob.getMaxHealth());
        }),
        RAGING_BLOW((mob, tier) -> {
            int duration = 20 * (10 + tier * 2);
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, tier));
        }),
        SHADOW_VEIL((mob, tier) -> {
            mob.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 60 + tier * 10, 0));
            mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60 + tier * 10, 1));
        }),
        EMBER_BURST((mob, tier) -> {
            double radius = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.setFire(4);
            }
        }),
        FROST_NOVA((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100 + tier * 10, 2));
                p.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100 + tier * 10, 1));
            }
        }),
        EARTHQUAKE((mob, tier) -> {
            double radius = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.GENERIC, 4.0F * tier);
                p.motionX += (p.posX - mob.posX) * 0.5;
                p.motionZ += (p.posZ - mob.posZ) * 0.5;
            }
        }),
        ARCANE_SHIELD((mob, tier) -> {
            int duration = 20 * (10 + tier * 2);
            mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, duration, tier));
        }),
        VENOMOUS_CLOUD((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.POISON, 120 + tier * 10, 1));
                p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120 + tier * 10, 2));
            }
        }),
        STORM_CALL((mob, tier) -> {
            double radius = 8.0;
            List<EntityPlayer> players = mob.world.getEntitiesWithinAABB(EntityPlayer.class,
                    new AxisAlignedBB(
                            mob.posX - radius, mob.posY - 3, mob.posZ - radius,
                            mob.posX + radius, mob.posY + 3, mob.posZ + radius));
            for (EntityPlayer p : players) {
                mob.world.addWeatherEffect(new EntityLightningBolt(mob.world, p.posX, p.posY, p.posZ, false));
            }
        }),
        FORCE_WAVE((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.motionX += (p.posX - mob.posX) * 0.7;
                p.motionZ += (p.posZ - mob.posZ) * 0.7;
            }
        }),
        MIRROR_IMAGE((mob, tier) -> {
            double radius = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 80 + tier * 10, 0));
            }
        }),

        // --- 25 New Abilities ---
        FURY_RUSH((mob, tier) -> {
            int duration = 20 * (10 + tier * 2);
            mob.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 1));
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, 1));
        }),
        TIME_DILATION((mob, tier) -> {
            double radius = 7.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100 + tier * 10, 2));
            }
        }),
        INFERNO((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.setFire(6);
                p.attackEntityFrom(DamageSource.IN_FIRE, 5.0F * tier);
            }
        }),
        SHOCKWAVE((mob, tier) -> {
            double radius = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 1, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 1, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.causeMobDamage(mob), 3.0F * tier);
                p.motionX += (p.posX - mob.posX) * 0.8;
                p.motionZ += (p.posZ - mob.posZ) * 0.8;
            }
        }),
        SILENCE((mob, tier) -> {
            double radius = 4.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 1, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 1, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 100 + tier * 10, 2));
            }
        }),
        CROWSTORM((mob, tier) -> {
            double radius = 8.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 3, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 3, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 80 + tier * 10, 0));
            }
        }),
        GRAVITY_WELL((mob, tier) -> {
            double radius = 7.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                double dx = mob.posX - p.posX;
                double dz = mob.posZ - p.posZ;
                p.motionX += dx * 0.05;
                p.motionZ += dz * 0.05;
            }
        }),
        PHANTOM_DASH((mob, tier) -> {
            double range = 5.0;
            double newX = mob.posX + (mob.world.rand.nextDouble() * range - range / 2);
            double newZ = mob.posZ + (mob.world.rand.nextDouble() * range - range / 2);
            mob.setPositionAndUpdate(newX, mob.posY, newZ);
        }),
        VOID_RIFT((mob, tier) -> {
            if (!mob.world.isRemote) {
                mob.world.createExplosion(mob, mob.posX, mob.posY, mob.posZ, 2.0F + tier, false);
            }
        }),
        ARCANE_MYSTERY((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                if (mob.world.rand.nextBoolean()) {
                    p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0));
                } else {
                    p.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100, 0));
                }
            }
        }),
        ETHEREAL_BLADE((mob, tier) -> {
            int duration = 20 * (8 + tier);
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, 1));
            double radius = 4.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, duration, 1));
            }
        }),
        CHILLING_TOUCH((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120 + tier * 10, 2));
                p.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 120 + tier * 10, 1));
            }
        }),
        PIERCING_GALE((mob, tier) -> {
            double radius = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 1, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 1, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.causeMobDamage(mob), 2.0F * tier);
                p.motionX += (p.posX - mob.posX) * 0.6;
                p.motionZ += (p.posZ - mob.posZ) * 0.6;
            }
        }),
        NATURES_WRATH((mob, tier) -> {
            double radius = 5.5;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.POISON, 100 + tier * 10, 1));
            }
        }),
        SOLAR_BEAM((mob, tier) -> {
            double radius = 7.0;
            List<EntityPlayer> players = mob.world.getEntitiesWithinAABB(EntityPlayer.class,
                    new AxisAlignedBB(
                            mob.posX - radius, mob.posY - 3, mob.posZ - radius,
                            mob.posX + radius, mob.posY + 3, mob.posZ + radius));
            if (!players.isEmpty()) {
                EntityPlayer target = players.get(mob.world.rand.nextInt(players.size()));
                mob.world.addWeatherEffect(new EntityLightningBolt(mob.world, target.posX, target.posY, target.posZ, false));
            }
        }),
        LUNAR_BLESSING((mob, tier) -> {
            int duration = 20 * (15 + tier * 2);
            mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, 1));
            mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, duration, tier));
        }),
        STEEL_FORTRESS((mob, tier) -> {
            int duration = 20 * (10 + tier * 2);
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, tier));
        }),
        BLOOD_RITUAL((mob, tier) -> {
            float sacrifice = 2.0F * tier;
            mob.attackEntityFrom(DamageSource.MAGIC, sacrifice);
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.causeMobDamage(mob), sacrifice);
            }
            mob.heal(sacrifice);
        }),
        SPIRIT_LINK((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                float damage = 2.0F * tier;
                p.attackEntityFrom(DamageSource.causeMobDamage(mob), damage);
            }
        }),
        // Additional 6 new abilities to reach 25 new abilities.
        SUNDRY_FLARE((mob, tier) -> {
            double radius = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.setFire(3);
                p.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 50 + tier * 5, 0));
            }
        }),
        COSMIC_DUST((mob, tier) -> {
            double radius = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 1, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 1, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                if (rand.nextBoolean()) {
                    p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 80 + tier * 5, 0));
                } else {
                    p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80 + tier * 5, 1));
                }
            }
        }),
        EARTHEN_FURY((mob, tier) -> {
            double radius = 4.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 2, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 2, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.GENERIC, 3.0F * tier);
            }
        }),
        WIND_REND((mob, tier) -> {
            double radius = 5.5;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - radius, mob.posY - 1, mob.posZ - radius,
                    mob.posX + radius, mob.posY + 1, mob.posZ + radius);
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.motionX += (p.posX - mob.posX) * 0.8;
                p.motionZ += (p.posZ - mob.posZ) * 0.8;
            }
        }),
        SHATTERING_BOOM((mob, tier) -> {
            if (!mob.world.isRemote) {
                mob.world.createExplosion(mob, mob.posX, mob.posY, mob.posZ, 3.0F + tier, false);
            }
        }),
        MYSTIC_BARRIER((mob, tier) -> {
            int duration = 20 * (12 + tier);
            mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, duration, tier));
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, tier));
        });

        // End of enum constants.

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