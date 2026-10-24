package com.mikers.nevegda.registry;

import com.mikers.nevegda.MikersNevegdaMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MikersNevegdaMod.MOD_ID);

    // Регистрируем яйцо призыва.
    // Цвета (0x1a1a1a - темный, 0x8b0000 - темно-красный) можешь поменять под свою текстуру.
    public static final DeferredItem<Item> NEVEGDA_SPAWN_EGG = ITEMS.register("nevegda_spawn_egg",
            () -> new SpawnEggItem(ModEntities.NEVEGDA.get(), 0x1a1a1a, 0x8b0000, new Item.Properties()));
}
