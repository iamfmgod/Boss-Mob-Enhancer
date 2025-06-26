package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic configuration screen for Boss Mob Enhancer.
 * Shows only the "General" and "Display" categories.
 */
@SideOnly(Side.CLIENT)
public class GuiBasicConfigScreen extends GuiConfig {

    public GuiBasicConfigScreen(GuiScreen parentScreen) {
        super(
                parentScreen,
                getBasicConfigElements(),
                "bossmobenhancer",
                false,
                false,
                "Boss Mob Enhancer - Basic Options"
        );
    }

    private static List<IConfigElement> getBasicConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        // Boss Behavior (General)
        ConfigHandler.getConfig().getCategory("General")
                .setComment("Boss Behavior: Toggle fundamental boss settings such as scaling and enhancement chance.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("General")));

        // Visual Effects (Display)
        ConfigHandler.getConfig().getCategory("Display")
                .setComment("Visual Effects: Configure boss nameplate visibility, particle effects, and other visual elements.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Display")));

        return list;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (ConfigHandler.getConfig().hasChanged()) {
            ConfigHandler.getConfig().save();
            ConfigHandler.init(ConfigHandler.getConfig().getConfigFile());
        }
    }
}