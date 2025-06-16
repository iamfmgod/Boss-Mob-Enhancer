package com.example.bossmobenhancer;

import com.example.bossmobenhancer.client.BossOverlayHandler;
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import com.example.bossmobenhancer.commands.BossProfileCommand;
import com.example.bossmobenhancer.items.ItemLunarBlessedApple;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;

@Mod(
        modid = MainMod.MODID,
        name = MainMod.NAME,
        version = MainMod.VERSION,
        guiFactory = "com.example.bossmobenhancer.client.GuiFactory"
)
public class MainMod {
    public static final String MODID = "bossmobenhancer";
    public static final String NAME = "Boss Mob Enhancer";
    public static final String VERSION = "1.0.0";

    // Store a reference to your custom Lunar Blessed Apple item.
    public static ItemLunarBlessedApple lunarBlessedApple;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Load scaling profiles and initialize configuration.
        File configDir = event.getModConfigurationDirectory();
        ScalingProfileLoader.load(configDir);
        File configFile = new File(configDir, "bossmobenhancer.cfg");
        ConfigHandler.init(configFile);

        // Register the Lunar Blessed Apple item.
        lunarBlessedApple = new ItemLunarBlessedApple();
        ForgeRegistries.ITEMS.register(lunarBlessedApple);

        // Client-side registration: register item model and client-specific event handlers.
        if (FMLLaunchHandler.side().isClient()) {
            // Register the Lunar Blessed Apple model so that the custom texture is used.
            ModelLoader.setCustomModelResourceLocation(
                    lunarBlessedApple,
                    0,
                    new ModelResourceLocation(lunarBlessedApple.getRegistryName(), "inventory")
            );
            System.out.println("[BossMobEnhancer] Client-side detected: GUI features enabled.");
            // Register the Boss Overlay event handler.
            MinecraftForge.EVENT_BUS.register(new BossOverlayHandler());
        } else {
            System.out.println("[BossMobEnhancer] Server-side detected: skipping client-only GUI setup.");
        }
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        // Register the server command to change boss scaling profiles.
        event.registerServerCommand(new BossProfileCommand());
    }
}