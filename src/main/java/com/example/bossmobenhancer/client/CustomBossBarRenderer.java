package com.example.bossmobenhancer.client;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Custom boss‐bar overlay: draws a centered bar and boss name
 * using the entries in BossRegistry.
 */
@SideOnly(Side.CLIENT)
public class CustomBossBarRenderer {
    private static final Minecraft MC       = Minecraft.getMinecraft();
    private static final int BAR_WIDTH      = 180;
    private static final int BAR_HEIGHT     = 6;
    private static final int BORDER         = 2;
    private static final int Y_OFFSET       = 10;

    /**
     * Register this class on the client bus in ClientProxy:
     *   MinecraftForge.EVENT_BUS.register(new CustomBossBarRenderer());
     */
    @SubscribeEvent
    public void onRenderBossBar(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSHEALTH) return;

        // Cancel vanilla bar
        event.setCanceled(true);

        // No boss → nothing to draw
        if (!BossRegistry.hasActiveBoss()) return;

        // Fetch data from registry
        String name   = BossRegistry.getBossName();
        float  health = BossRegistry.getBossHealthPercent();

        ScaledResolution sr = new ScaledResolution(MC);
        int sw = sr.getScaledWidth();

        // Center bar at top
        int x0 = (sw - BAR_WIDTH) / 2;
        int y0 = Y_OFFSET;

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,       GlStateManager.DestFactor.ZERO
        );

        // Draw outer border
        drawRect(
                x0 - BORDER, y0 - BORDER,
                x0 + BAR_WIDTH + BORDER, y0 + BAR_HEIGHT + BORDER,
                0xAA000000
        );

        // Draw background
        drawRect(x0, y0, x0 + BAR_WIDTH, y0 + BAR_HEIGHT, 0xCC111111);

        // Draw fill
        int fill = Math.round(BAR_WIDTH * health);
        drawRect(x0, y0, x0 + fill, y0 + BAR_HEIGHT, 0xFFE55555);

        // Draw boss name above bar
        if (name != null && !name.isEmpty()) {
            int nameW = MC.fontRenderer.getStringWidth(name);
            MC.fontRenderer.drawStringWithShadow(
                    name,
                    (sw - nameW) / 2,
                    y0 - (MC.fontRenderer.FONT_HEIGHT + 3),
                    0xFFFFFF
            );
        }

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    /** Wrapper for drawRect */
    private static void drawRect(int l, int t, int r, int b, int color) {
        MC.ingameGUI.drawRect(l, t, r, b, color);
    }
}