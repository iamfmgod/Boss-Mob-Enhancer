package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class GuiAdvancedConfigScreen extends GuiConfig {

    private final GuiScreen parentScreen;

    public GuiAdvancedConfigScreen(GuiScreen parentScreen) {
        super(parentScreen, getAdvancedConfigElements(), "bossmobenhancer", false, false, "Boss Mob Enhancer - Advanced Options");
        this.parentScreen = parentScreen;
    }

    private static List<IConfigElement> getAdvancedConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        // Scaling Category
        ConfigHandler.getConfig().getCategory("Scaling")
                .setComment("Difficulty Scaling: Adjust internal multipliers and weights for dynamic boss scaling.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Scaling")));

        // Abilities Category
        ConfigHandler.getConfig().getCategory("Abilities")
                .setComment("Abilities & Effects: Enable and fine-tune special boss abilities and terrain effects.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Abilities")));

        // Entities Category
        ConfigHandler.getConfig().getCategory("Entities")
                .setComment("Mob Filters: Specify whitelists and blacklists for enhanced bosses.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Entities")));

        // Minions Category
        ConfigHandler.getConfig().getCategory("Minions")
                .setComment("Minion Settings: Adjust the spawn chance and maximum minions for bosses.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Minions")));

        // Rewards Category - new
        ConfigHandler.getConfig().getCategory("Rewards")
                .setComment("Rewards Settings: Configure loot drop multipliers for non-lord bosses.");
        list.add(new ConfigElement(ConfigHandler.getConfig().getCategory("Rewards")));

        return list;
    }

    @Override
    public void initGui() {
        super.initGui();
        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = width / 2 - buttonWidth / 2;
        int y = height - 40;
        buttonList.add(new GuiButton(100, x, y, buttonWidth, buttonHeight, "Reset Defaults"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 100) {
            resetToDefaults();
            mc.displayGuiScreen(new GuiAdvancedConfigScreen(this.parentScreen));
            return;
        }
        super.actionPerformed(button);
    }

    private void resetToDefaults() {
        // Reset Scaling values.
        ConfigHandler.getConfig().get("Scaling", "Difficulty Curve Preset", "Balanced").set("Balanced");
        ConfigHandler.getConfig().get("Scaling", "Health Weight", 1.0f).set(1.0f);
        ConfigHandler.getConfig().get("Scaling", "Player Count Weight", 1.0f).set(1.0f);
        ConfigHandler.getConfig().get("Scaling", "Max Tier", 4).set(4);

        // Reset Abilities.
        ConfigHandler.getConfig().get("Abilities", "Enable Terrain Effects", true).set(true);
        ConfigHandler.getConfig().get("Abilities", "Enable Minions", true).set(true);
        ConfigHandler.getConfig().get("Abilities", "Enable Buff Potions", true).set(true);

        // Reset Entities.
        ConfigHandler.getConfig().get("Entities", "Mob Whitelist", new String[]{}).set(new String[]{});
        ConfigHandler.getConfig().get("Entities", "Mob Blacklist", new String[]{}).set(new String[]{});

        // Reset Minions.
        ConfigHandler.getConfig().get("Minions", "Base Minion Spawn Chance", 20).set(20);
        ConfigHandler.getConfig().get("Minions", "Max Minions Per Spawn", 5).set(5);

        // Reset Rewards.
        ConfigHandler.getConfig().get("Rewards", "Loot Drop Multiplier", 0.25f).set(0.25f);

        ConfigHandler.getConfig().save();
        ConfigHandler.init(ConfigHandler.getConfig().getConfigFile());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawRect(0, 0, width, height, new Color(0, 0, 0, 128).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
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