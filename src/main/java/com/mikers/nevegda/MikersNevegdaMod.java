package com.mikers.nevegda;

import com.mikers.nevegda.registry.ModEntities;
import com.mikers.nevegda.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(MikersNevegdaMod.MOD_ID)
public class MikersNevegdaMod {
    public static final String MOD_ID = "mikers_nevegda";

    public MikersNevegdaMod(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        // Добавляем яйцо призыва в ванильную вкладку "Яйца призыва"
        modEventBus.addListener(BuildCreativeModeTabContentsEvent.class, event -> {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(ModItems.NEVEGDA_SPAWN_EGG);
            }
        });
    }
}
