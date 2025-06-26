package com.example.bossmobenhancer.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Button for selecting curses in the Enshrouded Beacon GUI.
 */
@SideOnly(Side.CLIENT)
public class GuiCurseButton extends GuiButton {

    private final ResourceLocation texture;
    private final ResourceLocation icon;
    private boolean isSelected;

    public GuiCurseButton(int id, int x, int y, int width, int height,
                          String displayString,
                          ResourceLocation texture,
                          ResourceLocation icon,
                          boolean isSelected) {
        super(id, x, y, width, height, displayString);
        this.texture    = texture;
        this.icon       = icon;
        this.isSelected = isSelected;
    }

    /** Dynamically change selection state. */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        // Bind base texture
        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1F, 1F, 1F, 1F);

        // Update hover state
        this.hovered = mouseX >= x && mouseY >= y
                && mouseX < x + width && mouseY < y + height;

        // Draw button background (hover shifts V coordinate)
        int textureV = hovered ? height : 0;
        drawTexturedModalRect(x, y, 0, textureV, width, height);

        // Cyan glow overlay if selected
        if (isSelected) {
            GlStateManager.enableBlend();
            GlStateManager.color(0.4F, 0.8F, 1F, 0.5F);
            drawRect(x, y, x + width, y + height, 0x55FFFFFF);
            GlStateManager.disableBlend();
        }

        // Draw icon if provided
        if (icon != null) {
            mc.getTextureManager().bindTexture(icon);
            drawModalRectWithCustomSizedTexture(x + 4, y + 2, 0, 0, 16, 16, 16, 16);
        }

        // Draw centered label
        int textColor = isSelected ? 0xFFFFAA00 : 0xFFFFFFFF;
        drawCenteredString(mc.fontRenderer, displayString,
                x + width / 2, y + (height - 8) / 2, textColor);
    }
}