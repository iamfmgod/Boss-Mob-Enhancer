package com.example.bossmobenhancer.utils;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;

public class NameGenerator {

    /**
     * Checks if the provided name fits the boss naming pattern.
     * Expected prefixes: "§7Wanderer ", "§7Challenger ", "§7Champion ", "§7Overlord ",
     * "§7Eclipsebound ", or "§6Lord ".
     *
     * @param name The name to check.
     * @return True if the name is a valid boss name.
     */
    public static boolean isBossName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return name.startsWith("§7Wanderer ") ||
                name.startsWith("§7Challenger ") ||
                name.startsWith("§7Champion ") ||
                name.startsWith("§7Overlord ") ||
                name.startsWith("§7Eclipsebound ") ||
                name.startsWith("§6Lord ");
    }

    /**
     * Generates a boss name using fixed color-coded prefixes and a thematic suffix.
     *
     * @return The generated boss name.
     */
    public static String generateBossName() {
        String[] prefixes = new String[] {
                "§7Wanderer ",
                "§7Challenger ",
                "§7Champion ",
                "§7Overlord ",
                "§7Eclipsebound ",
                "§6Lord "
        };
        int index = (int) (Math.random() * prefixes.length);
        return prefixes[index] + generateRandomSuffix();
    }

    private static String generateRandomSuffix() {
        String[] suffixes = new String[] {
                "of the Eclipse",
                "the Unyielding",
                "the Shadowborn",
                "the Forsaken",
                "the Eternal",
                "the Vanquisher"
        };
        int index = (int) (Math.random() * suffixes.length);
        return suffixes[index];
    }

    /**
     * Generates a fantasy boss name with tier information appended.
     *
     * @param mob  The boss entity.
     * @param tier The boss tier.
     * @return The formatted boss name.
     */
    public static String generateFantasyName(EntityLiving mob, int tier) {
        return generateBossName() + " §7[Tier " + tier + "]";
    }

    /**
     * Returns the display name for the boss.
     * If boss nameplates are turned off, returns an empty string.
     *
     * @param mob  The boss entity.
     * @param tier The boss tier.
     * @return The boss display name or an empty string.
     */
    public static String getDisplayName(EntityLiving mob, int tier) {
        if (!ConfigHandler.showBossNamePlate) {
            return "";
        }
        return generateFantasyName(mob, tier);
    }
}