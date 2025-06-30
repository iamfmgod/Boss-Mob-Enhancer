package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class GuiAdvancedConfigScreen extends GuiConfig {
    private static final int RESET_ID = 100;
    private static final int SAVE_ID  = 101;

    private final GuiScreen parent;

    public GuiAdvancedConfigScreen(GuiScreen parent) {
        super(
                parent,
                // build only non‐general, non‐display, top‐level categories
                getAllCategories().stream()
                        .map(ConfigElement::new)
                        .collect(Collectors.toList()),
                MainMod.MODID,
                false,
                false,
                "Boss Mob Enhancer - Advanced Options"
        );
        this.parent = parent;
    }

    private static List<ConfigCategory> getAllCategories() {
        return ConfigHandler.getConfig()
                .getCategoryNames()
                .stream()
                // exclude the ones handled elsewhere
                .filter(name ->
                        !name.equalsIgnoreCase("general") &&
                                !name.equalsIgnoreCase("display")
                )
                // grab each category object
                .map(ConfigHandler.getConfig()::getCategory)
                // only top-level (no children)
                .filter(cat -> !cat.isChild())
                .collect(Collectors.toList());
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.removeIf(b -> b.id == RESET_ID || b.id == SAVE_ID);

        int btnW = 110, btnH = 20, spacing = 10;
        int totalW = btnW * 2 + spacing;
        int startX = (width - totalW) / 2;
        int yPos   = height - btnH - 10;

        buttonList.add(new GuiButton(RESET_ID,
                startX, yPos, btnW, btnH, "Reset Defaults"));
        buttonList.add(new GuiButton(SAVE_ID,
                startX + btnW + spacing, yPos, btnW, btnH, "Save & Close"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == RESET_ID) {
            resetToDefaults();
            mc.displayGuiScreen(new GuiAdvancedConfigScreen(parent));
        }
        else if (button.id == SAVE_ID) {
            ConfigHandler.getConfig().save();
            mc.displayGuiScreen(parent);
        }
        else {
            super.actionPerformed(button);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (Character.toUpperCase(typedChar) == 'R') {
            resetToDefaults();
            mc.displayGuiScreen(new GuiAdvancedConfigScreen(parent));
        }
    }

    private void resetToDefaults() {
        ConfigHandler.getConfig()
                .getCategoryNames()
                .forEach(name -> {
                    if (name.equalsIgnoreCase("general") ||
                            name.equalsIgnoreCase("display")) {
                        return; // skip these
                    }
                    ConfigCategory cat = ConfigHandler.getConfig().getCategory(name);
                    cat.getValues().forEach((k, prop) -> prop.setToDefault());
                });

        ConfigHandler.getConfig().save();
        ConfigHandler.init(ConfigHandler.getConfig().getConfigFile());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawRect(0, 0, width, height, 0x80000000);
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