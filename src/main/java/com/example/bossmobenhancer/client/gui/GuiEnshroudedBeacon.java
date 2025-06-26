package com.example.bossmobenhancer.client.gui;

import com.example.bossmobenhancer.container.ContainerEnshroudedBeacon;
import com.example.bossmobenhancer.network.MessageSetCurse;
import com.example.bossmobenhancer.network.PacketHandler;
import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Client-only GUI for the Enshrouded Beacon block.
 */
@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class GuiEnshroudedBeacon extends GuiContainer {

    private static final ResourceLocation BG =
            new ResourceLocation("bossmobenhancer", "textures/gui/enshrouded_beacon.png");
    private static final ResourceLocation ATLAS =
            new ResourceLocation("bossmobenhancer", "textures/gui/buttons_atlas.png");

    private final TileEntityEnshroudedBeacon beacon;
    private static final String[] NAMES = {
            "Withering Touch", "Blindness Hex", "Sluggish Curse", "Vampiric Fog",
            "Curse Of Static", "Terror Pulse", "Hex Of Hunger", "Doombrand"
    };

    public GuiEnshroudedBeacon(InventoryPlayer inv, TileEntityEnshroudedBeacon beacon) {
        super(new ContainerEnshroudedBeacon(inv, beacon));
        this.beacon = beacon;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();

        int startX = guiLeft + 32, startY = guiTop + 30;
        int w = 80, h = 20, dx = 85, dy = 24;

        for (int i = 0; i < NAMES.length; i++) {
            int col = i % 2, row = i / 2;
            buttonList.add(new CurseButton(
                    i,
                    startX + col * dx,
                    startY + row * dy,
                    w, h,
                    NAMES[i]
            ));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof CurseButton) {
            PacketHandler.INSTANCE.sendToServer(new MessageSetCurse(button.id));
            // Immediately refresh highlights
            int current = beacon.getCurseId();
            for (GuiButton b : buttonList) {
                if (b instanceof CurseButton) {
                    ((CurseButton) b).updateHighlight(current);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString("Enshrouded Beacon", 8, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks,
                                                   int mouseX,
                                                   int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BG);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    /**
     * Button that pulls its graphic from an atlas,
     * shows a hover overlay, and highlights when selected.
     */
    @SideOnly(Side.CLIENT)
    public static class CurseButton extends GuiButton {
        public CurseButton(int id, int x, int y, int width, int height, String txt) {
            super(id, x, y, width, height, txt);
            updateHighlight(0);
        }

        /** Call this after the beacon updates its curseId */
        public void updateHighlight(int selectedId) {
            // Selected = tinted text, otherwise white
            this.packedFGColour = (this.id == selectedId)
                    ? 0xFFAAAA55
                    : 0xFFFFFFFF;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) return;

            mc.getTextureManager().bindTexture(ATLAS);
            hovered = mouseX >= x && mouseY >= y
                    && mouseX < x + width && mouseY < y + height;

            int texX = (id % 2) * width;
            int texY = (id / 2) * height;
            drawTexturedModalRect(x, y, texX, texY, width, height);

            if (isSelected()) {
                drawRect(x, y, x + width, y + height, 0x55FFFFFF);
            } else if (hovered) {
                drawRect(x, y, x + width, y + height, 0x40FFFFFF);
            }

            drawCenteredString(
                    mc.fontRenderer,
                    displayString,
                    x + width / 2,
                    y + (height - 8) / 2,
                    packedFGColour
            );
        }

        private boolean isSelected() {
            return packedFGColour != 0xFFFFFFFF;
        }
    }
}