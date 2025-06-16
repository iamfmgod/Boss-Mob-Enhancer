package com.example.bossmobenhancer;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.ai.SpecialAbilities;
import com.example.bossmobenhancer.ai.TerrainManipulator;
import com.example.bossmobenhancer.ai.MinionSpawner;
import com.example.bossmobenhancer.ai.ParticleEmitter;
import com.example.bossmobenhancer.utils.NameGenerator;

import java.util.Random;

@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class BossMobEnhancer {
    private static final Random rand = new Random();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityLiving)) return;

        EntityLiving mob = (EntityLiving) event.getEntity();
        if (!shouldEnhance(mob)) return;

        if (rand.nextFloat() >= ConfigHandler.enhancementChance) return;

        int tier = determineTier(mob);

        // Assign name tag
        String fancyName = NameGenerator.generateFantasyName(mob, tier);
        mob.setCustomNameTag(fancyName);
        mob.setAlwaysRenderNameTag(true);

        // Personality-driven enhancements
        if (ConfigHandler.enableBuffs) {
            SpecialAbilities.applyLoadout(mob, tier);
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

    private static boolean shouldEnhance(EntityLiving mob) {
        ResourceLocation key = EntityList.getKey(mob);
        if (key == null) return false;

        String id = key.toString();
        if (!ConfigHandler.whitelistMobs.isEmpty() && !ConfigHandler.whitelistMobs.contains(id)) return false;
        if (ConfigHandler.blacklistMobs.contains(id)) return false;

        return true;
    }

    private static int determineTier(EntityLiving mob) {
        return Math.min(1 + (int) (mob.getMaxHealth() / 20.0f), ConfigHandler.maxTier);
    }
}