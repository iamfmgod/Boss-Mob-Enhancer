package com.example.bossmobenhancer.registry;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.blocks.BlockEnshroudedBeacon;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = MainMod.MODID)
public class BlockRegistry {

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> ev) {
        BlockEnshroudedBeacon beacon = new BlockEnshroudedBeacon();
        beacon.setRegistryName(MainMod.MODID, "enshrouded_beacon");
        ev.getRegistry().register(beacon);
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<net.minecraft.item.Item> ev) {
        // register corresponding ItemBlock
        ev.getRegistry().register(
                new ItemBlock(
                        net.minecraft.block.Block.REGISTRY.getObject(
                                new net.minecraft.util.ResourceLocation(MainMod.MODID, "enshrouded_beacon")
                        )
                ).setRegistryName(MainMod.MODID, "enshrouded_beacon")
        );
    }

    // If you have TESR or special block models, hook them here:
    @SubscribeEvent
    public static void onModelBake(ModelRegistryEvent ev) {
        // client‐only: registerTileEntitySpecialRenderer, etc.
    }
}