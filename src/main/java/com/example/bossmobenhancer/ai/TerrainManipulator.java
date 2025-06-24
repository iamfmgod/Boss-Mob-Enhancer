package com.example.bossmobenhancer.ai;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityFallingBlock;  // Correct import for 1.12.2
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TerrainManipulator {
    private static final Random rand = new Random();
    // Cooldown key stored in mob's NBT data.
    private static final String COOLDOWN_KEY = "bossTerrainCooldown";
    // Cooldown duration in ticks (100 ticks = approx. 5 seconds).
    private static final long COOLDOWN_TICKS = 100;

    public static void tryModifyTerrain(EntityLiving mob, int tier) {
        if (tier < 2) return;

        World world = mob.world;
        long currentTime = world.getTotalWorldTime();
        if (mob.getEntityData().hasKey(COOLDOWN_KEY)) {
            long nextAllowedTime = mob.getEntityData().getLong(COOLDOWN_KEY);
            if (currentTime < nextAllowedTime) return;
        }
        mob.getEntityData().setLong(COOLDOWN_KEY, currentTime + COOLDOWN_TICKS);

        BlockPos mobPos = mob.getPosition();
        int iterations = 5 + rand.nextInt(5);
        for (int i = 0; i < iterations; i++) {
            int dx = rand.nextInt(9) - 4;  // Range: -4 to +4
            int dz = rand.nextInt(9) - 4;
            BlockPos targetPos = mobPos.add(dx, -1, dz);  // Target block is one below the mob.
            IBlockState state = world.getBlockState(targetPos);
            int effectChoice = rand.nextInt(17);  // Choose one effect out of 17 possibilities
            switch (effectChoice) {
                case 0:
                    // Convert grass or dirt to coarse dirt.
                    if (state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT) {
                        world.setBlockState(targetPos,
                                Blocks.DIRT.getStateFromMeta(BlockDirt.DirtType.COARSE_DIRT.getMetadata()), 2);
                    }
                    break;
                case 1:
                    // Transform stone into gold ore.
                    if (state.getBlock() == Blocks.STONE) {
                        world.setBlockState(targetPos, Blocks.GOLD_ORE.getDefaultState(), 2);
                    }
                    break;
                case 2:
                    // Transform cobblestone into mossy cobblestone.
                    if (state.getBlock() == Blocks.COBBLESTONE) {
                        world.setBlockState(targetPos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                    }
                    break;
                case 3:
                    // Tier 3+: if air, fill with water.
                    if (tier >= 3 && world.isAirBlock(targetPos)) {
                        world.setBlockState(targetPos, Blocks.WATER.getDefaultState(), 3);
                    }
                    break;
                case 4:
                    // Spawn tall grass on top if the below block is grass or dirt and space is empty.
                    if ((state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT)
                            && world.isAirBlock(targetPos.up())) {
                        world.setBlockState(targetPos.up(), Blocks.TALLGRASS.getDefaultState(), 2);
                    }
                    break;
                case 5:
                    // Place a snow layer if block above is air.
                    if (world.isAirBlock(targetPos.up())) {
                        world.setBlockState(targetPos.up(), Blocks.SNOW_LAYER.getDefaultState(), 2);
                    }
                    break;
                case 6:
                    // Tier 4+: Small explosion simulating a ground tremor.
                    if (tier >= 4 && !world.isRemote) {
                        world.createExplosion(mob,
                                targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                                0.5F, false);
                    }
                    break;
                case 7:
                    // Tier 3+: If air, place a random colored wool block.
                    if (tier >= 3 && world.isAirBlock(targetPos)) {
                        int woolColor = rand.nextInt(16);
                        world.setBlockState(targetPos, Blocks.WOOL.getStateFromMeta(woolColor), 2);
                    }
                    break;
                case 8:
                    // Tier 3+: Transform grass into podzol.
                    if (tier >= 3 && state.getBlock() == Blocks.GRASS) {
                        world.setBlockState(targetPos,
                                Blocks.DIRT.getStateFromMeta(BlockDirt.DirtType.PODZOL.getMetadata()), 2);
                    }
                    break;
                case 9:
                    // Tier 3+: Spawn a falling block barrage with random block types.
                    if (tier >= 3 && !world.isRemote) {
                        BlockPos spawnPos = targetPos.up(3);
                        IBlockState[] possibleStates = new IBlockState[] {
                                Blocks.SAND.getDefaultState(),
                                Blocks.GRAVEL.getDefaultState(),
                                Blocks.CLAY.getDefaultState()
                        };
                        IBlockState fallingState = possibleStates[rand.nextInt(possibleStates.length)];
                        EntityFallingBlock fallingBlock = new EntityFallingBlock(world,
                                spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, fallingState);
                        world.spawnEntity(fallingBlock);
                    }
                    break;
                case 10:
                    // Corruption Effect: convert stone to netherrack or grass/dirt to mycelium.
                    if (state.getBlock() == Blocks.STONE) {
                        world.setBlockState(targetPos, Blocks.NETHERRACK.getDefaultState(), 2);
                        triggerCorruptionEffects(world, targetPos, mob);
                    } else if (state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT) {
                        world.setBlockState(targetPos, Blocks.MYCELIUM.getDefaultState(), 2);
                        triggerCorruptionEffects(world, targetPos, mob);
                    }
                    break;
                case 11:
                    // Withered Corruption: 50% chance to convert any non-air block to obsidian.
                    if (!world.isAirBlock(targetPos) && rand.nextBoolean()) {
                        world.setBlockState(targetPos, Blocks.OBSIDIAN.getDefaultState(), 2);
                        triggerCorruptionEffects(world, targetPos, mob);
                    }
                    break;
                case 12:
                    // Chaotic Corruption: spawn a falling soul sand block (tier 3+).
                    if (tier >= 3 && !world.isRemote) {
                        BlockPos spawnPos = targetPos.up(2);
                        EntityFallingBlock fb = new EntityFallingBlock(world,
                                spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                                Blocks.SOUL_SAND.getDefaultState());
                        world.spawnEntity(fb);
                        triggerCorruptionEffects(world, targetPos, mob);
                    }
                    break;
                case 13:
                    // Frozen Domain: if block is water, change to packed ice (tier 3+).
                    if (tier >= 3 && state.getBlock() == Blocks.WATER) {
                        world.setBlockState(targetPos, Blocks.PACKED_ICE.getDefaultState(), 2);
                        triggerCorruptionEffects(world, targetPos, mob);
                    }
                    break;
                case 14:
                    // Crystal Growth: occasionally convert stone into a diamond block (tier 4+).
                    if (tier >= 4 && state.getBlock() == Blocks.STONE && rand.nextBoolean()) {
                        world.setBlockState(targetPos, Blocks.DIAMOND_BLOCK.getDefaultState(), 2);
                        triggerCorruptionEffects(world, targetPos, mob);
                    }
                    break;
                case 15:
                    // Fungal Bloom: if target is a log or leaves, replace with a mushroom block (tier 3+).
                    if (tier >= 3 && (state.getBlock() instanceof BlockLog || state.getBlock() instanceof BlockLeaves)) {
                        IBlockState mushroomState = rand.nextBoolean() ?
                                Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState() :
                                Blocks.RED_MUSHROOM_BLOCK.getDefaultState();
                        world.setBlockState(targetPos, mushroomState, 2);
                        triggerCorruptionEffects(world, targetPos, mob);
                    }
                    break;
                case 16:
                    // Eroding Earth: if farmland, convert to dirt.
                    if (state.getBlock() == Blocks.FARMLAND) {
                        world.setBlockState(targetPos, Blocks.DIRT.getDefaultState(), 2);
                        triggerCorruptionEffects(world, targetPos, mob);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static void triggerCorruptionEffects(World world, BlockPos pos, EntityLiving boss) {
        spawnCorruptionParticles(world, pos);
        if (!world.isRemote) {
            applyCorruptionHarm(world, pos, boss);
        }
    }

    private static void spawnCorruptionParticles(World world, BlockPos pos) {
        if (world.isRemote) {
            for (int j = 0; j < 10; j++) {
                double offsetX = (rand.nextDouble() - 0.5) * 0.5;
                double offsetY = rand.nextDouble() * 0.5;
                double offsetZ = (rand.nextDouble() - 0.5) * 0.5;
                world.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                        pos.getX() + 0.5 + offsetX,
                        pos.getY() + 0.5 + offsetY,
                        pos.getZ() + 0.5 + offsetZ,
                        0, 0, 0);
            }
        }
    }

    private static void applyCorruptionHarm(World world, BlockPos pos, EntityLiving boss) {
        double radius = 2.0; // Declare the radius variable before using it.
        AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(radius);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        for (EntityLivingBase entity : entities) {
            if (entity != boss) {
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
                entity.attackEntityFrom(DamageSource.MAGIC, 2.0F);
            }
        }
    }
}