package com.example.bossmobenhancer.data;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and applies boss scaling profiles from JSON.
 */
public class ScalingProfileLoader {
    private static final Logger LOGGER = LogManager.getLogger("BossMobEnhancer");

    public static class Profile {
        public double healthWeight = 1.0d;
        public double playerCountWeight = 1.0d;
        public int    maxTier           = 4;
    }

    /** All loaded profiles. */
    public static Map<String, Profile> profiles = new HashMap<>();

    /** Currently active profile. */
    public static Profile active = new Profile();

    /**
     * SERVER-ONLY: Load profiles from configDir/bossmobenhancer/scaling_profiles.json.
     */
    public static void load(File configDir) {
        File file = new File(configDir, "bossmobenhancer/scaling_profiles.json");
        if (!file.exists()) {
            LOGGER.info("No scaling_profiles.json found. Using default profile.");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<Map<String, Profile>>() {}.getType();
            Map<String, Profile> map = new Gson().fromJson(reader, type);

            if (map != null) {
                profiles = map;
                LOGGER.info("Successfully loaded {} scaling profiles.", profiles.size());
            } else {
                LOGGER.warn("scaling_profiles.json is empty or malformed. Using default profile.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load scaling_profiles.json", e);
        }
    }

    /**
     * Get a named profile or return a fresh default if missing.
     */
    public static Profile get(String name) {
        return profiles.getOrDefault(name, new Profile());
    }

    /**
     * Apply the named profile immediately (server-side). Also syncs into ConfigHandler.
     */
    public static void applyProfile(String name) {
        Profile p = get(name);
        active = p;

        // sync into the config handler so everything downstream uses the new weights
        ConfigHandler.healthWeight      = p.healthWeight;
        ConfigHandler.playerCountWeight = p.playerCountWeight;
        ConfigHandler.maxTier           = p.maxTier;

        LOGGER.info("Applied scaling profile '{}': healthWeight={}, playerCountWeight={}, maxTier={}",
                name, p.healthWeight, p.playerCountWeight, p.maxTier);
    }
}