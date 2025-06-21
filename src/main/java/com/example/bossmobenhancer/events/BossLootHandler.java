package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.client.BossRegistry;
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.utils.NameGenerator;
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

import java.util.*;

@Mod.EventBusSubscriber
public class BossLootHandler {

    @SubscribeEvent
    public static void onBossMobDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityLiving)) return;
        EntityLiving mob = (EntityLiving) entity;

        if (!isBossMob(mob)) return;

        List<EntityItem> drops = event.getDrops();
        drops.clear();
        Random rand = mob.world.rand;

        int tier = Math.max(1, (int) (mob.getMaxHealth() / 20));
        boolean isLord = isLordBoss(mob);
        float lootMultiplier = isLord ? 1.0f : ConfigHandler.lootDropMultiplier;

        // A: Elemental weapon
        if (rand.nextInt(100) < (tier * 15 * lootMultiplier)) {
            drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, generateElementalWeapon(tier, rand)));
        }

        // B: Dungeon Loot Tables
        for (String lootTable : ConfigHandler.dungeonLootTables) {
            addSingleItemFromLootTable(drops, mob, rand, lootTable, lootMultiplier);
        }

        // C: Custom Loot Tables
        for (String lootTable : ConfigHandler.customLootTables) {
            addSingleItemFromLootTable(drops, mob, rand, lootTable, lootMultiplier);
        }

        // D: Custom Items
        for (String entry : ConfigHandler.customLootItems) {
            String[] parts = entry.split(",");
            if (parts.length != 3) continue;
            try {
                ResourceLocation itemLoc = new ResourceLocation(parts[0].trim());
                int count = Integer.parseInt(parts[1].trim());
                float chance = Float.parseFloat(parts[2].trim());
                if (rand.nextFloat() < chance) {
                    Item item = ForgeRegistries.ITEMS.getValue(itemLoc);
                    if (item != null) {
                        drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(item, count)));
                    }
                }
            } catch (Exception ignored) {}
        }

        // E: Lootbags Legendary Bag
        if (isLord && Loader.isModLoaded("lootbags")) {
            Item bag = ForgeRegistries.ITEMS.getValue(new ResourceLocation("lootbags", "legendary_loot_bag"));
            if (bag != null) {
                drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(bag)));
            }
        }

        // F: Lunar Blessed Apple
        if (isLord) {
            Item special = ForgeRegistries.ITEMS.getValue(new ResourceLocation("bossmobenhancer", "lunar_blessed_apple"));
            if (special != null) {
                drops.add(new EntityItem(mob.world, mob.posX, mob.posY, mob.posZ, new ItemStack(special)));
            } else {
                System.err.println("[BossMobEnhancer] lunar_blessed_apple not found.");
            }
        }
    }

    private static void addSingleItemFromLootTable(List<EntityItem> drops, EntityLiving mob, Random rand, String lootTableLoc, float multiplier) {
        World world = mob.world;
        LootTableManager manager = world.getLootTableManager();
        LootTable table = manager.getLootTableFromLocation(new ResourceLocation(lootTableLoc));
        LootContext context = new LootContext.Builder((WorldServer) world).withLuck(0.0F).build();
        List<ItemStack> loot = table.generateLootForPools(rand, context);
        if (!loot.isEmpty()) {
            ItemStack chosen = loot.get(rand.nextInt(loot.size()));
            int scaled = Math.max(1, (int) (chosen.getCount() * multiplier));
            chosen.setCount(scaled);
            drops.add(new EntityItem(world, mob.posX, mob.posY, mob.posZ, chosen));
        }
    }

    private static ItemStack generateElementalWeapon(int tier, Random rand) {
        String[] names = {
                "Shadowfang Blade", "Infernal Longsword", "Frostbound Scythe", "Stormpiercer", "Celestial Edge"
        };
        String name = names[rand.nextInt(names.length)];
        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
        stack.setStackDisplayName("§6" + name);

        Map<Enchantment, Integer> ench = new HashMap<>();
        ench.put(Enchantments.SHARPNESS, Math.min(5, tier + rand.nextInt(2)));

        if (name.contains("Shadowfang")) ench.put(Enchantments.UNBREAKING, Math.min(3, tier));
        else if (name.contains("Infernal")) ench.put(Enchantments.FIRE_ASPECT, Math.min(2, tier));
        else if (name.contains("Frostbound")) ench.put(Enchantments.KNOCKBACK, 1);
        else if (name.contains("Stormpiercer")) ench.put(Enchantments.KNOCKBACK, Math.min(3, tier));
        else if (name.contains("Celestial")) ench.put(Enchantments.UNBREAKING, 3);

        Enchantment[] extras = {
                Enchantments.LOOTING, Enchantments.MENDING, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS
        };
        for (int i = 0; i < Math.min(2, rand.nextInt(tier + 1)); i++) {
            Enchantment e = extras[rand.nextInt(extras.length)];
            ench.put(e, Math.min(e.getMaxLevel(), tier));
        }

        EnchantmentHelper.setEnchantments(ench, stack);
        return stack;
    }

    private static boolean isBossMob(EntityLiving mob) {
        String name = mob.getCustomNameTag();
        return (name != null && !name.isEmpty() && NameGenerator.isBossName(name))
                || BossRegistry.getActiveBossMobs(mob.world).contains(mob);
    }

    private static boolean isLordBoss(EntityLiving mob) {
        String name = mob.getCustomNameTag();
        return name != null && name.toLowerCase().contains("lord");
    }
}