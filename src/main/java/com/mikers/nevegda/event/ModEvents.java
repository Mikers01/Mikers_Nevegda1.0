package com.mikers.nevegda.event;

import com.mikers.nevegda.MikersNevegdaMod;
import com.mikers.nevegda.entity.NevegdaEntity;
import com.mikers.nevegda.registry.ModEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = MikersNevegdaMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.NEVEGDA.get(), NevegdaEntity.createAttributes().build());
    }
}
