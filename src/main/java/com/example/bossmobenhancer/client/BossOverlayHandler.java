package com.example.bossmobenhancer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Renders a single boss bar + name at the top of the screen
 * based on the current boss in BossRegistry.
 */
@SideOnly(Side.CLIENT)
public class BossOverlayHandler {

    private static final int BAR_WIDTH  = 182;
    private static final int BAR_HEIGHT = 6;
    private static final int X_OFFSET   = 0;   // centered
    private static final int Y_OFFSET   = 10;  // pixels down from top

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        // only intercept the boss‐health element
        if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            return;
        }

        // cancel vanilla boss bar
        event.setCanceled(true);

        // nothing to draw if no boss is active
        if (!BossRegistry.hasActiveBoss()) {
            return;
        }

        // fetch boss info
        String name       = BossRegistry.getBossName();
        float  healthPct  = BossRegistry.getBossHealthPercent();  // 0.0 – 1.0

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);
        int sw = res.getScaledWidth();

        // calculate positions
        int x = (sw - BAR_WIDTH) / 2 + X_OFFSET;
        int y = Y_OFFSET;

        // draw background
        Gui.drawRect(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xAA000000);

        // draw fill
        int fill = Math.round(BAR_WIDTH * healthPct);
        Gui.drawRect(x, y, x + fill, y + BAR_HEIGHT, 0xFFFF4444);

        // draw boss name centered above bar
        if (name != null && !name.isEmpty()) {
            int nameW = mc.fontRenderer.getStringWidth(name);
            int nameX = (sw - nameW) / 2;
            int nameY = y - (mc.fontRenderer.FONT_HEIGHT + 3);
            mc.fontRenderer.drawStringWithShadow(name, nameX, nameY, 0xFFFFFF);
        }
    }
}