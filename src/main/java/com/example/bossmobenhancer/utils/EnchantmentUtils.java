package com.example.bossmobenhancer.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnchantmentUtils {
    private static final Random rand = new Random();

    /**
     * Applies a generic set of balanced enchantments.
     * Uses Protection, Fire Protection, and Thorns.
     *
     * @param item The item to enchant.
     * @param tier The boss tier.
     */
    public static void applyBalancedEnchantments(ItemStack item, int tier) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantments.PROTECTION, weightedRandom(tier, 3));
        enchants.put(Enchantments.FIRE_PROTECTION, weightedRandom(tier, 2));
        enchants.put(Enchantments.THORNS, weightedRandom(tier, 1));
        EnchantmentHelper.setEnchantments(enchants, item);
    }

    /**
     * Applies armor-specific balanced enchantments.
     * Ensures that the item is armor, then applies Protection, Fire Protection,
     * Blast Protection, and Thorns.
     *
     * @param item The armor item to enchant.
     * @param tier The boss tier.
     */
    public static void applyBalancedArmorEnchantments(ItemStack item, int tier) {
        if (!(item.getItem() instanceof ItemArmor)) {
            return; // Only applies to armor.
        }
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantments.PROTECTION, weightedRandom(tier, 4));
        enchants.put(Enchantments.FIRE_PROTECTION, weightedRandom(tier, 3));
        enchants.put(Enchantments.BLAST_PROTECTION, weightedRandom(tier, 3));
        enchants.put(Enchantments.THORNS, weightedRandom(tier, 1));
        EnchantmentHelper.setEnchantments(enchants, item);
    }

    /**
     * Applies weapon-specific aggressive enchantments.
     * Adds Sharpness, Smite, Fire Aspect, and Knockback to enhance offensive power.
     *
     * @param item The weapon item to enchant.
     * @param tier The boss tier.
     */
    public static void applyAggressiveWeaponEnchantments(ItemStack item, int tier) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantments.SHARPNESS, weightedRandom(tier, 5));
        enchants.put(Enchantments.SMITE, weightedRandom(tier, 3));
        enchants.put(Enchantments.FIRE_ASPECT, weightedRandom(tier, 2));
        enchants.put(Enchantments.KNOCKBACK, weightedRandom(tier, 2));
        EnchantmentHelper.setEnchantments(enchants, item);
    }

    /**
     * Optionally applies a random selection of enchantments to the item.
     * This method randomly chooses between the balanced and aggressive sets.
     *
     * @param item The item to enchant.
     * @param tier The boss tier.
     */
    public static void applyRandomEnchantments(ItemStack item, int tier) {
        if (rand.nextBoolean()) {
            applyBalancedEnchantments(item, tier);
        } else {
            applyAggressiveWeaponEnchantments(item, tier);
        }
    }

    /**
     * Returns a weighted random level for an enchantment.
     * It uses a basic random value influenced by the tier and maximum allowed level,
     * ensuring the value is at least 1 and does not exceed maxLevel.
     *
     * @param tier     The boss tier.
     * @param maxLevel The maximum level for the enchantment.
     * @return An integer level between 1 and maxLevel.
     */
    private static int weightedRandom(int tier, int maxLevel) {
        // Generate a value in [0, tier + maxLevel), divide by 2 for bias,
        // then ensure the level is within [1, maxLevel].
        int level = rand.nextInt(tier + maxLevel) / 2;
        return Math.max(1, Math.min(level, maxLevel));
    }
}