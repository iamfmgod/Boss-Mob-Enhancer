package com.example.bossmobenhancer.ai;

import net.minecraft.entity.EntityLiving;
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
                // Fire abilities
                case "FLAME_WHIRLWIND":
                case "EMBER_BURST":
                case "SOLAR_FLARE":
                case "INFERNO":
                case "SUNDRY_FLARE":
                    return "fire";
                // Ice abilities
                case "FROST_NOVA":
                case "CHILLING_TOUCH":
                case "GLACIAL_PRISON":
                case "HAILSTORM":
                case "PERMAFROST":
                case "ICE_SPIKES":
                case "FROZEN_ARMOR":
                case "ABSOLUTE_ZERO":
                case "SHATTER":
                case "SNOWBLIND":
                case "WINTERS_GRASP":
                case "FROSTBITE":
                    return "ice";
                // Poison/Decay abilities
                case "WITHER_AURA":
                case "POISON_CLOUD":
                case "VENOMOUS_CLOUD":
                case "NATURES_WRATH":
                case "TOXIC_SPIT":
                case "PLAGUE_AURA":
                case "BLIGHT":
                case "NECROTIC_TOUCH":
                case "PESTILENCE":
                case "CORROSIVE_SPRAY":
                case "ROT":
                case "MIASMA":
                    return "decay";
                // Storm abilities
                case "LIGHTNING_STRIKE":
                case "STORM_CALL":
                case "SOLAR_BEAM":
                case "THUNDER_CLAP":
                case "STATIC_FIELD":
                case "CHAIN_LIGHTNING":
                case "WIND_BURST":
                case "EYE_OF_THE_STORM":
                case "TEMPEST_SHIELD":
                case "DOWNPOUR":
                case "GALE_FORCE":
                    return "storm";
                // Utility/Shadow abilities
                case "SHADOW_VEIL":
                case "MIRROR_IMAGE":
                case "BLINK_STEP":
                case "PHANTOM_DASH":
                    return "shade";
                // Defensive abilities
                case "ARCANE_SHIELD":
                case "HEALTH_BOOST":
                case "LUNAR_BLESSING":
                case "STEEL_FORTRESS":
                case "DIVINE_AEGIS":
                case "STONEFORM":
                case "BULWARK":
                case "GUARDIANS_GRACE":
                case "IRON_SKIN":
                case "MAGIC_WARD":
                case "FORTITUDE":
                case "LAST_STAND":
                case "SANCTUARY":
                case "REFLECTIVE_BARRIER":
                    return "defense";
                // Physical abilities
                case "EARTHQUAKE":
                case "FORCE_WAVE":
                case "RAGING_BLOW":
                case "FURY_RUSH":
                case "SHOCKWAVE":
                case "VOID_RIFT":
                case "GRAVITY_WELL":
                case "ARCANE_MYSTERY":
                case "ETHEREAL_BLADE":
                case "PIERCING_GALE":
                case "SILENCE":
                case "CROWSTORM":
                    return "physical";
                // Web/Time abilities
                case "WEB_SPAWN":
                case "TIME_DILATION":
                    return "utility";
            }
        }
        return "neutral";
    }

    /**
     * Defines each special ability. All logic.run(...) calls execute SERVER‐SIDE only.
     */
    public enum Ability {
        FLAME_WHIRLWIND(FireAbilities::flameWhirlwind),
        EMBER_BURST(FireAbilities::emberBurst),
        SOLAR_FLARE(FireAbilities::solarFlare),
        INFERNO(FireAbilities::inferno),
        SUNDRY_FLARE(FireAbilities::solarFlare),

        FROST_NOVA(IceAbilities::frostNova),
        CHILLING_TOUCH(IceAbilities::chillingTouch),
        GLACIAL_PRISON(IceAbilities::glacialPrison),
        HAILSTORM(IceAbilities::hailstorm),
        PERMAFROST(IceAbilities::permafrost),
        ICE_SPIKES(IceAbilities::iceSpikes),
        FROZEN_ARMOR(IceAbilities::frozenArmor),
        ABSOLUTE_ZERO(IceAbilities::absoluteZero),
        SHATTER(IceAbilities::shatter),
        SNOWBLIND(IceAbilities::snowblind),
        WINTERS_GRASP(IceAbilities::wintersGrasp),
        FROSTBITE(IceAbilities::frostbite),

        WITHER_AURA(PoisonAbilities::witherAura),
        POISON_CLOUD(PoisonAbilities::poisonCloud),
        VENOMOUS_CLOUD(PoisonAbilities::venomousCloud),
        NATURES_WRATH(PoisonAbilities::naturesWrath),
        TOXIC_SPIT(PoisonAbilities::toxicSpit),
        PLAGUE_AURA(PoisonAbilities::plagueAura),
        BLIGHT(PoisonAbilities::blight),
        NECROTIC_TOUCH(PoisonAbilities::necroticTouch),
        PESTILENCE(PoisonAbilities::pestilence),
        CORROSIVE_SPRAY(PoisonAbilities::corrosiveSpray),
        ROT(PoisonAbilities::rot),
        MIASMA(PoisonAbilities::miasma),

        LIGHTNING_STRIKE(StormAbilities::lightningStrike),
        STORM_CALL(StormAbilities::stormCall),
        SOLAR_BEAM(StormAbilities::solarBeam),
        THUNDER_CLAP(StormAbilities::thunderClap),
        STATIC_FIELD(StormAbilities::staticField),
        CHAIN_LIGHTNING(StormAbilities::chainLightning),
        WIND_BURST(StormAbilities::windBurst),
        EYE_OF_THE_STORM(StormAbilities::eyeOfTheStorm),
        TEMPEST_SHIELD(StormAbilities::tempestShield),
        DOWNPOUR(StormAbilities::downpour),
        GALE_FORCE(StormAbilities::galeForce),

        BLINK_STEP(UtilityAbilities::blinkStep),
        WEB_SPAWN(UtilityAbilities::webSpawn),
        MIRROR_IMAGE(UtilityAbilities::mirrorImage),
        PHANTOM_DASH(UtilityAbilities::phantomDash),
        TIME_DILATION(UtilityAbilities::timeDilation),

        HEALTH_BOOST(DefensiveAbilities::healthBoost),
        ARCANE_SHIELD(DefensiveAbilities::arcaneShield),
        LUNAR_BLESSING(DefensiveAbilities::lunarBlessing),
        STEEL_FORTRESS(DefensiveAbilities::steelFortress),
        DIVINE_AEGIS(DefensiveAbilities::divineAegis),
        STONEFORM(DefensiveAbilities::stoneform),
        BULWARK(DefensiveAbilities::bulwark),
        GUARDIANS_GRACE(DefensiveAbilities::guardiansGrace),
        IRON_SKIN(DefensiveAbilities::ironSkin),
        MAGIC_WARD(DefensiveAbilities::magicWard),
        FORTITUDE(DefensiveAbilities::fortitude),
        LAST_STAND(DefensiveAbilities::lastStand),
        SANCTUARY(DefensiveAbilities::sanctuary),
        REFLECTIVE_BARRIER(DefensiveAbilities::reflectiveBarrier),

        EARTHQUAKE(PhysicalAbilities::earthquake),
        FORCE_WAVE(PhysicalAbilities::forceWave),
        RAGING_BLOW(PhysicalAbilities::ragingBlow),
        FURY_RUSH(PhysicalAbilities::furyRush),
        SHOCKWAVE(PhysicalAbilities::shockwave),
        VOID_RIFT(PhysicalAbilities::voidRift),
        GRAVITY_WELL(PhysicalAbilities::gravityWell),
        ARCANE_MYSTERY(PhysicalAbilities::arcaneMystery),
        ETHEREAL_BLADE(PhysicalAbilities::etherealBlade),
        PIERCING_GALE(PhysicalAbilities::piercingGale),
        SILENCE(PhysicalAbilities::silence),
        CROWSTORM(PhysicalAbilities::crowstorm),
        SHADOW_VEIL(PhysicalAbilities::shadowVeil);

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