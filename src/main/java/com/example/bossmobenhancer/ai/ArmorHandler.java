package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.utils.EnchantmentUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;

/**
 * Equips boss mobs with tiered armor sets plus balanced enchantments.
 * Optionally equips a Baubles accessory if the Baubles mod is loaded.
 */
public class ArmorHandler {

    /**
     * Give the boss a full armor set based on tier 1–4+.
     */
    public static void applyArmor(EntityLiving mob, int tier) {
        ItemStack helmet, chestplate, leggings, boots;

        if (tier <= 1) {
            helmet    = new ItemStack(Items.LEATHER_HELMET);
            chestplate= new ItemStack(Items.LEATHER_CHESTPLATE);
            leggings  = new ItemStack(Items.LEATHER_LEGGINGS);
            boots     = new ItemStack(Items.LEATHER_BOOTS);
        } else if (tier == 2) {
            helmet    = new ItemStack(Items.IRON_HELMET);
            chestplate= new ItemStack(Items.IRON_CHESTPLATE);
            leggings  = new ItemStack(Items.IRON_LEGGINGS);
            boots     = new ItemStack(Items.IRON_BOOTS);
        } else if (tier == 3) {
            helmet    = new ItemStack(Items.GOLDEN_HELMET);
            chestplate= new ItemStack(Items.GOLDEN_CHESTPLATE);
            leggings  = new ItemStack(Items.GOLDEN_LEGGINGS);
            boots     = new ItemStack(Items.GOLDEN_BOOTS);
        } else {
            helmet    = new ItemStack(Items.DIAMOND_HELMET);
            chestplate= new ItemStack(Items.DIAMOND_CHESTPLATE);
            leggings  = new ItemStack(Items.DIAMOND_LEGGINGS);
            boots     = new ItemStack(Items.DIAMOND_BOOTS);
        }

        // Apply enchantments
        EnchantmentUtils.applyBalancedEnchantments(helmet, tier);
        EnchantmentUtils.applyBalancedEnchantments(chestplate, tier);
        EnchantmentUtils.applyBalancedEnchantments(leggings, tier);
        EnchantmentUtils.applyBalancedEnchantments(boots, tier);

        // Equip
        mob.setItemStackToSlot(EntityEquipmentSlot.HEAD,   helmet);
        mob.setItemStackToSlot(EntityEquipmentSlot.CHEST,  chestplate);
        mob.setItemStackToSlot(EntityEquipmentSlot.LEGS,   leggings);
        mob.setItemStackToSlot(EntityEquipmentSlot.FEET,   boots);

        // Baubles accessory (optional)
        if (Loader.isModLoaded("baubles")) {
            applyBaubles(mob, tier);
        }
    }

    /**
     * Equip a Baubles accessory slot if Baubles is present.
     */
    @Optional.Method(modid = "baubles")
    private static void applyBaubles(EntityLiving mob, int tier) {
        ItemStack ring = new ItemStack(Items.DIAMOND);
        ring.setStackDisplayName("§dRing of Dominance (Tier " + tier + ")");
        try {
            // BaublesApi.getBaublesHandler(mob).setStackInSlot(0, ring);
        } catch (Throwable t) {
            System.err.println("[BossMobEnhancer] Failed to equip Baubles accessory:");
            t.printStackTrace();
        }
    }
}