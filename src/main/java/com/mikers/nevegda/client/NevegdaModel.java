package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.dataticket.DataTicket;

public class NevegdaModel extends GeoModel<NevegdaEntity> {

    public static final DataTicket<Integer> VARIANT = DataTicket.create("variant", Integer.class);
    
    // Новые ключи для головы
    public static final DataTicket<Float> HEAD_PITCH = DataTicket.create("head_pitch", Float.class);
    public static final DataTicket<Float> HEAD_YAW = DataTicket.create("head_yaw", Float.class);

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
        int variant = renderState.getOrDefaultGeckolibData(VARIANT, 1);
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "textures/entity/mikers_nevegda" + variant + ".png");
    }
}
