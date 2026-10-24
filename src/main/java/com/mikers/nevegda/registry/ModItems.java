package com.mikers.nevegda.registry;

import com.mikers.nevegda.MikersNevegdaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MikersNevegdaMod.MOD_ID);

    private static final ResourceKey<Item> SPAWN_EGG_ID = ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(MikersNevegdaMod.MOD_ID, "nevegda_spawn_egg")
    );

    public static final DeferredItem<Item> NEVEGDA_SPAWN_EGG = ITEMS.register("nevegda_spawn_egg",
            () -> new SpawnEggItem(
                    new Item.Properties()
                            .setId(SPAWN_EGG_ID)
                            .spawnEgg(ModEntities.NEVEGDA.get())
            ));
}
