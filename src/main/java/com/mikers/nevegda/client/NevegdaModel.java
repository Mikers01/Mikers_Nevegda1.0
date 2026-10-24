package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.dataticket.DataTicket; // Импорт для DataTicket

public class NevegdaModel extends GeoModel<NevegdaEntity> {

    // Создаем ключ для передачи номера текстуры
    public static final DataTicket<Integer> VARIANT = DataTicket.create("variant", Integer.class);

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
        // Достаем номер варианта из RenderState (если нет, ставим 1)
        int variant = renderState.getOrDefaultGeckolibData(VARIANT, 1);
        
        // Формируем путь: mikers_nevegda1.png, mikers_nevegda2.png и т.д.
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "textures/entity/mikers_nevegda" + variant + ".png");
    }

}
