package com.example.bossmobenhancer.utils;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber
public class NameGenerator {

    private static final Random random = new Random();

    public static boolean isBossName(String name) {
        if (name == null || name.isEmpty()) return false;
        return name.startsWith("§7Wanderer ") ||
                name.startsWith("§7Challenger ") ||
                name.startsWith("§7Champion ") ||
                name.startsWith("§7Overlord ") ||
                name.startsWith("§7Eclipsebound ") ||
                name.startsWith("§6Lord ");
    }

    public static String generateBossName() {
        String[] prefixes = {
                "§7Wanderer ", "§7Challenger ", "§7Champion ",
                "§7Overlord ", "§7Eclipsebound ", "§6Lord "
        };
        String[] suffixes = {
                "of the Eclipse", "the Unyielding", "the Shadowborn",
                "the Forsaken", "the Eternal", "the Vanquisher"
        };
        return prefixes[random.nextInt(prefixes.length)] +
                suffixes[random.nextInt(suffixes.length)];
    }

    public static String generateFantasyName(EntityLiving mob, int tier) {
        return generateBossName() + " §7[Tier " + tier + "]";
    }

    public static String getDisplayName(EntityLiving mob, int tier) {
        if (!ConfigHandler.showBossNamePlate) {
            return "";
        }
        return generateFantasyName(mob, tier);
    }

    /**
     * Assigns a name and visibility based on config setting.
     */
    public static void applyDisplayName(EntityLiving mob, int tier) {
        String name = getDisplayName(mob, tier);
        mob.setCustomNameTag(name);
        mob.setAlwaysRenderNameTag(ConfigHandler.showBossNamePlate);
    }

    /**
     * Optional client-side override to suppress nameplate rendering even if other code fails to hide it.
     */
    @SubscribeEvent
    public static void onRenderName(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (!ConfigHandler.showBossNamePlate &&
                isBossName(event.getEntity().getCustomNameTag())) {
            event.setCanceled(true);
        }
    }
}