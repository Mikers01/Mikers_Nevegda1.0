package com.mikers.nevegda.client;

import com.mikers.nevegda.MikersNevegdaMod;
import com.mikers.nevegda.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MikersNevegdaMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Вот эта строчка обязательна — она связывает сущность и её рендерер
        event.registerEntityRenderer(ModEntities.NEVEGDA.get(), NevegdaRenderer::new);
    }
}
