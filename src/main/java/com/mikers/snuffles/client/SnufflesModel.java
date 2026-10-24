package com.mikers.snuffles.client;

import com.mikers.snuffles.entity.SnufflesEntity;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.constant.dataticket.DataTicket;

public class SnufflesModel extends GeoModel<SnufflesEntity> {
    
    // Создаем свой билет для передачи варианта
    public static final DataTicket<Integer> VARIANT = DataTicket.create("variant", Integer.class);

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath("mikers_snuffles", "mikers_snuffles");
    }

    @Override
    public Identifier getAnimationResource(SnufflesEntity animatable) {
        return Identifier.fromNamespaceAndPath("mikers_snuffles", "mikers_snuffles");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        int variant = renderState.getOrDefaultGeckolibData(VARIANT, 1);
        return Identifier.fromNamespaceAndPath("mikers_snuffles", "textures/entity/mikers_snuffles" + variant + ".png");
    }
}
