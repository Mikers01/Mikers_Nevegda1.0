package com.mikers.snuffles.client;

import com.mikers.snuffles.entity.SnufflesEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;

public class SnufflesRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<SnufflesEntity, R> {

    public SnufflesRenderer(EntityRendererProvider.Context context) {
        super(context, new SnufflesModel());
    }

    @Override
    public void addRenderData(SnufflesEntity animatable, Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        // Сохраняем вариант текстуры в RenderState
        renderState.addGeckolibData(SnufflesModel.VARIANT, animatable.getVariant());
    }
}
