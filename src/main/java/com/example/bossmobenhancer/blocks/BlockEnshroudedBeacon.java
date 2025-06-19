package com.example.bossmobenhancer.blocks;

import net.minecraft.block.BlockBeacon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity; // Import the TileEntity class
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockEnshroudedBeacon extends BlockBeacon {

    public BlockEnshroudedBeacon() {
        super();
        setUnlocalizedName("enshrouded_beacon");
        setHardness(3.0F);
        setResistance(10.0F);
    }

    // Disable the tile entity functionality.
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return false;
    }

    // Override with the exact signature and return type as defined in BlockBeacon.
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        // Return null to disable tile entity creation.
        return null;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        System.out.println("[BossMobEnhancer] Enshrouded Beacon (WIP) added at " + pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Instead of opening a GUI, inform the player that this block is WIP.
        if (!world.isRemote) {
            player.sendMessage(new TextComponentString("[BossMobEnhancer] This block is currently WIP."));
        }
        return true;
    }
}