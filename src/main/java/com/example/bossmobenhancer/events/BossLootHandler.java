package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.client.BossRegistry;
import com.example.bossmobenhancer.utils.NameGenerator;
import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber
public class BossLootHandler {

    @SubscribeEvent
    public static void onBossMobDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityLiving)) {
            return;
        }
        EntityLiving mob = (EntityLiving) entity;

        // Only process if this mob qualifies as a boss.
        if (!isBossMob(mob)) {
            return;
        }

        List<EntityItem> drops = event.getDrops();
        drops.clear(); // Clear default drops for custom loot logic.
        Random rand = mob.world.rand;

        // Determine boss tier based on maximum health.
        int tier = Math.max(1, (int) (mob.getMaxHealth() / 20));

        // Determine if this boss is a "lord" (full rewards) or not.
        boolean isLord = isLordBoss(mob);
        // For lord bosses, drop amounts remain full (multiplier 1.0). For non-lord bosses, use the configurable multiplier.
        float lootMultiplier = isLord ? 1.0f : ConfigHandler.lootDropMultiplier;

        // Reward A: Elemental Weapon drop – chance scales with tier.
        if (rand.nextInt(100) < (tier * 15 * lootMultiplier)) {
            ItemStack weaponLoot = generateElementalWeapon(tier, rand);
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, weaponLoot));
        }

        // Reward B: Golden Apple drop.
        if (rand.nextInt(100) < (tier * 20 * lootMultiplier)) {
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ,
                    new ItemStack(Items.GOLDEN_APPLE)));
        }

        // Reward C: Nether Star drop for higher-tier bosses.
        if (tier >= 3 && rand.nextInt(100) < (tier * 10 * lootMultiplier)) {
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ,
                    new ItemStack(Items.NETHER_STAR)));
        }

        // Reward D: Bonus diamonds.
        if (rand.nextInt(100) < (tier * 5 * lootMultiplier)) {
            int diamondCount = Math.max(1, (int) Math.round((rand.nextInt(3) + 1) * lootMultiplier));
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ,
                    new ItemStack(Items.DIAMOND, diamondCount)));
        }

        // Reward E: Pull loot from various dungeon loot tables.
        // Scale down each loot stack from dungeon loot by the multiplier.
        addDungeonLoot(drops, mob, rand, "minecraft:chests/stronghold_library", lootMultiplier);
        addDungeonLoot(drops, mob, rand, "minecraft:chests/nether_bridge", lootMultiplier);
        addDungeonLoot(drops, mob, rand, "minecraft:chests/end_city_treasure", lootMultiplier);

        // Reward F: If Loot Bags mod is installed and the boss is a lord, drop a legendary loot bag.
        if (isLord && Loader.isModLoaded("lootbags")) {
            Item legendaryBag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("lootbags", "legendary_loot_bag"));
            if (legendaryBag != null) {
                drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ,
                        new ItemStack(legendaryBag)));
            }
        }
    }

    /**
     * Generates an elemental weapon with themed enchantments based on the boss tier.
     *
     * @param tier the boss tier level.
     * @param rand a random instance.
     * @return a dynamically enchanted elemental weapon as an ItemStack.
     */
    private static ItemStack generateElementalWeapon(int tier, Random rand) {
        // Define elemental weapon names.
        String[] weaponNames = {
                "Shadowfang Blade",   // Withering Strike.
                "Infernal Longsword",   // Burning Execution.
                "Frostbound Scythe",    // Glacial Grip.
                "Stormpiercer",         // Lightning Surge.
                "Celestial Edge"        // Gravity Breaker.
        };
        ItemStack weapon = new ItemStack(Items.DIAMOND_SWORD);
        String chosenName = weaponNames[rand.nextInt(weaponNames.length)];
        weapon.setStackDisplayName("§6" + chosenName);

        // Base enchantments.
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        enchantments.put(Enchantments.SHARPNESS, Math.min(5, tier + rand.nextInt(2)));

        // Apply thematic enchantments based on the name.
        if (chosenName.contains("Shadowfang")) {
            enchantments.put(Enchantments.UNBREAKING, Math.min(3, tier));
        } else if (chosenName.contains("Infernal")) {
            enchantments.put(Enchantments.FIRE_ASPECT, Math.min(2, tier));
        } else if (chosenName.contains("Frostbound")) {
            enchantments.put(Enchantments.KNOCKBACK, 1);
        } else if (chosenName.contains("Stormpiercer")) {
            enchantments.put(Enchantments.KNOCKBACK, Math.min(3, tier));
        } else if (chosenName.contains("Celestial")) {
            enchantments.put(Enchantments.UNBREAKING, 3);
        }

        // Add extra enchantments randomly (up to 2 extras).
        Enchantment[] extraEnchantments = {
                Enchantments.LOOTING,
                Enchantments.MENDING,
                Enchantments.SMITE,
                Enchantments.BANE_OF_ARTHROPODS
        };
        int extraCount = Math.min(2, rand.nextInt(tier + 1));
        for (int i = 0; i < extraCount; i++) {
            Enchantment extra = extraEnchantments[rand.nextInt(extraEnchantments.length)];
            int level = Math.min(extra.getMaxLevel(), tier);
            enchantments.put(extra, level);
        }

        EnchantmentHelper.setEnchantments(enchantments, weapon);
        return weapon;
    }

    /**
     * Adds loot from a given dungeon loot table and scales each loot stack by the loot multiplier.
     *
     * @param drops        the list of current drops.
     * @param mob          the boss mob instance.
     * @param rand         a random instance.
     * @param lootTableLoc the resource location string for the loot table.
     * @param multiplier   The loot multiplier (1.0 for full rewards, less for reduced rewards).
     */
    private static void addDungeonLoot(List<EntityItem> drops, EntityLiving mob, Random rand, String lootTableLoc, float multiplier) {
        World world = mob.world;
        LootTableManager lootManager = world.getLootTableManager();
        LootTable table = lootManager.getLootTableFromLocation(new ResourceLocation(lootTableLoc));

        // Build a LootContext using WorldServer cast, with default luck.
        LootContext context = new LootContext.Builder((WorldServer) world)
                .withLuck(0.0F)
                .build();

        List<ItemStack> lootList = table.generateLootForPools(rand, context);
        for (ItemStack stack : lootList) {
            // Scale down stack sizes by the multiplier, ensuring at least 1 item remains.
            int newCount = Math.max(1, (int) (stack.getCount() * multiplier));
            stack.setCount(newCount);
            drops.add(new EntityItem(world, mob.posX, mob.posY, mob.posZ, stack));
        }
    }

    /**
     * Determines whether the provided mob qualifies as a boss.
     *
     * @param mob the EntityLiving instance to check.
     * @return true if the mob qualifies as a boss.
     */
    private static boolean isBossMob(EntityLiving mob) {
        String customName = mob.getCustomNameTag();
        if (customName != null && !customName.trim().isEmpty()) {
            return NameGenerator.isBossName(customName);
        }
        return BossRegistry.getActiveBossMobs(mob.world).contains(mob);
    }

    /**
     * Determines whether the boss mob is considered a "lord."
     * For this example, a boss is considered a lord if its custom name contains "lord" (ignoring case).
     *
     * @param mob the boss mob.
     * @return true if it is a lord, false otherwise.
     */
    private static boolean isLordBoss(EntityLiving mob) {
        String customName = mob.getCustomNameTag();
        return customName != null && customName.toLowerCase().contains("lord");
    }
}