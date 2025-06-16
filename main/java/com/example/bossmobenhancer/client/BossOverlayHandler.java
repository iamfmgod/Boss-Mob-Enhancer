package com.example.bossmobenhancer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class BossOverlayHandler {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        // Only render during the ALL phase.
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);

        // Data check: Get the list of active boss mobs from BossRegistry.
        List<EntityLiving> bossMobs = BossRegistry.getActiveBossMobs(mc.world);
        if (bossMobs.isEmpty()) {
            return;
        }

        // For demonstration, we'll display data for the first boss in the list.
        EntityLiving boss = bossMobs.get(0);
        float healthPercentage = boss.getHealth() / boss.getMaxHealth();

        // Design & Layout: Define the health bar dimensions and position.
        int barWidth = 182;
        int barHeight = 5;
        int xPos = (res.getScaledWidth() - barWidth) / 2;
        int yPos = 10;

        // Draw a background bar (black).
        Gui.drawRect(xPos, yPos, xPos + barWidth, yPos + barHeight, 0xFF000000);
        // Draw the health bar with a thematic color (red in this example).
        int healthColor = 0xFFAA0000;
        Gui.drawRect(xPos, yPos, xPos + (int)(barWidth * healthPercentage), yPos + barHeight, healthColor);

        // Render the boss's name above the health bar.
        String bossName = boss.getName();
        int nameX = xPos + (barWidth / 2) - mc.fontRenderer.getStringWidth(bossName) / 2;
        int nameY = yPos - 12;
        // Use 0xFFFFFF for white color in place of TextFormatting.WHITE.getColor()
        mc.fontRenderer.drawStringWithShadow(bossName, nameX, nameY, 0xFFFFFF);
    }
}