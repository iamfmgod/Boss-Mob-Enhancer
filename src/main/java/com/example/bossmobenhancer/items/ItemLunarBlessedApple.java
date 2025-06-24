package com.example.bossmobenhancer.items;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemLunarBlessedApple extends ItemFood {

    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("b6f9d1c9-6c7b-4f79-9f0d-123456789abc");
    private static final double MAX_BOOST = 64.0D;

    public ItemLunarBlessedApple() {
        super(4, 1.2F, false);
        // Use setTranslationKey to define the item's unlocalized name.
        // This results in the translation key "item.bossmobenhancer.lunar_blessed_apple.name"
        this.setTranslationKey(MainMod.MODID + ".lunar_blessed_apple");
        setCreativeTab(CreativeTabs.FOOD);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            IAttributeInstance maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            AttributeModifier existing = maxHealth.getModifier(HEALTH_MODIFIER_UUID);
            double newBoost = 0;
            if (existing != null) {
                newBoost = Math.min(existing.getAmount() + 1.0D, MAX_BOOST);
                maxHealth.removeModifier(existing);
            } else {
                newBoost = 1.0D;
            }
            if (newBoost <= MAX_BOOST) {
                maxHealth.applyModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, "LunarBlessing", newBoost, 0));
            }
            // Apply temporary potion effects.
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 10 * 20, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 20, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 60 * 20, 0));
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.3F);
        }
        super.onFoodEaten(stack, world, player);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_PURPLE + "A blessing harvested under the lunar eclipse.");
        tooltip.add(TextFormatting.GRAY + "Permanently increases max health (up to +64).");
        tooltip.add(TextFormatting.BLUE + "Grants regeneration, resistance, and night vision.");
    }
}