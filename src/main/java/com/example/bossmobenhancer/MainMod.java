package com.example.bossmobenhancer;

import com.example.bossmobenhancer.client.BossOverlayHandler;
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import com.example.bossmobenhancer.entities.EntityBossMinion;
import com.example.bossmobenhancer.items.ItemLunarBlessedApple;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

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

    // Reference to the custom Lunar Blessed Apple item.
    public static ItemLunarBlessedApple lunarBlessedApple;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Load scaling profiles and initialize configuration.
        File configDir = event.getModConfigurationDirectory();
        ScalingProfileLoader.load(configDir);
        File configFile = new File(configDir, "bossmobenhancer.cfg");
        ConfigHandler.init(configFile);
        System.out.println("[BossMobEnhancer] Loaded configuration from " + configFile.getAbsolutePath());

        // Register the Lunar Blessed Apple item.
        lunarBlessedApple = new ItemLunarBlessedApple();
        ForgeRegistries.ITEMS.register(lunarBlessedApple);
        System.out.println("[BossMobEnhancer] Registered item: " + lunarBlessedApple.getRegistryName());

        // Register our custom boss minion entity.
        EntityRegistry.registerModEntity(
                new ResourceLocation(MODID, "boss_minion"), // Unique resource location.
                EntityBossMinion.class,                      // Entity class.
                "BossMinion",                                // Entity name.
                0,                                           // Unique entity ID (within your mod).
                this,                                        // Mod instance.
                64,                                          // Tracking range in blocks.
                3,                                           // Update frequency.
                true                                         // Send velocity updates.
        );
        System.out.println("[BossMobEnhancer] Registered entity: BossMinion");

        // Client-side initialization: register item model and GUI event handlers.
        if (event.getSide() == Side.CLIENT) {
            ModelLoader.setCustomModelResourceLocation(
                    lunarBlessedApple,
                    0,
                    new ModelResourceLocation(lunarBlessedApple.getRegistryName(), "inventory")
            );
            System.out.println("[BossMobEnhancer] Client-side detected: GUI features enabled.");
            MinecraftForge.EVENT_BUS.register(new BossOverlayHandler());
        } else {
            System.out.println("[BossMobEnhancer] Server-side detected: skipping client-only GUI setup.");
        }
    }

    // onServerStart is intentionally left out since your mod already handles command registration elsewhere.
    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        // No command registration needed here.
    }
}