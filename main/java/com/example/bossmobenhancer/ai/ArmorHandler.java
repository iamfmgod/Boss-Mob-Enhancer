package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.utils.EnchantmentUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ArmorHandler {
    public static void applyArmor(EntityLiving mob, int tier) {
        ItemStack chestplate = ItemStack.EMPTY;

        switch (tier) {
            case 1: chestplate = new ItemStack(Items.LEATHER_CHESTPLATE); break;
            case 2: chestplate = new ItemStack(Items.IRON_CHESTPLATE); break;
            case 3: chestplate = new ItemStack(Items.GOLDEN_CHESTPLATE); break;
            case 4: chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE); break;
        }

        if (!chestplate.isEmpty()) {
            EnchantmentUtils.applyBalancedEnchantments(chestplate, tier);
            mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestplate);
        }
    }
}