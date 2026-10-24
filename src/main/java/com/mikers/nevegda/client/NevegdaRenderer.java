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
        
        // Передаем вариант текстуры (как было)
        renderState.addGeckolibData(NevegdaModel.VARIANT, animatable.getVariant());

        // --- ПЕРЕДАЧА УГЛОВ ГОЛОВЫ ---
        // Передаем pitch (наклон вверх/вниз) и yaw (поворот влево/вправо)
        // Используем стандартные методы LivingEntity
        renderState.addGeckolibData(NevegdaModel.HEAD_PITCH, animatable.getViewXRot(partialTick));
        renderState.addGeckolibData(NevegdaModel.HEAD_YAW, animatable.getViewYRot(partialTick));
    }
}
