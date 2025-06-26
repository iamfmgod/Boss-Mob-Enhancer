package com.example.bossmobenhancer.container;

import com.example.bossmobenhancer.tileentity.TileEntityEnshroudedBeacon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;

public class ContainerEnshroudedBeacon extends Container {
    private final TileEntityEnshroudedBeacon beacon;

    public ContainerEnshroudedBeacon(InventoryPlayer playerInv,
                                     TileEntityEnshroudedBeacon beacon) {
        this.beacon = beacon;
        // Player inventory slots (3 rows x 9 columns)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlotToContainer(new net.minecraft.inventory.Slot(
                        playerInv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }
        // Hotbar slots (1 row x 9 columns)
        for (int col = 0; col < 9; ++col) {
            addSlotToContainer(new net.minecraft.inventory.Slot(
                    playerInv,
                    col,
                    8 + col * 18,
                    142
            ));
        }
        // If you add custom beacon slots, add them here
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        BlockPos pos = beacon.getPos();
        return player.getDistanceSq(pos) <= 64;
    }
}