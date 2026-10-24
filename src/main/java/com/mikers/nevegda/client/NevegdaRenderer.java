package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.DataTickets; // Встроенный класс

public class NevegdaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NevegdaEntity, R> {

    public NevegdaRenderer(EntityRendererProvider.Context context) {
        super(context, new NevegdaModel());
    }

    @Override
    public Identifier getTextureLocation(R renderState) {
        int variant = 1;
        // Достаем сущность через встроенный DataTickets.ENTITY
        if (renderState.getGeckolibData(DataTickets.ENTITY) instanceof NevegdaEntity nevegda) {
            variant = nevegda.getVariant();
        }
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "textures/entity/MikersNevegda" + variant + ".png");
    }
}
