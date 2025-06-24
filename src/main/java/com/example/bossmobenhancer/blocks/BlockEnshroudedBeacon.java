package com.example.bossmobenhancer.blocks;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

/**
 * A placeholder WIP version of the Enshrouded Beacon.
 * This block no longer supports tile entities.
 */
public class BlockEnshroudedBeacon extends Block {

    public BlockEnshroudedBeacon() {
        // Change Material.IRON to Material.ROCK, which is available in 1.12.2.
        super(Material.ROCK);

        // Set the translation key so that the proper name is looked up from your lang file.
        this.setTranslationKey(MainMod.MODID + ".enshrouded_beacon");
        setHardness(3.0F);
        setResistance(10.0F);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return null;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        System.out.println("[BossMobEnhancer] Enshrouded Beacon (WIP) added at " + pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state,
                                    EntityPlayer player, net.minecraft.util.EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.sendMessage(new TextComponentString("[BossMobEnhancer] This block is currently WIP."));
        }
        return true;
    }
}