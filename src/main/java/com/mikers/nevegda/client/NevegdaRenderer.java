package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;

public class NevegdaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NevegdaEntity, R> {

    public NevegdaRenderer(EntityRendererProvider.Context context) {
        super(context, new NevegdaModel());
    }

    @Override
    public void addRenderData(NevegdaEntity animatable, Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(NevegdaModel.VARIANT, animatable.getVariant());
    }

    // Отключаем ванильный поворот при смерти
    @Override
    protected void setupRotations(LivingEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        // Если моб умирает, не применяем стандартный наклон
        if (entity.isDeadOrDying()) {
            return;
        }
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
    }
}
