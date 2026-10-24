package com.mikers.nevegda.registry;

import com.mikers.nevegda.MikersNevegdaMod;
import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(MikersNevegdaMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<NevegdaEntity>> NEVEGDA =
            ENTITY_TYPES.registerEntityType("nevegda", NevegdaEntity::new,
                    MobCategory.MONSTER, builder -> builder
                            // ХИТБОКС: Ширина/Длина 5.0, Высота 2.5
                            .sized(1.0F, 2.5F)
                            .clientTrackingRange(10));
}
