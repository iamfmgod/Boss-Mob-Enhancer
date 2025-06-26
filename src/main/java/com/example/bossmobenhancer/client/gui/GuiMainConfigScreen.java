package com.example.bossmobenhancer.client.gui;

import com.example.bossmobenhancer.client.GuiAdvancedConfigScreen;
import com.example.bossmobenhancer.client.GuiBasicConfigScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Main entry screen for Boss Mob Enhancer's in-game config GUI.
 */
@SideOnly(Side.CLIENT)
public class GuiMainConfigScreen extends GuiScreen {
    private final GuiScreen parentScreen;

    public GuiMainConfigScreen(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        super.initGui();  // ensure width/height updated
        buttonList.clear();

        int buttonWidth  = 160;
        int buttonHeight = 20;
        int centerX      = width  / 2;
        int centerY      = height / 2;

        // Basic, Advanced, and Done buttons
        buttonList.add(new GuiButton(0,
                centerX - buttonWidth - 10,
                centerY - buttonHeight / 2,
                buttonWidth, buttonHeight,
                "Basic Options"
        ));
        buttonList.add(new GuiButton(1,
                centerX + 10,
                centerY - buttonHeight / 2,
                buttonWidth, buttonHeight,
                "Advanced Options"
        ));
        buttonList.add(new GuiButton(2,
                centerX - buttonWidth / 2,
                centerY + buttonHeight + 10,
                buttonWidth, buttonHeight,
                "Done"
        ));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiBasicConfigScreen(this));
                break;
            case 1:
                mc.displayGuiScreen(new GuiAdvancedConfigScreen(this));
                break;
            case 2:
                mc.displayGuiScreen(parentScreen);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        // Semi-transparent fullscreen overlay
        drawRect(0, 0, width, height, 0x80000000);
        drawCenteredString(
                fontRenderer,
                "Boss Mob Enhancer Configuration",
                width / 2, 15,
                0xFFFFFF
        );
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}