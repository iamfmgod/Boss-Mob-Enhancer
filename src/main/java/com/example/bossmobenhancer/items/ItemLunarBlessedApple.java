package com.example.bossmobenhancer.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.UUID;

public class ItemLunarBlessedApple extends ItemFood {
    // Unique UUID used for our health modifier. Using the same UUID ensures we update the boost rather than stacking multiple modifiers.
    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("b6f9d1c9-6c7b-4f79-9f0d-123456789abc");
    // Maximum boost value (each boost is 1.0 health point; 64 boosts add up to 64 health points).
    private static final double MAX_BOOST = 64.0D;

    public ItemLunarBlessedApple() {
        // (hunger restored, saturation modifier, isWolfFood flag)
        super(4, 1.2F, false);
        setUnlocalizedName("lunarBlessedApple");
        setRegistryName("lunar_blessed_apple");
        setCreativeTab(CreativeTabs.FOOD);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            // Get the player's MAX_HEALTH attribute.
            IAttributeInstance maxHealthAttr = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            // Retrieve our modifier if it exists.
            AttributeModifier mod = maxHealthAttr.getModifier(HEALTH_MODIFIER_UUID);

            if (mod == null) {
                // If the modifier isn't applied yet, add a new modifier of 1.0 (adds half a heart).
                AttributeModifier newMod = new AttributeModifier(HEALTH_MODIFIER_UUID, "LunarBlessing", 1.0D, 0);
                maxHealthAttr.applyModifier(newMod);
            } else {
                // If a modifier exists, check its current boost amount.
                double currentBoost = mod.getAmount();
                if (currentBoost < MAX_BOOST) {
                    // Remove the old modifier...
                    maxHealthAttr.removeModifier(mod);
                    // ...and apply a new one with the increased value.
                    AttributeModifier newMod = new AttributeModifier(HEALTH_MODIFIER_UUID, "LunarBlessing", currentBoost + 1.0D, 0);
                    maxHealthAttr.applyModifier(newMod);
                }
                // Otherwise, if currentBoost is at or above MAX_BOOST, do nothing.
            }
        }
        // Proceed with the normal food consumption effects.
        super.onFoodEaten(stack, worldIn, player);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // Mark this item as rare so it appears colorful in tooltips.
        return EnumRarity.RARE;
    }
}