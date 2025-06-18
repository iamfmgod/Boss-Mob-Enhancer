package com.example.bossmobenhancer.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnchantmentUtils {
    private static final Random rand = new Random();

    public static void applyBalancedEnchantments(ItemStack item, int tier) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantments.PROTECTION, weightedRandom(tier, 3));
        enchants.put(Enchantments.FIRE_PROTECTION, weightedRandom(tier, 2));
        enchants.put(Enchantments.THORNS, weightedRandom(tier, 1));
        EnchantmentHelper.setEnchantments(enchants, item);
    }

    private static int weightedRandom(int tier, int maxLevel) {
        return Math.max(1, rand.nextInt(tier + maxLevel) / 2);
    }
}