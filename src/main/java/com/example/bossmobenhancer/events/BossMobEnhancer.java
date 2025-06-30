package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.ai.MinionSpawner;
import com.example.bossmobenhancer.ai.ParticleEmitter;
import com.example.bossmobenhancer.ai.SpecialAbilities;
import com.example.bossmobenhancer.ai.TerrainManipulator;
import com.example.bossmobenhancer.utils.NameGenerator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class BossMobEnhancer {
    private static final Logger LOGGER = LogManager.getLogger(MainMod.MODID);
    private static final Random  RAND   = new Random();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityLiving)) return;
        EntityLiving mob = (EntityLiving) event.getEntity();

        // Only run on server
        if (mob.world.isRemote) return;

        // Master on/off switch
        if (!ConfigHandler.enableScaling) return;

        // Mob‐filter checks
        ResourceLocation key = EntityList.getKey(mob);
        if (key == null) return;
        String id = key.toString();
        if (!ConfigHandler.whitelistMobs.isEmpty() &&
                !ConfigHandler.whitelistMobs.contains(id)) return;
        if (ConfigHandler.blacklistMobs.contains(id)) return;

        // Enhancement roll
        if (RAND.nextFloat() >= ConfigHandler.enhancementChance) return;

        int tier = determineTier(mob);

        // Assign a fanciful boss name
        String fancyName = NameGenerator.generateFantasyName(mob, tier);
        mob.setCustomNameTag(fancyName);
        mob.setAlwaysRenderNameTag(true);

        LOGGER.info("Enhancing {} (Tier {})", fancyName, tier);

        // Apply configured modules
        if (ConfigHandler.enableBuffs)    SpecialAbilities.applyLoadout(mob, tier);
        if (ConfigHandler.enableTerrain)  TerrainManipulator.tryModifyTerrain(mob, tier);
        if (ConfigHandler.enableMinions)  MinionSpawner.trySpawnMinions(mob, tier);

        // Only emit particles on the client side
        if (mob.world.isRemote) {
            com.example.bossmobenhancer.ai.ParticleEmitter.emitParticles(mob, tier);
        }
    }

    /**
     * Derive tier from max health, then cap to [1, maxTier].
     */
    private static int determineTier(EntityLiving mob) {
        // Simple: one tier per 15 HP
        int base = (int)(mob.getMaxHealth() / 15.0);
        int tier = 1 + base;
        if (tier < 1) tier = 1;
        if (tier > ConfigHandler.maxTier) tier = ConfigHandler.maxTier;
        return tier;
    }
}