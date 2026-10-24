package com.mikers.nevegda.client;

import com.mikers.nevegda.entity.NevegdaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class NevegdaRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<NevegdaEntity, R> {

    public static final DataTicket<Integer> VARIANT = DataTicket.create("variant", Integer.class);

    public NevegdaRenderer(EntityRendererProvider.Context context) {
        super(context, new NevegdaModel());
    }

    @Override
    public void addRenderData(NevegdaEntity animatable, Void relatedObject, R renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(VARIANT, animatable.getVariant());
    }

    @Override
    public Identifier getTextureLocation(R renderState) {
        int variant = renderState.getOrDefaultGeckolibData(VARIANT, 1);
        return Identifier.fromNamespaceAndPath("mikers_nevegda", "textures/entity/MikersNevegda" + variant + ".png");
    }
}
