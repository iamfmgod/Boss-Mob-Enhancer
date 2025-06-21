package com.example.bossmobenhancer.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ScalingProfileLoader {
    // Profile record holds scaling parameters.
    public static class Profile {
        public float healthWeight = 1.0f;
        public float playerCountWeight = 1.0f;
        public int maxTier = 4;
    }

    // Holds the loaded profiles. Defaults to an empty map.
    public static Map<String, Profile> profiles = new HashMap<>();

    /**
     * Loads scaling profiles from the scaling_profiles.json file within the config directory.
     *
     * @param configDir The base config directory.
     */
    public static void load(File configDir) {
        File file = new File(configDir, "bossmobenhancer/scaling_profiles.json");
        if (!file.exists()) {
            System.out.println("[BossMobEnhancer] No scaling_profiles.json found. Using default profile.");
            return;
        }

        // Use try-with-resources to ensure the reader is closed automatically.
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Profile>>(){}.getType();
            Map<String, Profile> loadedProfiles = gson.fromJson(reader, type);

            if (loadedProfiles != null) {
                profiles = loadedProfiles;
                System.out.println("[BossMobEnhancer] Successfully loaded scaling profiles.");
            } else {
                System.out.println("[BossMobEnhancer] scaling_profiles.json is empty or malformed. Using default profile.");
            }
        } catch (Exception e) {
            System.err.println("[BossMobEnhancer] Failed to load scaling_profiles.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the profile associated with the given name.
     * If no profile exists, returns a default profile.
     *
     * @param name The profile name.
     * @return The associated Profile or a default Profile if not found.
     */
    public static Profile get(String name) {
        return profiles.getOrDefault(name, new Profile());
    }
}