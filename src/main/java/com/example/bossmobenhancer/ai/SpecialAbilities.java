package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.*;

/**
 * Applies and manages special‐ability loadouts on boss mobs.
 * All actual ability effects run SERVER‐SIDE only.
 * getPersonalityElement() may be called client or server.
 */
public class SpecialAbilities {
    private static final Random RAND = new Random();
    private static final String NBT_KEY = "bossmobenhancer.loadout";

    private static final Map<String, List<Ability>> presetLoadouts = new HashMap<>();
    static {
        presetLoadouts.put("Flame Lord", Arrays.asList(
                Ability.FLAME_WHIRLWIND,
                Ability.EMBER_BURST,
                Ability.SOLAR_FLARE
        ));
        presetLoadouts.put("Frost King", Arrays.asList(
                Ability.FROST_NOVA,
                Ability.ARCANE_SHIELD,
                Ability.EARTHQUAKE
        ));
    }

    /**
     * Picks a preset or 3 random abilities and applies them.
     * This entire method is SERVER‐ONLY.
     */
    public static void applyLoadout(EntityLiving mob, int tier) {
        World world = mob.world;
        if (world == null || world.isRemote) return;

        // choose preset vs random
        List<Ability> selected;
        String name = mob.getCustomNameTag();
        if (mob.hasCustomName() && presetLoadouts.containsKey(name)) {
            selected = presetLoadouts.get(name);
        } else {
            List<Ability> pool = new ArrayList<>(Arrays.asList(Ability.values()));
            Collections.shuffle(pool, RAND);
            selected = pool.subList(0, Math.min(3, pool.size()));
        }

        // store in NBT
        StringBuilder sb = new StringBuilder();
        for (Ability a : selected) sb.append(a.name()).append(",");
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        mob.getEntityData().setString(NBT_KEY, sb.toString());

        // apply each ability
        for (String token : sb.toString().split(",")) {
            try {
                Ability.valueOf(token).apply(mob, tier);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    /**
     * Can run client or server; purely reads NBT to return a simple element tag.
     */
    public static String getPersonalityElement(EntityLiving mob) {
        if (!mob.getEntityData().hasKey(NBT_KEY)) return "neutral";
        for (String trait : mob.getEntityData().getString(NBT_KEY).split(",")) {
            switch (trait) {
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
                case "SOLAR_BEAM":
                    return "storm";
                case "SHADOW_VEIL":
                case "MIRROR_IMAGE":
                    return "shade";
            }
        }
        return "neutral";
    }

    /**
     * Defines each special ability. All logic.run(...) calls execute SERVER‐SIDE only.
     */
    public enum Ability {
        FLAME_WHIRLWIND((mob, tier) -> {
            double r = 5.0 + tier;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.setFire(5);
                p.attackEntityFrom(DamageSource.IN_FIRE, 4.0F * tier);
            }
        }),

        EMBER_BURST((mob, tier) -> {
            double r = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.setFire(4);
            }
        }),

        SOLAR_FLARE((mob, tier) -> {
            double r = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.BLINDNESS, 60 + tier * 10, 0
                ));
            }
        }),

        WITHER_AURA((mob, tier) -> {
            double r = 4.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.WITHER, 100 + tier * 20, 1
                ));
            }
        }),

        POISON_CLOUD((mob, tier) -> {
            double r = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.POISON, 100 + tier * 10, 1
                ));
            }
        }),

        WEB_SPAWN((mob, tier) -> {
            double r = 3.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 1, mob.posZ - r,
                    mob.posX + r, mob.posY + 1, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 2));
            }
        }),

        LIGHTNING_STRIKE((mob, tier) -> {
            double r = 8.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 3, mob.posZ - r,
                    mob.posX + r, mob.posY + 3, mob.posZ + r
            );
            List<EntityPlayer> players = mob.world.getEntitiesWithinAABB(EntityPlayer.class, area);
            if (!players.isEmpty() && RAND.nextFloat() < 0.5f) {
                EntityPlayer target = players.get(RAND.nextInt(players.size()));
                mob.world.addWeatherEffect(new EntityLightningBolt(
                        mob.world, target.posX, target.posY, target.posZ, false
                ));
            }
        }),

        BLINK_STEP((mob, tier) -> {
            double range = 4.0;
            double newX = mob.posX + (mob.world.rand.nextDouble() * range - range / 2);
            double newZ = mob.posZ + (mob.world.rand.nextDouble() * range - range / 2);
            mob.setPositionAndUpdate(newX, mob.posY, newZ);
            mob.addPotionEffect(new PotionEffect(
                    MobEffects.INVISIBILITY, 40 + 10 * tier, 0
            ));
        }),

        HEALTH_BOOST((mob, tier) -> {
            double boost = 10.0 * tier;
            mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                    .setBaseValue(mob.getMaxHealth() + boost);
            mob.setHealth(mob.getMaxHealth());
        }),

        RAGING_BLOW((mob, tier) -> {
            int dur = 20 * (10 + tier * 2);
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, dur, tier));
        }),

        SHADOW_VEIL((mob, tier) -> {
            mob.addPotionEffect(new PotionEffect(
                    MobEffects.INVISIBILITY, 60 + tier * 10, 0
            ));
            mob.addPotionEffect(new PotionEffect(
                    MobEffects.SPEED, 60 + tier * 10, 1
            ));
        }),

        FROST_NOVA((mob, tier) -> {
            double r = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.SLOWNESS, 100 + tier * 10, 2
                ));
                p.addPotionEffect(new PotionEffect(
                        MobEffects.MINING_FATIGUE, 100 + tier * 10, 1
                ));
            }
        }),

        EARTHQUAKE((mob, tier) -> {
            double r = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.GENERIC, 4.0F * tier);
                p.motionX += (p.posX - mob.posX) * 0.5;
                p.motionZ += (p.posZ - mob.posZ) * 0.5;
            }
        }),

        ARCANE_SHIELD((mob, tier) -> {
            int dur = 20 * (10 + tier * 2);
            mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, dur, tier));
        }),

        VENOMOUS_CLOUD((mob, tier) -> {
            double r = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.POISON, 120 + tier * 10, 1
                ));
                p.addPotionEffect(new PotionEffect(
                        MobEffects.SLOWNESS, 120 + tier * 10, 2
                ));
            }
        }),

        STORM_CALL((mob, tier) -> {
            double r = 8.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 3, mob.posZ - r,
                    mob.posX + r, mob.posY + 3, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                mob.world.addWeatherEffect(new EntityLightningBolt(
                        mob.world, p.posX, p.posY, p.posZ, false
                ));
            }
        }),

        FORCE_WAVE((mob, tier) -> {
            double r = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.motionX += (p.posX - mob.posX) * 0.7;
                p.motionZ += (p.posZ - mob.posZ) * 0.7;
            }
        }),

        MIRROR_IMAGE((mob, tier) -> {
            double r = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.NAUSEA, 80 + tier * 10, 0
                ));
            }
        }),

        FURY_RUSH((mob, tier) -> {
            int d = 20 * (10 + tier * 2);
            mob.addPotionEffect(new PotionEffect(
                    MobEffects.SPEED, d, 1
            ));
            mob.addPotionEffect(new PotionEffect(
                    MobEffects.STRENGTH, d, 1
            ));
        }),

        TIME_DILATION((mob, tier) -> {
            double r = 7.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.SLOWNESS, 100 + tier * 10, 2
                ));
            }
        }),

        INFERNO((mob, tier) -> {
            double r = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.setFire(6);
                p.attackEntityFrom(DamageSource.IN_FIRE, 5.0F * tier);
            }
        }),

        SHOCKWAVE((mob, tier) -> {
            double r = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 1, mob.posZ - r,
                    mob.posX + r, mob.posY + 1, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.causeMobDamage(mob), 3.0F * tier);
                p.motionX += (p.posX - mob.posX) * 0.8;
                p.motionZ += (p.posZ - mob.posZ) * 0.8;
            }
        }),

        SILENCE((mob, tier) -> {
            double r = 4.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 1, mob.posZ - r,
                    mob.posX + r, mob.posY + 1, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.MINING_FATIGUE, 100 + tier * 10, 2
                ));
            }
        }),

        CROWSTORM((mob, tier) -> {
            double r = 8.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 3, mob.posZ - r,
                    mob.posX + r, mob.posY + 3, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.NAUSEA, 80 + tier * 10, 0
                ));
            }
        }),

        GRAVITY_WELL((mob, tier) -> {
            double r = 7.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                double dx = mob.posX - p.posX;
                double dz = mob.posZ - p.posZ;
                p.motionX += dx * 0.05;
                p.motionZ += dz * 0.05;
            }
        }),

        PHANTOM_DASH((mob, tier) -> {
            double range = 5.0;
            double x = mob.posX + (mob.world.rand.nextDouble() * range - range / 2);
            double z = mob.posZ + (mob.world.rand.nextDouble() * range - range / 2);
            mob.setPositionAndUpdate(x, mob.posY, z);
        }),

        VOID_RIFT((mob, tier) -> {
            mob.world.createExplosion(mob, mob.posX, mob.posY, mob.posZ, 2.0F + tier, false);
        }),

        ARCANE_MYSTERY((mob, tier) -> {
            double r = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                if (RAND.nextBoolean()) {
                    p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0));
                } else {
                    p.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100, 0));
                }
            }
        }),

        ETHEREAL_BLADE((mob, tier) -> {
            int d = 20 * (8 + tier);
            mob.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, d, 1));
            double r = 4.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, d, 1));
            }
        }),

        CHILLING_TOUCH((mob, tier) -> {
            double r = 5.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 2, mob.posZ - r,
                    mob.posX + r, mob.posY + 2, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.addPotionEffect(new PotionEffect(
                        MobEffects.SLOWNESS, 120 + tier * 10, 2
                ));
                p.addPotionEffect(new PotionEffect(
                        MobEffects.MINING_FATIGUE, 120 + tier * 10, 1
                ));
            }
        }),

        PIERCING_GALE((mob, tier) -> {
            double r = 6.0;
            AxisAlignedBB area = new AxisAlignedBB(
                    mob.posX - r, mob.posY - 1, mob.posZ - r,
                    mob.posX + r, mob.posY + 1, mob.posZ + r
            );
            for (EntityPlayer p : mob.world.getEntitiesWithinAABB(EntityPlayer.class, area)) {
                p.attackEntityFrom(DamageSource.causeMobDamage(mob), 2.0F * tier);
                p.motionX += (p.posX - mob.posX) * 0.6;
                p.motionZ += (p.posZ - mob.posZ) * 0.6;
            }
        }),

        NATURES_WRATH((mob,tier)->{
            double r=5.5;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-2,mob.posZ-r,
                    mob.posX+r,mob.posY+2,mob.posZ+r
            );
            for(EntityPlayer p:mob.world.getEntitiesWithinAABB(EntityPlayer.class,area)){
                p.addPotionEffect(new PotionEffect(MobEffects.POISON,100+tier*10,1));
            }
        }),

        SOLAR_BEAM((mob,tier)->{
            double r=7.0;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-3,mob.posZ-r,
                    mob.posX+r,mob.posY+3,mob.posZ+r
            );
            List<EntityPlayer> ps=mob.world.getEntitiesWithinAABB(EntityPlayer.class,area);
            if(!ps.isEmpty()){
                EntityPlayer t=ps.get(RAND.nextInt(ps.size()));
                mob.world.addWeatherEffect(new EntityLightningBolt(
                        mob.world,t.posX,t.posY,t.posZ,false
                ));
            }
        }),

        LUNAR_BLESSING((mob,tier)->{
            int d=20*(15+tier*2);
            mob.addPotionEffect(new PotionEffect(MobEffects.REGENERATION,d,1));
            mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION,d,tier));
        }),

        STEEL_FORTRESS((mob,tier)->{
            int d=20*(10+tier*2);
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,d,tier));
        }),

        BLOOD_RITUAL((mob,tier)->{
            float sac=2.0F*tier;
            mob.attackEntityFrom(DamageSource.MAGIC,sac);
            double r=5.0;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-2,mob.posZ-r,
                    mob.posX+r,mob.posY+2,mob.posZ+r
            );
            for(EntityPlayer p:mob.world.getEntitiesWithinAABB(EntityPlayer.class,area)){
                p.attackEntityFrom(DamageSource.causeMobDamage(mob),sac);
            }
            mob.heal(sac);
        }),

        SPIRIT_LINK((mob,tier)->{
            double r=5.0;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-2,mob.posZ-r,
                    mob.posX+r,mob.posY+2,mob.posZ+r
            );
            for(EntityPlayer p:mob.world.getEntitiesWithinAABB(EntityPlayer.class,area)){
                p.attackEntityFrom(DamageSource.causeMobDamage(mob),2.0F*tier);
            }
        }),

        SUNDRY_FLARE((mob,tier)->{
            double r=6.0;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-2,mob.posZ-r,
                    mob.posX+r,mob.posY+2,mob.posZ+r
            );
            for(EntityPlayer p:mob.world.getEntitiesWithinAABB(EntityPlayer.class,area)){
                p.setFire(3);
                p.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS,50+tier*5,0));
            }
        }),

        COSMIC_DUST((mob,tier)->{
            double r=5.0;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-1,mob.posZ-r,
                    mob.posX+r,mob.posY+1,mob.posZ+r
            );
            for(EntityPlayer p:mob.world.getEntitiesWithinAABB(EntityPlayer.class,area)){
                if(RAND.nextBoolean()) p.addPotionEffect(new PotionEffect(MobEffects.REGENERATION,80+tier*5,0));
                else                    p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,80+tier*5,1));
            }
        }),

        EARTHEN_FURY((mob,tier)->{
            double r=4.0;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-2,mob.posZ-r,
                    mob.posX+r,mob.posY+2,mob.posZ+r
            );
            for(EntityPlayer p:mob.world.getEntitiesWithinAABB(EntityPlayer.class,area)){
                p.attackEntityFrom(DamageSource.GENERIC,3.0F*tier);
            }
        }),

        WIND_REND((mob,tier)->{
            double r=5.5;
            AxisAlignedBB area=new AxisAlignedBB(
                    mob.posX-r,mob.posY-1,mob.posZ-r,
                    mob.posX+r,mob.posY+1,mob.posZ+r
            );
            for(EntityPlayer p:mob.world.getEntitiesWithinAABB(EntityPlayer.class,area)){
                p.motionX+=(p.posX-mob.posX)*0.8;
                p.motionZ+=(p.posZ-mob.posZ)*0.8;
            }
        }),

        SHATTERING_BOOM((mob,tier)->mob.world.createExplosion(mob,mob.posX,mob.posY,mob.posZ,3.0F+tier,false)),

        MYSTIC_BARRIER((mob,tier)->{
            int d=20*(12+tier);
            mob.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION,d,tier));
            mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,d,tier));
        });

        private final Action logic;
        Ability(Action logic) { this.logic=logic; }
        public void apply(EntityLiving mob,int tier) {
            // SERVER‐ONLY guard
            if (mob.world==null || mob.world.isRemote) return;
            logic.apply(mob,tier);
        }

        @FunctionalInterface
        public interface Action {
            void apply(EntityLiving mob,int tier);
        }
    }
}