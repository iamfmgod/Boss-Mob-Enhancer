package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.client.BossRegistry;
import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.Random;

/**
 * BossLootHandler listens for boss mob deaths and creates a rich loot table with enhanced rewards.
 */
@Mod.EventBusSubscriber
public class BossLootHandler {

    @SubscribeEvent
    public static void onBossMobDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityLiving)) {
            return;
        }
        EntityLiving mob = (EntityLiving) entity;

        // Determine if the mob is a boss by custom naming convention or registration.
        if (!isBossMob(mob)) {
            return;
        }

        // Clear default drops and prepare our enhanced loot list.
        List<EntityItem> drops = event.getDrops();
        drops.clear();

        Random rand = mob.world.rand;

        // Calculate the boss tier (e.g., 20 health = 1 tier)
        int tier = (int) (mob.getMaxHealth() / 20);
        if (tier < 1) {
            tier = 1;
        }

        // Reward A: Custom Enchanted Weapon (Diamond Sword).
        ItemStack weaponLoot = new ItemStack(Items.DIAMOND_SWORD);
        int sharpnessLevel = Math.min(5, tier + 1);
        weaponLoot.addEnchantment(Enchantments.SHARPNESS, sharpnessLevel);
        drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, weaponLoot));

        // Reward B: Golden Apple (with increased chance based on tier).
        // For example, at tier 1 the chance is 20%, scaling upward.
        if (rand.nextInt(100) < (tier * 20)) {
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(Items.GOLDEN_APPLE)));
        }

        // Reward C: Nether Star drop for higher-tier bosses.
        if (tier >= 3 && rand.nextInt(100) < (tier * 10)) {
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(Items.NETHER_STAR)));
        }

        // Reward D: Splash Potion drop (chance increases with tier).
        if (rand.nextInt(100) < (tier * 15)) {
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(Items.SPLASH_POTION)));
        }

        // Reward E: Bonus diamonds (small chance scaled with tier).
        if (rand.nextInt(100) < (tier * 5)) {
            int diamondCount = rand.nextInt(3) + 1; // 1 to 3 diamonds.
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(Items.DIAMOND, diamondCount)));
        }

        // Reward F: If the Loot Bags mod is installed, guarantee a Legendary Loot Bag drop.
        if (Loader.isModLoaded("lootbags")) {
            Item legendaryBag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("lootbags", "legendary_loot_bag"));
            if (legendaryBag != null) {
                drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(legendaryBag)));
            }
        }
    }

    /**
     * Checks if the mob qualifies as a boss by verifying its custom naming or registration status.
     *
     * @param mob The EntityLiving instance to check.
     * @return true if the mob qualifies as a boss.
     */
    private static boolean isBossMob(EntityLiving mob) {
        String customName = mob.getCustomNameTag();
        if (customName != null && !customName.trim().isEmpty()) {
            if (NameGenerator.isBossName(customName)) {
                return true;
            }
        }
        return BossRegistry.getActiveBossMobs(mob.world).contains(mob);
    }
}