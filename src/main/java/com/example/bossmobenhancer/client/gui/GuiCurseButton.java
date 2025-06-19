package com.example.bossmobenhancer.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiCurseButton extends GuiButton {

    private final ResourceLocation texture;
    private final ResourceLocation icon;
    private final boolean isSelected;

    public GuiCurseButton(int id, int x, int y, int width, int height, String displayString,
                          ResourceLocation texture, ResourceLocation icon, boolean isSelected) {
        super(id, x, y, width, height, displayString);
        this.texture = texture;
        this.icon = icon;
        this.isSelected = isSelected;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible)
            return;

        // Bind and draw the base button texture.
        mc.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Update hover state.
        this.hovered = mouseX >= this.x && mouseY >= this.y &&
                mouseX < this.x + this.width && mouseY < this.y + this.height;

        // Determine texture vertical offset: use the lower portion (height) when hovered.
        int textureV = this.hovered ? this.height : 0;
        this.drawTexturedModalRect(this.x, this.y, 0, textureV, this.width, this.height);

        // If selected, draw a glowing cyan overlay.
        if (isSelected) {
            GlStateManager.enableBlend();
            GlStateManager.color(0.4F, 0.8F, 1.0F, 0.5F);  // Cyan glow tint.
            drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x55FFFFFF);
            GlStateManager.disableBlend();
        }

        // Draw the optional icon on the left side of the button.
        if (icon != null) {
            mc.getTextureManager().bindTexture(icon);
            drawModalRectWithCustomSizedTexture(this.x + 4, this.y + 2, 0, 0, 16, 16, 16, 16);
        }

        // Render the button label centered on the button.
        int textColor = isSelected ? 0xFFFFAA00 : 0xFFFFFFFF;
        this.drawCenteredString(mc.fontRenderer, this.displayString,
                this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
    }
}