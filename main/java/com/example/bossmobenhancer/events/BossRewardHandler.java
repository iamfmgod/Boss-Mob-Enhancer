package com.example.bossmobenhancer.events;

import com.example.bossmobenhancer.MainMod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class BossRewardHandler {

    @SubscribeEvent
    public static void onBossMobDeath(LivingDropsEvent event) {
        // Ensure the entity is an instance of EntityLiving to have a custom name.
        EntityLivingBase baseEntity = event.getEntityLiving();
        if (!(baseEntity instanceof EntityLiving)) {
            return;
        }
        EntityLiving mob = (EntityLiving) baseEntity;
        String customName = mob.getCustomNameTag();

        // Check if the mob is classified as a Lord-class enemy.
        // In our naming scheme, Lord-class bosses start with "§6Lord "
        if (customName != null && customName.startsWith("§6Lord ")) {
            // Create an ItemStack for the Lunar Blessed Apple.
            ItemStack appleStack = new ItemStack(MainMod.lunarBlessedApple);

            // Create a new drop with the apple.
            EntityItem appleDrop = new EntityItem(
                    mob.world,
                    mob.posX,
                    mob.posY,
                    mob.posZ,
                    appleStack
            );

            // Add the new drop to the event's drop list.
            event.getDrops().add(appleDrop);
        }
    }
}