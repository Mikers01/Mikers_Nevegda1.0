package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;

public class NevegdaModel extends GeoModel<NevegdaEntity> {

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
        // Получаем ID сущности из RenderState (он всегда есть у сущностей)
        int id = renderState.getEntityId();
        
        // Делим ID на 5 с остатком, чтобы получить число от 0 до 4, и прибавляем 1
        // Это дает стабильный выбор текстуры для каждой конкретной сущности
        int variant = (id % 5) + 1; 
        
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "textures/entity/MikersNevegda" + variant + ".png");
    }
}
