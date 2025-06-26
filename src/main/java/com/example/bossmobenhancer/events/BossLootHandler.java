package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Handles loot drops for enhanced boss mobs.
 */
@Mod.EventBusSubscriber(modid = MainMod.MODID, value = Side.SERVER)
public class BossLootHandler {
    private static final Logger LOGGER = LogManager.getLogger(MainMod.MODID);

    @SubscribeEvent
    public static void onBossMobDrops(LivingDropsEvent event) {
        try {
            EntityLivingBase base = event.getEntityLiving();
            if (base.world.isRemote || !(base instanceof EntityLiving)) return;
            EntityLiving mob = (EntityLiving) base;

            // read NBT and name
            NBTTagCompound data = mob.getEntityData();
            String rawName = mob.getCustomNameTag();
            String cleanName = TextFormatting
                    .getTextWithoutFormattingCodes(rawName == null ? "" : rawName)
                    .trim();

            // detect boss: either named by NameGenerator or flagged NBT
            boolean isBoss = NameGenerator.isBossName(cleanName)
                    || data.getBoolean("isLordBoss")
                    || data.getBoolean("isEliteBoss"); // if you have other flags
            if (!isBoss) return;

            // clear vanilla drops
            List<EntityItem> drops = event.getDrops();
            drops.clear();

            Random rand = mob.getRNG();
            // tier: prefer NBT, fallback to health
            int tier = data.getInteger("bossTier");
            if (tier <= 0) {
                tier = Math.max(1, (int)(mob.getMaxHealth() / 20.0));
            }

            boolean isLord = data.getBoolean("isLordBoss");
            float lootMult = isLord ? 1.0f : ConfigHandler.lootDropMultiplier;

            // A) Elemental weapon
            if (rand.nextInt(100) < tier * 15 * lootMult) {
                drops.add(new EntityItem(
                        mob.world, mob.posX, mob.posY, mob.posZ,
                        generateElementalWeapon(tier, rand)
                ));
            }

            // B) Dungeon loot tables
            for (String tableLoc : ConfigHandler.dungeonLootTables) {
                addLootFromTable(drops, mob, rand, tableLoc, lootMult);
            }

            // C) Custom loot tables
            for (String tableLoc : ConfigHandler.customLootTables) {
                addLootFromTable(drops, mob, rand, tableLoc, lootMult);
            }

            // D) Custom loot items (mod-defined)
            for (String entry : ConfigHandler.customLootItems) {
                String[] parts = entry.split(",");
                if (parts.length != 3) continue;
                try {
                    ResourceLocation id     = new ResourceLocation(parts[0].trim());
                    int             count  = Integer.parseInt(parts[1].trim());
                    float           chance = Float.parseFloat(parts[2].trim());
                    if (rand.nextFloat() <= chance) {
                        Item item = ForgeRegistries.ITEMS.getValue(id);
                        if (item != null) {
                            drops.add(new EntityItem(
                                    mob.world, mob.posX, mob.posY, mob.posZ,
                                    new ItemStack(item, count)
                            ));
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.warn("Bad customLootItems entry: {}", entry, ex);
                }
            }

            // E) LootBags legendary bag (if installed & lord)
            if (isLord && Loader.isModLoaded("lootbags")) {
                Item bag = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("lootbags", "legendary_loot_bag")
                );
                if (bag != null) {
                    drops.add(new EntityItem(
                            mob.world, mob.posX, mob.posY, mob.posZ,
                            new ItemStack(bag)
                    ));
                }
            }

            // F) Lunar blessed apple for lords
            if (isLord) {
                Item apple = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(MainMod.MODID, "lunar_blessed_apple")
                );
                if (apple != null) {
                    drops.add(new EntityItem(
                            mob.world, mob.posX, mob.posY, mob.posZ,
                            new ItemStack(apple)
                    ));
                } else {
                    LOGGER.error("lunar_blessed_apple not found in registry");
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error in BossLootHandler.onBossMobDrops", e);
        }
    }

    private static void addLootFromTable(List<EntityItem> drops,
                                         EntityLiving mob,
                                         Random rand,
                                         String tableLoc,
                                         float mult) {
        try {
            WorldServer ws = (WorldServer) mob.world;
            LootTableManager mgr = ws.getLootTableManager();
            LootTable table = mgr.getLootTableFromLocation(new ResourceLocation(tableLoc));
            LootContext ctx = new LootContext.Builder(ws)
                    .withLuck(0.0F)
                    .build();

            List<ItemStack> loot = table.generateLootForPools(rand, ctx);
            if (!loot.isEmpty()) {
                ItemStack pick = loot.get(rand.nextInt(loot.size()));
                pick.setCount(Math.max(1, (int)(pick.getCount() * mult)));
                drops.add(new EntityItem(ws, mob.posX, mob.posY, mob.posZ, pick));
            }
        } catch (Exception ex) {
            LOGGER.error("Loot table error: {}", tableLoc, ex);
        }
    }

    private static ItemStack generateElementalWeapon(int tier, Random rand) {
        String[] names = {
                "Shadowfang Blade", "Infernal Longsword",
                "Frostbound Scythe", "Stormpiercer", "Celestial Edge"
        };
        String name = names[rand.nextInt(names.length)];
        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
        stack.setStackDisplayName(TextFormatting.GOLD + name);

        Map<Enchantment,Integer> ench = new HashMap<>();
        ench.put(Enchantments.SHARPNESS, Math.min(5, tier + rand.nextInt(2)));
        if (name.contains("Shadowfang"))    ench.put(Enchantments.UNBREAKING, Math.min(3, tier));
        else if (name.contains("Infernal")) ench.put(Enchantments.FIRE_ASPECT, Math.min(2, tier));
        else if (name.contains("Frostbound")) ench.put(Enchantments.KNOCKBACK, 1);
        else if (name.contains("Stormpiercer")) ench.put(Enchantments.KNOCKBACK, Math.min(3, tier));
        else if (name.contains("Celestial")) ench.put(Enchantments.UNBREAKING, 3);

        Enchantment[] extras = {
                Enchantments.LOOTING, Enchantments.MENDING,
                Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS
        };
        int extraCount = Math.min(2, rand.nextInt(tier + 1));
        for (int i = 0; i < extraCount; i++) {
            Enchantment e = extras[rand.nextInt(extras.length)];
            ench.put(e, Math.min(e.getMaxLevel(), tier));
        }

        EnchantmentHelper.setEnchantments(ench, stack);
        return stack;
    }
}