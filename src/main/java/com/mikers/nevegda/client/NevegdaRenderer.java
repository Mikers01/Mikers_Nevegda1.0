package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class NevegdaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NevegdaEntity, R> {

    public NevegdaRenderer(EntityRendererProvider.Context context) {
        super(context, new NevegdaModel());
    }

    // Убираем getTextureLocation, чтобы не было конфликтов с DataTicket
}
