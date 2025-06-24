package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.utils.EnchantmentUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

// Optional integration with Baubles; uncomment the next line if you've added the Baubles API dependency.
// import baubles.api.BaublesApi;

/**
 * Utility class to equip boss mobs with an appropriate armor set.
 * The armor set material is determined by the boss tier:
 * - Tier 1: Leather
 * - Tier 2: Iron
 * - Tier 3: Golden
 * - Tier 4+: Diamond
 * Each piece of armor receives balanced enchantments.
 *
 * Additionally, if the Baubles mod is installed, an extra accessory is equipped.
 */
public class ArmorHandler {

    /**
     * Equips the given boss mob with a full armor set based on its tier.
     *
     * @param mob  The boss mob entity.
     * @param tier The boss tier, which scales both armor quality and enchantment strength.
     */
    public static void applyArmor(EntityLiving mob, int tier) {
        ItemStack helmet;
        ItemStack chestplate;
        ItemStack leggings;
        ItemStack boots;

        // Determine armor material based on tier.
        if (tier <= 1) {
            helmet = new ItemStack(Items.LEATHER_HELMET);
            chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
            leggings = new ItemStack(Items.LEATHER_LEGGINGS);
            boots = new ItemStack(Items.LEATHER_BOOTS);
        } else if (tier == 2) {
            helmet = new ItemStack(Items.IRON_HELMET);
            chestplate = new ItemStack(Items.IRON_CHESTPLATE);
            leggings = new ItemStack(Items.IRON_LEGGINGS);
            boots = new ItemStack(Items.IRON_BOOTS);
        } else if (tier == 3) {
            helmet = new ItemStack(Items.GOLDEN_HELMET);
            chestplate = new ItemStack(Items.GOLDEN_CHESTPLATE);
            leggings = new ItemStack(Items.GOLDEN_LEGGINGS);
            boots = new ItemStack(Items.GOLDEN_BOOTS);
        } else {
            helmet = new ItemStack(Items.DIAMOND_HELMET);
            chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
            leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
            boots = new ItemStack(Items.DIAMOND_BOOTS);
        }

        // Apply balanced enchantments based on boss tier.
        EnchantmentUtils.applyBalancedEnchantments(helmet, tier);
        EnchantmentUtils.applyBalancedEnchantments(chestplate, tier);
        EnchantmentUtils.applyBalancedEnchantments(leggings, tier);
        EnchantmentUtils.applyBalancedEnchantments(boots, tier);

        // Equip the armor pieces.
        mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, helmet);
        mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, chestplate);
        mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, leggings);
        mob.setItemStackToSlot(EntityEquipmentSlot.FEET, boots);

        // Additional integration: if the Baubles mod is loaded, equip an accessory.
        if (Loader.isModLoaded("baubles")) {
            applyBaubles(mob, tier);
        }
    }

    /**
     * Applies an accessory item via the Baubles API.
     * In this example, a placeholder accessory ("Ring of Dominance") is equipped;
     * replace it with your custom accessory if desired.
     *
     * @param mob  The boss mob entity.
     * @param tier The boss tier, which can be used to scale accessory potency.
     */
    private static void applyBaubles(EntityLiving mob, int tier) {
        // Create a placeholder accessory using an ItemStack (a diamond is used as a stand-in).
        ItemStack accessory = new ItemStack(Items.DIAMOND);
        accessory.setStackDisplayName("§dRing of Dominance (Tier " + tier + ")");

        try {
            // Uncomment the line below after adding the Baubles API dependency.
            // BaublesApi.getBaublesHandler(mob).setStackInSlot(0, accessory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}