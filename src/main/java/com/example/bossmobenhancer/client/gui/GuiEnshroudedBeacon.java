package com.example.bossmobenhancer.client.gui;

import com.example.bossmobenhancer.network.PacketHandler;
import com.example.bossmobenhancer.network.MessageSetCurse;
import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiEnshroudedBeacon extends GuiContainer {

    private static final ResourceLocation BEACON_GUI_TEXTURE =
            new ResourceLocation("bossmobenhancer", "textures/gui/enshrouded_beacon.png");

    // Single texture atlas for all curse buttons.
    private static final ResourceLocation BUTTONS_ATLAS =
            new ResourceLocation("bossmobenhancer", "textures/gui/buttons_atlas.png");

    private final TileEntityEnshroudedBeacon beacon;

    // Array of curse names.
    private static final String[] CURSE_NAMES = new String[] {
            "Withering Touch", "Blindness Hex", "Sluggish Curse", "Vampiric Fog",
            "Curse Of Static", "Terror Pulse", "Hex Of Hunger", "Doombrand"
    };

    public GuiEnshroudedBeacon(InventoryPlayer playerInv, TileEntityEnshroudedBeacon beacon) {
        // Create a dummy container since there are no inventory slots to show.
        super(new Container() {
            @Override
            public boolean canInteractWith(net.minecraft.entity.player.EntityPlayer playerIn) {
                return true;
            }
        });
        this.beacon = beacon;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();

        // Layout constants – adjust these if needed.
        int startX = this.guiLeft + 32;
        int startY = this.guiTop + 30;
        int buttonWidth = 80;   // Expanded width to better fit curse names.
        int buttonHeight = 20;
        int spacingX = 85;
        int spacingY = 24;

        for (int i = 0; i < CURSE_NAMES.length; i++) {
            String label = CURSE_NAMES[i];
            int col = i % 2;
            int row = i / 2;

            int x = startX + col * spacingX;
            int y = startY + row * spacingY;

            // Create the curse button, passing in the button's index.
            this.buttonList.add(new CurseButton(i, x, y, buttonWidth, buttonHeight, label, i, isSelected(i)));
        }
    }

    private boolean isSelected(int id) {
        return beacon.getCurseId() == id;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof CurseButton) {
            PacketHandler.INSTANCE.sendToServer(new MessageSetCurse(button.id));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString("Enshrouded Beacon", 8, 6, 0x404040);
        this.fontRenderer.drawString("Inventory", 8, this.ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1f, 1f, 1f, 1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BEACON_GUI_TEXTURE);

        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
    }

    // Inner class for the curse button, now pulling its image from the atlas.
    public static class CurseButton extends GuiButton {

        // The index determines which cell of the atlas to use.
        private final int curseIndex;
        private final boolean isSelected;

        public CurseButton(int id, int x, int y, int width, int height, String displayString, int curseIndex, boolean isSelected) {
            super(id, x, y, width, height, displayString);
            this.curseIndex = curseIndex;
            this.isSelected = isSelected;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible)
                return;

            // Bind the single atlas texture.
            mc.getTextureManager().bindTexture(BUTTONS_ATLAS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            // Update hover state.
            this.hovered = mouseX >= this.x && mouseY >= this.y &&
                    mouseX < this.x + this.width && mouseY < this.y + this.height;

            // Compute atlas cell for this button based on its index.
            // (Assuming the atlas is arranged in 2 columns and that each cell is exactly the size of the button.)
            int colIndex = curseIndex % 2;
            int rowIndex = curseIndex / 2;
            int texX = colIndex * this.width;
            int texY = rowIndex * this.height;

            // Draw this button's subtexture from the atlas.
            this.drawTexturedModalRect(this.x, this.y, texX, texY, this.width, this.height);

            // If hovered, draw a semi-transparent overlay.
            if (this.hovered) {
                GlStateManager.enableBlend();
                GlStateManager.color(1F, 1F, 1F, 0.3F);
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x60FFFFFF);
                GlStateManager.disableBlend();
            }

            // If selected, draw an additional glowing overlay.
            if (isSelected) {
                GlStateManager.enableBlend();
                GlStateManager.color(0.4F, 0.8F, 1.0F, 0.5F);
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x55FFFFFF);
                GlStateManager.disableBlend();
            }

            // Draw the button label (centered).
            int textColor = 0xFFFFFFFF; // White text.
            this.drawCenteredString(mc.fontRenderer, this.displayString,
                    this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
        }
    }
}