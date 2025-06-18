package com.example.bossmobenhancer.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigHandler {

    private static Configuration config;

    public static Configuration getConfig() {
        if (config == null) {
            throw new IllegalStateException("Config not initialized. Call ConfigHandler.init() first.");
        }
        return config;
    }

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
    public static List<String> whitelistMobs = Collections.emptyList();
    public static List<String> blacklistMobs = Collections.emptyList();

    // Minion Settings
    public static int baseMinionSpawnChance;
    public static int maxMinionsPerSpawn;

    // Display Settings
    public static boolean showBossNamePlate;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        config.load();

        // General
        enableScaling = config.getBoolean(
                "Enable Scaling", "General", true,
                "Allow mobs to scale difficulty dynamically."
        );

        enhancementChance = config.getFloat(
                "Enhancement Chance", "General", 0.5f, 0f, 1f,
                "Chance (0.0 to 1.0) that an eligible mob will be enhanced."
        );

        difficultyPreset = config.getString(
                "Difficulty Curve Preset", "Scaling", "Balanced",
                "Choose a scaling profile: Balanced, Aggressive, Brutal, or Custom."
        );

        // Weights
        healthWeight = config.getFloat(
                "Health Weight", "Scaling", 1.0f, 0f, 5f,
                "Multiplier for mob health influence on tier calculation (Custom preset only)."
        );

        playerCountWeight = config.getFloat(
                "Player Count Weight", "Scaling", 1.0f, 0f, 5f,
                "Multiplier for player count influence on tier calculation (Custom preset only)."
        );

        maxTier = config.getInt(
                "Max Tier", "Scaling", 4, 1, 5,
                "Maximum boss tier allowed."
        );

        // Modules
        enableTerrain = config.getBoolean(
                "Enable Terrain Effects", "Abilities", true,
                "Allow bosses to alter terrain (firestorms, traps, etc.)."
        );

        enableMinions = config.getBoolean(
                "Enable Minions", "Abilities", true,
                "Allow bosses to summon allied mobs during combat."
        );

        enableBuffs = config.getBoolean(
                "Enable Buff Potions", "Abilities", true,
                "Allow bosses to receive potion-based combat enhancements."
        );

        // Filters
        whitelistMobs = Arrays.asList(config.getStringList(
                "Mob Whitelist", "Entities", new String[]{},
                "Only apply enhancements to these mobs. Leave blank to allow all."
        ));

        blacklistMobs = Arrays.asList(config.getStringList(
                "Mob Blacklist", "Entities", new String[]{},
                "Prevent these specific mobs from being enhanced."
        ));

        // Minion Settings
        baseMinionSpawnChance = config.getInt(
                "Base Minion Spawn Chance", "Minions", 20, 0, 100,
                "Base chance (in percent) for bosses to spawn minions."
        );

        maxMinionsPerSpawn = config.getInt(
                "Max Minions Per Spawn", "Minions", 5, 1, 20,
                "Maximum number of minions a boss can spawn at once."
        );

        // Display Settings
        showBossNamePlate = config.getBoolean(
                "Show Boss Nameplates", "Display", true,
                "Toggle visibility of boss nameplates."
        );

        if (config.hasChanged()) {
            config.save();
        }
    }
}