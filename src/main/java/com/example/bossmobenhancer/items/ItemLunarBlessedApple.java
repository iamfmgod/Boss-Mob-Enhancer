package com.example.bossmobenhancer.items;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemLunarBlessedApple extends ItemFood {
    public static final UUID HEALTH_MODIFIER_UUID =
            UUID.fromString("b6f9d1c9-6c7b-4f79-9f0d-123456789abc");
    private static final double MAX_BOOST = 64.0D;

    public ItemLunarBlessedApple() {
        super(4, 1.2F, false);
        // registry name will be set when we register the item:
        setTranslationKey(MainMod.MODID + ".lunar_blessed_apple");
        setCreativeTab(CreativeTabs.FOOD);
        setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten(@Nonnull ItemStack stack,
                               @Nonnull World world,
                               @Nonnull EntityPlayer player) {
        if (!world.isRemote) {
            IAttributeInstance hpAttr =
                    player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);

            AttributeModifier old = hpAttr.getModifier(HEALTH_MODIFIER_UUID);
            double boost = (old == null
                    ? 1.0D
                    : Math.min(old.getAmount() + 1.0D, MAX_BOOST));

            if (old != null) hpAttr.removeModifier(old);
            hpAttr.applyModifier(new AttributeModifier(
                    HEALTH_MODIFIER_UUID, "LunarBlessing", boost, 0));

            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 400, 0));
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 1200, 0));

            world.playSound(null,
                    player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.PLAYERS,
                    1.0F, 1.3F);
        }
        super.onFoodEaten(stack, world, player);
    }

    @Override
    @Nonnull
    public EnumRarity getRarity(@Nonnull ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack,
                               @Nullable World worldIn,
                               @Nonnull List<String> tooltip,
                               @Nonnull ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_PURPLE +
                "A blessing harvested under the lunar eclipse.");
        tooltip.add(TextFormatting.GRAY +
                "Permanently increases max health (up to +" + (int) MAX_BOOST + ").");
        tooltip.add(TextFormatting.BLUE +
                "Grants Regeneration, Resistance, and Night Vision.");
    }
}