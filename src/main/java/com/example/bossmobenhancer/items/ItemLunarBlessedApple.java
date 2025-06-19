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
    // Unique UUID used for our health modifier.
    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("b6f9d1c9-6c7b-4f79-9f0d-123456789abc");
    private static final double MAX_BOOST = 64.0D;

    public ItemLunarBlessedApple() {
        super(4, 1.2F, false);
        setUnlocalizedName("lunarBlessedApple");
        setCreativeTab(CreativeTabs.FOOD);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            IAttributeInstance maxHealthAttr = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            AttributeModifier mod = maxHealthAttr.getModifier(HEALTH_MODIFIER_UUID);

            if (mod == null) {
                maxHealthAttr.applyModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, "LunarBlessing", 1.0D, 0));
            } else {
                double currentBoost = mod.getAmount();
                if (currentBoost < MAX_BOOST) {
                    maxHealthAttr.removeModifier(mod);
                    maxHealthAttr.applyModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, "LunarBlessing", currentBoost + 1.0D, 0));
                }
            }
        }
        super.onFoodEaten(stack, worldIn, player);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }
}