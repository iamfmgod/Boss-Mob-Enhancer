package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiConfigScreen extends GuiConfig {

    public GuiConfigScreen(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), "bossmobenhancer", false, false, "Boss Mob Enhancer Config");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        Configuration config = ConfigHandler.getConfig();

        list.add(new ConfigElement(config.getCategory("General")));
        list.add(new ConfigElement(config.getCategory("Scaling")));
        list.add(new ConfigElement(config.getCategory("Abilities")));
        list.add(new ConfigElement(config.getCategory("Entities")));

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