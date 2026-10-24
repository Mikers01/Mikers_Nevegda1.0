package com.mikers.nevegda;

import com.mikers.nevegda.registry.ModEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MikersNevegdaMod.MOD_ID)
public class MikersNevegdaMod {
    public static final String MOD_ID = "mikers_nevegda";

    public MikersNevegdaMod(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
    }
}
