package com.example.bossmobenhancer;

import com.example.bossmobenhancer.blocks.BlockEnshroudedBeacon;
import com.example.bossmobenhancer.client.BossOverlayHandler;
import com.example.bossmobenhancer.client.GuiHandler;
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import com.example.bossmobenhancer.entities.EntityBossMinion;
import com.example.bossmobenhancer.items.ItemLunarBlessedApple;
import com.example.bossmobenhancer.network.PacketHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
// import net.minecraftforge.fml.common.registry.GameRegistry; // Not used here.
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

    @Mod.Instance(MODID)
    public static MainMod instance;

    public static final BlockEnshroudedBeacon ENSHROUDED_BEACON = new BlockEnshroudedBeacon();
    public static final Item LUNAR_BLESSED_APPLE = new ItemLunarBlessedApple();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configDir = event.getModConfigurationDirectory();

        // Load scaling profiles and core config.
        ScalingProfileLoader.load(configDir);
        ConfigHandler.init(new File(configDir, "bossmobenhancer.cfg"));
        System.out.println("[BossMobEnhancer] Configuration loaded.");

        // Register custom block and item.
        registerBlock(ENSHROUDED_BEACON, "enshrouded_beacon");
        registerItem(LUNAR_BLESSED_APPLE, "lunar_blessed_apple");

        // Tile entity registration has been removed as the block does not support TEs.
        // Register minion entity.
        registerEntity(EntityBossMinion.class, "boss_minion", 0, 64, 3, true);

        // Register GUI and network packets.
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        PacketHandler.init();

        // Client-side extras: register models and overlay.
        if (event.getSide() == Side.CLIENT) {
            registerModels();
            MinecraftForge.EVENT_BUS.register(new BossOverlayHandler());
            System.out.println("[BossMobEnhancer] GUI features enabled.");
        }
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        System.out.println("[BossMobEnhancer] Server initialization complete.");
    }

    private static void registerBlock(BlockEnshroudedBeacon block, String name) {
        block.setRegistryName(new ResourceLocation(MODID, name));
        // Do not call setUnlocalizedName here – rely on language files for the display name.
        ForgeRegistries.BLOCKS.register(block);
        ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        System.out.println("[BossMobEnhancer] Registered block: " + name);
    }

    private static void registerItem(Item item, String name) {
        item.setRegistryName(new ResourceLocation(MODID, name));
        ForgeRegistries.ITEMS.register(item);
        System.out.println("[BossMobEnhancer] Registered item: " + name);
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int id, int trackingRange, int updateFrequency, boolean sendVelocityUpdates) {
        EntityRegistry.registerModEntity(
                new ResourceLocation(MODID, name),
                entityClass,
                name,
                id,
                MODID,
                trackingRange,
                updateFrequency,
                sendVelocityUpdates
        );
        System.out.println("[BossMobEnhancer] Registered entity: " + name);
    }

    private static void registerModels() {
        // Load models for inventory rendering.
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ENSHROUDED_BEACON), 0,
                new ModelResourceLocation(ENSHROUDED_BEACON.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(LUNAR_BLESSED_APPLE, 0,
                new ModelResourceLocation(LUNAR_BLESSED_APPLE.getRegistryName(), "inventory"));
        System.out.println("[BossMobEnhancer] Item and block models registered.");
    }
}