package com.example.bossmobenhancer.ai;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class TerrainManipulator {
    private static final Random rand = new Random();

    public static void tryModifyTerrain(EntityLiving mob, int tier) {
        if (tier < 2) return;

        World world = mob.world;
        BlockPos mobPos = mob.getPosition();

        // Slightly randomized area around the mob
        for (int i = 0; i < 5 + rand.nextInt(5); i++) {
            int dx = rand.nextInt(7) - 3;
            int dz = rand.nextInt(7) - 3;
            BlockPos targetPos = mobPos.add(dx, -1, dz); // 1 block below feet

            IBlockState state = world.getBlockState(targetPos);
            if (state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT) {
                world.setBlockState(targetPos,
                        Blocks.DIRT.getStateFromMeta(BlockDirt.DirtType.COARSE_DIRT.getMetadata()),
                        2);
            } else if (tier >= 3 && world.isAirBlock(targetPos.up())) {
                world.setBlockState(targetPos.up(), Blocks.FIRE.getDefaultState(), 3);
            }
        }
    }
}