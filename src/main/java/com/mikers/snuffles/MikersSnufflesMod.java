package com.mikers.snuffles;

import com.mikers.snuffles.entity.SnufflesEntity;
import com.mikers.snuffles.registry.ModEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;

@Mod(MikersSnufflesMod.MOD_ID)
public class MikersSnufflesMod {
    public static final String MOD_ID = "mikers_snuffles";

    public MikersSnufflesMod(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        // Регистрируем атрибуты
        modEventBus.addListener(this::registerAttributes);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // ...
    }

    // Этот метод сообщает игре, какие атрибуты у нашего моба
    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SNUFFLES.get(), SnufflesEntity.createAttributes().build());
    }
}
