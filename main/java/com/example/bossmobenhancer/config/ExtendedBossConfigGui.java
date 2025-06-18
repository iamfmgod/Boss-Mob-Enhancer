package com.example.bossmobenhancer.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import com.example.bossmobenhancer.config.ConfigHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * ExtendedBossConfigGui provides a polished, multi-category configuration screen
 * for the Boss Mob Enhancer mod. It exposes options from the following config sections:
 *
 * - General: Basic settings (e.g. Enable Scaling, Enhancement Chance)
 * - Scaling: Scaling profile settings (e.g. Difficulty Curve Preset, Health Weight, Player Count Weight, Max Tier)
 * - Abilities: Toggles for special abilities (e.g. Terrain Effects, Minions, Buff Potions)
 * - Entities: Mob filters that define which mobs can or cannot be enhanced.
 *
 * This GUI allows modpack makers and players to adjust settings without directly editing
 * the config file.
 */
public class ExtendedBossConfigGui extends GuiConfig {

    public ExtendedBossConfigGui(GuiScreen parentScreen) {
        super(parentScreen,
                getConfigElements(),
                "bossmobenhancer",  // mod id; must match the one in your mod annotation
                false,              // Do settings require a world restart?
                false,              // Do settings require a Minecraft restart?
                "Boss Mob Enhancer Configuration");
    }

    /**
     * Creates and returns a list of configuration elements by retrieving the four categories that
     * exist in your bossmobenhancer.cfg file. These categories are "General", "Scaling", "Abilities",
     * and "Entities".
     *
     * @return a list of IConfigElement objects that represent your configuration categories.
     */
    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        // Add the "General" category.
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("General")));

        // Add the "Scaling" category.
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Scaling")));

        // Add the "Abilities" category.
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Abilities")));

        // Add the "Entities" category.
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Entities")));

        return list;
    }
}