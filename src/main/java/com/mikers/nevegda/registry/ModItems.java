package com.mikers.nevegda.registry;

import com.mikers.nevegda.MikersNevegdaMod;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MikersNevegdaMod.MOD_ID);

    public static final DeferredItem<Item> NEVEGDA_SPAWN_EGG = ITEMS.register("nevegda_spawn_egg",
            () -> {
                // Создаем NBT-тег, который говорит игре: "Это яйцо призывает Невежду"
                CompoundTag entityTag = new CompoundTag();
                entityTag.putString("id", "mikers_nevegda:nevegda");
                
                // Создаем Properties и добавляем в них данные о сущности
                Item.Properties properties = new Item.Properties()
                        .component(DataComponents.ENTITY_DATA, CustomData.of(entityTag));
                
                return new SpawnEggItem(properties);
            });
}
