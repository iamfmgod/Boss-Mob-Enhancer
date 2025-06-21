package com.example.bossmobenhancer.client;

import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.Set;

// If GuiMainConfigScreen is in a different package, remember to import it:
// import com.example.bossmobenhancer.client.GuiMainConfigScreen;

@SideOnly(Side.CLIENT)
public class GuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
        // No additional initialization needed
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        // ✅ Launches your mod's main config GUI from the Mod List
        return new GuiMainConfigScreen(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return Collections.emptySet(); // No runtime options shown in config screens
    }
}