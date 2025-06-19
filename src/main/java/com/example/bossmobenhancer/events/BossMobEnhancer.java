package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.MainMod; // Import your main mod so you can reference MODID
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.ai.SpecialAbilities;
import com.example.bossmobenhancer.ai.TerrainManipulator;
import com.example.bossmobenhancer.ai.MinionSpawner;
import com.example.bossmobenhancer.ai.ParticleEmitter;
import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.logging.Logger;

@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class BossMobEnhancer {
    private static final Random rand = new Random();
    private static final Logger logger = Logger.getLogger("BossMobEnhancer");

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityLiving)) return;

        EntityLiving mob = (EntityLiving) event.getEntity();
        if (!shouldEnhance(mob)) return;

        if (rand.nextFloat() >= ConfigHandler.enhancementChance) return;

        int tier = determineTier(mob);

        // Ensure server-side execution
        if (!mob.world.isRemote) {
            // Assign name tag
            String fancyName = NameGenerator.generateFantasyName(mob, tier);
            mob.setCustomNameTag(fancyName);
            mob.setAlwaysRenderNameTag(true);

            // Personality-driven enhancements
            if (ConfigHandler.enableBuffs) {
                SpecialAbilities.applyLoadout(mob, tier);
                logger.info("Applied abilities to: " + fancyName);
            }

            // Terrain & minion extras
            if (ConfigHandler.enableTerrain) {
                TerrainManipulator.tryModifyTerrain(mob, tier);
            }

            if (ConfigHandler.enableMinions) {
                MinionSpawner.trySpawnMinions(mob, tier);
            }

            // Visual feedback
            ParticleEmitter.emitParticles(mob, tier);
        }
    }

    private static boolean shouldEnhance(EntityLiving mob) {
        ResourceLocation key = EntityList.getKey(mob);
        if (key == null) return false;

        String id = key.toString();
        if (!ConfigHandler.whitelistMobs.isEmpty() && !ConfigHandler.whitelistMobs.contains(id)) return false;
        if (ConfigHandler.blacklistMobs.contains(id)) return false;

        return true;
    }

    private static int determineTier(EntityLiving mob) {
        int baseTier = (int) (mob.getMaxHealth() / 15.0);
        return Math.min(1 + baseTier, ConfigHandler.maxTier);
    }
}