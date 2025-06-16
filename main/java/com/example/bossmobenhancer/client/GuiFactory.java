package com.example.bossmobenhancer.client;

import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Collections;
import java.util.Set;

/**
 * GuiFactory links your mod to a custom configuration GUI.
 * This implementation returns an instance of ExtendedBossConfigGui,
 * which exposes a multi-category configuration screen for Boss Mob Enhancer.
 */
public class GuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
        // No additional initialization is needed.
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    /**
     * Creates and returns the mod's configuration GUI.
     * This method now instantiates ExtendedBossConfigGui to
     * provide a comprehensive options interface.
     *
     * @param parentScreen the parent GuiScreen.
     * @return an instance of ExtendedBossConfigGui.
     */
    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ExtendedBossConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return Collections.emptySet();
    }
}