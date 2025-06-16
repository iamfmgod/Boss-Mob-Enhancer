package com.example.bossmobenhancer.utils;

import net.minecraft.entity.EntityLiving;

public class NameGenerator {

    /**
     * Checks if the provided name fits the boss name patterns.
     * Boss names are expected to begin with one of the following prefixes:
     *  - "§7Wanderer "
     *  - "§7Challenger "
     *  - "§7Champion "
     *  - "§7Overlord "
     *  - "§7Eclipsebound "
     *  - "§6Lord " (for stronger bosses)
     *
     * As a fallback, names that start with a common color code (e.g., §a, §b, §c, or §e, case-insensitive)
     * are also accepted if followed by additional characters.
     *
     * @param name The custom name to check.
     * @return true if the name matches a known boss naming pattern.
     */
    public static boolean isBossName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        if (name.startsWith("§7Wanderer ") ||
                name.startsWith("§7Challenger ") ||
                name.startsWith("§7Champion ") ||
                name.startsWith("§7Overlord ") ||
                name.startsWith("§7Eclipsebound ") ||
                name.startsWith("§6Lord ")) {
            return true;
        }

        // Fallback: match any name that begins with a color code (§a, §b, §c, or §e) followed by additional text.
        return name.matches("^§[abceABCE].+");
    }

    /**
     * Optionally, generate a boss name by randomly selecting from defined prefixes and appending a custom suffix.
     * This method can help ensure consistency between name creation and your boss name validation logic.
     *
     * @return a generated boss name.
     */
    public static String generateBossName() {
        String[] prefixes = {
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

    /**
     * Generates a random suffix for a boss name.
     * In this simple example, it appends a random number, but you can replace this with a more creative method.
     *
     * @return a string suffix.
     */
    private static String generateRandomSuffix() {
        int number = 100 + (int)(Math.random() * 900);
        return "The " + number;
    }

    /**
     * Generates a fantasy boss name that incorporates data from the boss mob and its tier,
     * providing a themed name.
     *
     * @param mob  The boss entity.
     * @param tier The tier of the boss.
     * @return A fantasy name that includes a base name and tier information.
     */
    public static String generateFantasyName(EntityLiving mob, int tier) {
        String baseName = generateBossName();
        // Optionally you can incorporate mob-specific data here
        return baseName + " [Tier " + tier + "]";
    }
}