package com.example.bossmobenhancer.client;

import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.Container;
import com.example.bossmobenhancer.client.gui.GuiEnshroudedBeacon;
import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;

public class GuiHandler implements IGuiHandler {

    public static final int ENSHROUDED_BEACON_GUI_ID = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);

        if (ID == ENSHROUDED_BEACON_GUI_ID) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityEnshroudedBeacon) {
                // ✅ Return dummy container so server-side sync is happy
                return new Container() {
                    @Override public boolean canInteractWith(EntityPlayer playerIn) {
                        return true;
                    }
                };
            }
        }
        return null; // ❌ Return null if tile type mismatches to avoid crash
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);

        if (ID == ENSHROUDED_BEACON_GUI_ID) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityEnshroudedBeacon) {
                return new GuiEnshroudedBeacon(player.inventory, (TileEntityEnshroudedBeacon) tile);
            }
        }
        return null;
    }
}