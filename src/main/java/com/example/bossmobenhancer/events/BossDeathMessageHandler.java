package com.example.bossmobenhancer.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BossDeathMessageHandler {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || !entity.hasCustomName()) return;

        // Only show message if killed by a player
        if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            String mobName = entity.getCustomNameTag();
            String playerName = player.getName();
            String msg = playerName + " has slain " + mobName + "!";
            player.sendMessage(new TextComponentString(msg));
        }
    }
}

