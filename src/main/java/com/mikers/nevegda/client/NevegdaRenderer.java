package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class NevegdaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NevegdaEntity, R> {

    public NevegdaRenderer(EntityRendererProvider.Context context) {
        super(context, new NevegdaModel());
    }

    @Override
    public void addRenderData(NevegdaEntity animatable, Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(NevegdaModel.VARIANT, animatable.getVariant());
    }
}
