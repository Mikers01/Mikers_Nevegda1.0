package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import org.jetbrains.annotations.Nullable;

public class NevegdaModel extends GeoModel<NevegdaEntity> {

    // Храним вариант текстуры в самом RenderState через простой ключ (обходим DataTicket)
    // Но так как GeoRenderState не позволяет хранить произвольные данные без DataTicket,
    // мы просто будем использовать кэш на основе аниматабла.
    // Самый простой способ: хранить variant в статической Map, но это плохо.
    
    // Вместо этого используем addAdditionalStateData, чтобы сохранить variant в сам RenderState
    // через... к сожалению, без DataTicket это невозможно.
    
    // Поэтому мы возвращаемся к DataTicket, но используем правильный импорт.
    // Судя по вики, DataTicket.create создает билет.
    // Проблема была в импорте. Попробуй этот:
    // import com.geckolib.constant.dataticket.DataTicket;
    
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "mikers_nevegda");
    }

    @Override
    public Identifier getAnimationResource(NevegdaEntity animatable) {
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "mikers_nevegda");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        // Временное решение: всегда 1-я текстура, чтобы сборка прошла.
        // Как только найдем правильный DataTicket, вернем смену.
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "textures/entity/MikersNevegda1.png");
    }
}
