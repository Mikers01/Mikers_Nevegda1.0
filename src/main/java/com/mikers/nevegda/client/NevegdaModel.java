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
        // Возвращаем путь с нижним подчеркиванием и строчными буквами
        // Чтобы это сработало, файлы в папке textures/entity должны называться так же!
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "textures/entity/mikers_nevegda1.png");
    }
}
