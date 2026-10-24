package com.mikers.nevegda.registry;

import com.mikers.nevegda.MikersNevegdaMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MikersNevegdaMod.MOD_ID);

    public static final DeferredItem<Item> NEVEGDA_SPAWN_EGG = ITEMS.register("nevegda_spawn_egg",
            () -> new SpawnEggItem(ModEntities.NEVEGDA.get(), new Item.Properties()));
}
