package com.example.bossmobenhancer.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = "bossmobenhancer")
public class ConfigHandler {
    private static Configuration config;

    // General Settings
    public static boolean enableScaling;
    public static float enhancementChance;
    public static String difficultyPreset;

    // Scaling logic
    public static double healthWeight;
    public static double playerCountWeight;
    public static int maxTier;

    // Feature Modules
    public static boolean enableTerrain;
    public static boolean enableMinions;
    public static boolean enableBuffs;

    // Mob Filters
    public static List<String> whitelistMobs;
    public static List<String> blacklistMobs;

    // Minion Settings
    public static int baseMinionSpawnChance;
    public static int maxMinionsPerSpawn;
    public static double spawnChancePerTier;
    public static int minionTierThreshold;
    public static int minionSpawnRadius;

    // Rewards Settings
    public static float lootDropMultiplier;
    public static String[] dungeonLootTables;
    public static String[] customLootTables;
    public static String[] customLootItems;

    // Experience Settings
    public static int baseBossXpMultiplier;
    public static int lordBossXpMultiplier;

    // Display Settings
    public static boolean showBossNamePlate;

    /** Call during preInit to load config from disk. */
    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file);
        }
        load();
    }

    /** (Re)load all values from the config file. */
    public static void load() {
        config.load();

        // --- General ---
        enableScaling     = config.getBoolean(
                "Enable Scaling", "General", true,
                "Allow mobs to scale difficulty dynamically."
        );
        enhancementChance = config.getFloat(
                "Enhancement Chance", "General", 0.5f,
                0f, 1f,
                "Chance (0.0 to 1.0) that an eligible mob will be enhanced."
        );

        // --- Scaling ---
        Property pPreset = config.get(
                "Scaling", "Difficulty Curve Preset", "Balanced",
                "Balanced, Aggressive, Brutal, SkyblockFriendly, SoloSurvivor, Raider, BerserkerRush, Endwalker"
        );
        pPreset.setValidValues(new String[]{
                "Balanced","Aggressive","Brutal","SkyblockFriendly",
                "SoloSurvivor","Raider","BerserkerRush","Endwalker"
        });
        difficultyPreset   = pPreset.getString();
        healthWeight       = config.get("Scaling", "Health Weight", 1.0d).getDouble();
        playerCountWeight  = config.get("Scaling", "Player Count Weight", 1.0d).getDouble();
        maxTier            = config.getInt(
                "Max Tier", "Scaling", 4, 1, 5,
                "Maximum boss tier allowed."
        );

        // --- Abilities ---
        enableTerrain = config.getBoolean(
                "Enable Terrain Effects", "Abilities", true,
                "Allow bosses to alter terrain."
        );
        enableMinions = config.getBoolean(
                "Enable Minions", "Abilities", true,
                "Allow bosses to summon minions."
        );
        enableBuffs   = config.getBoolean(
                "Enable Buff Potions", "Abilities", true,
                "Allow bosses to buff themselves."
        );

        // --- Entities ---
        whitelistMobs = Arrays.asList(
                config.getStringList("Mob Whitelist", "Entities", new String[]{},
                        "Only enhance these mobs; leave blank for all.")
        );
        blacklistMobs = Arrays.asList(
                config.getStringList("Mob Blacklist", "Entities", new String[]{},
                        "Do not enhance these mobs.")
        );

        // --- Minions ---
        baseMinionSpawnChance = config.getInt(
                "Base Minion Spawn Chance", "Minions",
                20, 0, 100,
                "Percent chance bosses spawn minions."
        );
        maxMinionsPerSpawn = config.getInt(
                "Max Minions Per Spawn", "Minions",
                5, 1, 20,
                "Max minions at once."
        );
        // --- Minions ---
        baseMinionSpawnChance = config.getInt(
                "Base Minion Spawn Chance", "Minions",
                20, 0, 100,
                "Percent chance bosses spawn minions."
        );
        maxMinionsPerSpawn = config.getInt(
                "Max Minions Per Spawn", "Minions",
                5, 1, 20,
                "Max minions at once."
        );
        minionTierThreshold = config.getInt(
                "Minion Tier Threshold", "Minions",
                1, 0, maxTier,
                "Boss tier at or above which minions can spawn."
        );
        minionSpawnRadius = config.getInt(
                "Minion Spawn Radius", "Minions",
                16, 1, 128,
                "Blocks around boss in which minions may spawn."
        );
        minionTierThreshold = config.getInt(
                "Minion Tier Threshold", "Minions",
                1, 0, maxTier,
                "Boss tier at or above which minions can spawn."
        );
        minionSpawnRadius = config.getInt(
                "Minion Spawn Radius", "Minions",
                16, 1, 128,
                "Blocks around boss in which minions may spawn."
        );

        // --- Rewards ---
        lootDropMultiplier = config.getFloat(
                "Loot Drop Multiplier", "Rewards",
                0.25f, 0f, 1f,
                "Reduce loot for non-lords."
        );
        dungeonLootTables = config.getStringList(
                "Dungeon Loot Tables", "Rewards",
                new String[]{
                        "minecraft:chests/stronghold_library",
                        "minecraft:chests/nether_bridge",
                        "minecraft:chests/end_city_treasure"
                },
                "Vanilla loot tables to augment."
        );
        customLootTables = config.getStringList(
                "Custom Loot Tables", "Rewards", new String[]{},
                "Additional loot table IDs."
        );
        customLootItems = config.getStringList(
                "Custom Loot Items", "Rewards", new String[]{},
                "Format: modid:item,qty,chance"
        );

        // --- Experience ---
        baseBossXpMultiplier = config.getInt(
                "Base Boss XP Multiplier", "Experience",
                5, 1, 100,
                "XP multiplier for normal bosses (per tier)."
        );
        lordBossXpMultiplier = config.getInt(
                "Lord Boss XP Multiplier", "Experience",
                20, 1, 100,
                "XP multiplier for Lord-tier bosses."
        );

        // --- Display ---
        showBossNamePlate = config.getBoolean(
                "Show Boss Nameplates", "Display", true,
                "Toggle boss nameplate visibility."
        );

        if (config.hasChanged()) {
            config.save();
        }
    }

    /** Optional accessor for the raw Configuration instance. */
    public static Configuration getConfig() {
        return config;
    }

    /** Force-save the config at runtime. */
    public static void saveConfig() {
        if (config != null && config.hasChanged()) {
            config.save();
        }
    }

    /** Handle in-GUI config changes. */
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if ("bossmobenhancer".equals(event.getModID())) {
            load();
        }
    }

    // Prevent instantiation
    private ConfigHandler() { }
}