package com.example.bossmobenhancer;

import com.example.bossmobenhancer.client.gui.GuiEnshroudedBeacon;
import com.example.bossmobenhancer.container.ContainerEnshroudedBeacon;
import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Shared GUI handler safe on both sides.
 */
public class GuiHandler implements IGuiHandler {
    public static final int ENSHROUDED_BEACON_GUI_ID = 1;

    @Override
    public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id != ENSHROUDED_BEACON_GUI_ID) return null;
        TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
        if (!(te instanceof TileEntityEnshroudedBeacon)) return null;
        return new ContainerEnshroudedBeacon(player.inventory, (TileEntityEnshroudedBeacon)te);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id != ENSHROUDED_BEACON_GUI_ID) return null;
        TileEntity te = world.getTileEntity(new BlockPos(x,y,z));
        if (!(te instanceof TileEntityEnshroudedBeacon)) return null;
        return new GuiEnshroudedBeacon(player.inventory, (TileEntityEnshroudedBeacon)te);
    }
}