package com.mikers.nevegda.registry;

import com.mikers.nevegda.MikersNevegdaMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MikersNevegdaMod.MOD_ID);

    // Регистрируем яйцо. Цвета (0x1a1a1a, 0x8b0000) можно поставить любые, 
    // так как текстура у тебя уже своя.
    public static final DeferredItem<Item> NEVEGDA_SPAWN_EGG = ITEMS.register("nevegda_spawn_egg",
            () -> new SpawnEggItem(ModEntities.NEVEGDA.get(), 0x1a1a1a, 0x8b0000, new Item.Properties()));
}
