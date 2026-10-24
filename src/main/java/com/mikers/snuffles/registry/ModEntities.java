package com.mikers.snuffles.registry;

import com.mikers.snuffles.MikersSnufflesMod;
import com.mikers.snuffles.entity.SnufflesEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(MikersSnufflesMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<SnufflesEntity>> SNUFFLES =
            ENTITY_TYPES.registerEntityType("snuffles", SnufflesEntity::new,
                    MobCategory.MONSTER, builder -> builder.sized(0.8F, 1.8F).clientTrackingRange(10));
}
