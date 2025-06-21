package com.example.bossmobenhancer.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMainConfigScreen extends GuiScreen {
    private final GuiScreen parentScreen;

    public GuiMainConfigScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int buttonWidth = 160;
        int buttonHeight = 20;
        int centerX = width / 2;
        int centerY = height / 2;
        // Two option buttons for Basic and Advanced settings, plus a Done button.
        buttonList.add(new GuiButton(0, centerX - buttonWidth - 10, centerY - buttonHeight / 2, buttonWidth, buttonHeight, "Basic Options"));
        buttonList.add(new GuiButton(1, centerX + 10, centerY - buttonHeight / 2, buttonWidth, buttonHeight, "Advanced Options"));
        buttonList.add(new GuiButton(2, centerX - buttonWidth / 2, centerY + buttonHeight + 10, buttonWidth, buttonHeight, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(new GuiBasicConfigScreen(this));
        } else if (button.id == 1) {
            mc.displayGuiScreen(new GuiAdvancedConfigScreen(this));
        } else if (button.id == 2) {
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        // Draw a semi-transparent overlay for aesthetics.
        drawRect(0, 0, width, height, 0x80000000);
        drawCenteredString(fontRenderer, "Boss Mob Enhancer Configuration", width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}