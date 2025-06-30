package com.example.bossmobenhancer.ai;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

/**
 * Alters terrain around a boss mob according to its tier.
 */
public class TerrainManipulator {
    private static final Logger LOGGER            = LogManager.getLogger(MainMod.MODID);
    private static final Random RAND              = new Random();
    private static final String  COOLDOWN_KEY     = "bossTerrainCooldown";
    private static final long    COOLDOWN_TICKS   = 100L; // ≈5 seconds

    /**
     * Attempt a terrain effect burst around the boss.
     * Only runs server‐side and only once per cooldown.
     */
    public static void tryModifyTerrain(EntityLiving boss, int tier) {
        if (boss == null) {
            LOGGER.warn("tryModifyTerrain called with null boss");
            return;
        }
        World world = boss.world;
        if (world == null || world.isRemote || tier < 2) return;

        // enforce cooldown
        NBTTagCompound data = boss.getEntityData();
        long now  = world.getTotalWorldTime();
        long next = data.getLong(COOLDOWN_KEY);
        if (now < next) return;
        data.setLong(COOLDOWN_KEY, now + COOLDOWN_TICKS);

        BlockPos base = boss.getPosition();
        int loops = 5 + RAND.nextInt(5);

        for (int i = 0; i < loops; i++) {
            int dx = RAND.nextInt(9) - 4, dz = RAND.nextInt(9) - 4;
            BlockPos pos = base.add(dx, -1, dz);
            IBlockState state = world.getBlockState(pos);
            int effect = RAND.nextInt(17);

            switch (effect) {
                case 0: // Dirt → Coarse Dirt
                    if ((state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT)
                        && !world.isAirBlock(pos)) {
                        world.setBlockState(pos,
                                Blocks.DIRT.getStateFromMeta(BlockDirt.DirtType.COARSE_DIRT.getMetadata()), 2);
                    }
                    break;
                case 1: // Stone → Gold Ore
                    if (state.getBlock() == Blocks.STONE && !world.isAirBlock(pos)) {
                        world.setBlockState(pos, Blocks.GOLD_ORE.getDefaultState(), 2);
                    }
                    break;
                case 2: // Cobble → Mossy Cobble
                    if (state.getBlock() == Blocks.COBBLESTONE && !world.isAirBlock(pos)) {
                        world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                    }
                    break;
                case 3: // Air → Water (Tier 3+)
                    if (tier >= 3 && world.isAirBlock(pos)) {
                        world.setBlockState(pos, Blocks.WATER.getDefaultState(), 3);
                    }
                    break;
                case 4: // Tall Grass
                    if ((state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT)
                            && world.isAirBlock(pos.up())) {
                        world.setBlockState(pos.up(), Blocks.TALLGRASS.getDefaultState(), 2);
                    }
                    break;
                case 5: // Snow Layer
                    if (world.isAirBlock(pos.up())) {
                        world.setBlockState(pos.up(), Blocks.SNOW_LAYER.getDefaultState(), 2);
                    }
                    break;
                case 6: // Tiny Explosion (Tier 4+)
                    if (tier >= 4) {
                        world.createExplosion(
                                boss,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                0.5F, false
                        );
                    }
                    break;
                case 7: // Random Wool (Tier 3+)
                    if (tier >= 3 && world.isAirBlock(pos)) {
                        int color = RAND.nextInt(16);
                        world.setBlockState(pos, Blocks.WOOL.getStateFromMeta(color), 2);
                    }
                    break;
                case 8: // Podzol (Tier 3+)
                    if (tier >= 3 && state.getBlock() == Blocks.GRASS && !world.isAirBlock(pos)) {
                        world.setBlockState(pos,
                                Blocks.DIRT.getStateFromMeta(BlockDirt.DirtType.PODZOL.getMetadata()), 2);
                    }
                    break;
                case 9: // Falling Block Barrage (Tier 3+)
                    if (tier >= 3) {
                        BlockPos spawn = pos.up(3);
                        IBlockState[] choices = {
                                Blocks.SAND.getDefaultState(),
                                Blocks.GRAVEL.getDefaultState(),
                                Blocks.CLAY.getDefaultState()
                        };
                        IBlockState pick = choices[RAND.nextInt(choices.length)];
                        EntityFallingBlock fb = new EntityFallingBlock(
                                world, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, pick
                        );
                        world.spawnEntity(fb);
                    }
                    break;
                case 10: // Corruption: Rock & Dirt → Netherrack / Mycelium
                    if (state.getBlock() == Blocks.STONE && !world.isAirBlock(pos)) {
                        world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 2);
                        triggerCorruptionEffects(world, pos, boss);
                    } else if (state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT) {
                        world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState(), 2);
                        triggerCorruptionEffects(world, pos, boss);
                    }
                    break;
                case 11: // Obsidian Corruption
                    if (!world.isAirBlock(pos) && RAND.nextBoolean()) {
                        world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState(), 2);
                        triggerCorruptionEffects(world, pos, boss);
                    }
                    break;
                case 12: // Soul Sand Barrage (Tier 3+)
                    if (tier >= 3) {
                        BlockPos s = pos.up(2);
                        EntityFallingBlock sb = new EntityFallingBlock(
                                world, s.getX() + 0.5, s.getY(), s.getZ() + 0.5,
                                Blocks.SOUL_SAND.getDefaultState()
                        );
                        world.spawnEntity(sb);
                        triggerCorruptionEffects(world, pos, boss);
                    }
                    break;
                case 13: // Packed Ice (Tier 3+)
                    if (tier >= 3 && state.getBlock() == Blocks.WATER && !world.isAirBlock(pos)) {
                        world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 2);
                        triggerCorruptionEffects(world, pos, boss);
                    }
                    break;
                case 14: // Diamond Block Surprise (Tier 4+)
                    if (tier >= 4 && state.getBlock() == Blocks.STONE && RAND.nextBoolean()) {
                        world.setBlockState(pos, Blocks.DIAMOND_BLOCK.getDefaultState(), 2);
                        triggerCorruptionEffects(world, pos, boss);
                    }
                    break;
                case 15: // Mushroom Bloom (Tier 3+)
                    if (tier >= 3 && (state.getBlock() instanceof BlockLog || state.getBlock() instanceof BlockLeaves)) {
                        IBlockState ms = RAND.nextBoolean()
                                ? Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState()
                                : Blocks.RED_MUSHROOM_BLOCK.getDefaultState();
                        world.setBlockState(pos, ms, 2);
                        triggerCorruptionEffects(world, pos, boss);
                    }
                    break;
                case 16: // Farmland Erosion → Dirt
                    if (state.getBlock() == Blocks.FARMLAND && !world.isAirBlock(pos)) {
                        world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
                        triggerCorruptionEffects(world, pos, boss);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // server+client gateway for corruption effects
    private static void triggerCorruptionEffects(World world, BlockPos pos, EntityLiving boss) {
        // Only apply harm, do not spawn particles
        if (!world.isRemote) {
            applyCorruptionHarm(world, pos, boss);
        }
    }


    /**
     * Slow and damage nearby entities.
     * Server‐only; annotated and guarded.
     */
    public static void applyCorruptionHarm(World world, BlockPos pos, EntityLiving boss) {
        if (world == null || world.isRemote) return;
        double radius = 2.0;
        AxisAlignedBB box = new AxisAlignedBB(pos).grow(radius);
        List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
        for (EntityLivingBase e : list) {
            if (e != boss) {
                e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
                e.attackEntityFrom(DamageSource.MAGIC, 2.0F);
            }
        }
    }
}