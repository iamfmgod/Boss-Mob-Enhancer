package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import java.util.ArrayList;
import java.util.List;

public class GuiBasicConfigScreen extends GuiConfig {

    public GuiBasicConfigScreen(GuiScreen parentScreen) {
        super(parentScreen, getBasicConfigElements(), "bossmobenhancer", false, false, "Boss Mob Enhancer - Basic Options");
    }

    private static List<IConfigElement> getBasicConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        // Set a friendly comment for the General category so it appears as "Boss Behavior"
        ConfigHandler.getConfig().getCategory("General")
                .setComment("Boss Behavior: Toggle fundamental boss settings such as scaling and enhancement chance.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("General")));

        // Set a friendly comment for the Display category so it appears as "Visual Effects"
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