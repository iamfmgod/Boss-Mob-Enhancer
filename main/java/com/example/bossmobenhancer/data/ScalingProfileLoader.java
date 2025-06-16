package com.example.bossmobenhancer.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ScalingProfileLoader {
    public static class Profile {
        public float healthWeight = 1.0f;
        public float playerCountWeight = 1.0f;
        public int maxTier = 4;
    }

    public static Map<String, Profile> profiles = new HashMap<>();

    public static void load(File configDir) {
        try {
            File file = new File(configDir, "bossmobenhancer/scaling_profiles.json");
            if (!file.exists()) return;

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, Profile>>(){}.getType();
            profiles = gson.fromJson(new FileReader(file), type);
        } catch (Exception e) {
            System.err.println("[BossMobEnhancer] Failed to load scaling_profiles.json: " + e.getMessage());
        }
    }

    public static Profile get(String name) {
        return profiles.getOrDefault(name, new Profile());
    }
}