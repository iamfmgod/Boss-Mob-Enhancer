package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.client.BossRegistry; // Ensure your BossRegistry returns active boss entities.
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.util.text.TextFormatting;
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

        // Retrieve the boss phase from its NBT data.
        int phase = boss.getEntityData().getInteger("bossPhase");
        // If no phase is defined, assume phase 1.
        if (phase == 0) {
            phase = 1;
        }

        // Compose a display name that includes the boss's name and current phase.
        String bossName = boss.getName() + " " + TextFormatting.RED + "[Phase " + phase + "]";

        // Design & Layout: Define the boss bar dimensions and position (centered horizontally).
        int barWidth = 182;   // Standard boss bar width.
        int barHeight = 5;    // Boss bar height.
        int xPos = (res.getScaledWidth() - barWidth) / 2;
        int yPos = 10;        // Vertical offset from the top of the screen.

        // Draw a background bar (black).
        Gui.drawRect(xPos, yPos, xPos + barWidth, yPos + barHeight, 0xFF000000);
        // Draw the health bar with a thematic color (red in this example). The width is proportional to the boss's health.
        int healthColor = 0xFFAA0000;
        Gui.drawRect(xPos, yPos, xPos + (int)(barWidth * healthPercentage), yPos + barHeight, healthColor);

        // Render the boss's name (with phase info) above the health bar.
        int nameX = xPos + (barWidth / 2) - mc.fontRenderer.getStringWidth(bossName) / 2;
        int nameY = yPos - 12;
        mc.fontRenderer.drawStringWithShadow(bossName, nameX, nameY, 0xFFFFFF);
    }
}