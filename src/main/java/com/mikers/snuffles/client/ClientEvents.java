package com.mikers.snuffles.client;

import com.mikers.snuffles.MikersSnufflesMod;
import com.mikers.snuffles.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

// Убрали bus = EventBusSubscriber.Bus.MOD
@EventBusSubscriber(modid = MikersSnufflesMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SNUFFLES.get(), SnufflesRenderer::new);
    }
}
