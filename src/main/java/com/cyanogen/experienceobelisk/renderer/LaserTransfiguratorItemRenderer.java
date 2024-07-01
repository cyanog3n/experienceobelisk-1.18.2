package com.cyanogen.experienceobelisk.renderer;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.item.LaserTransfiguratorItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LaserTransfiguratorItemRenderer extends GeoItemRenderer<LaserTransfiguratorItem> {

    public LaserTransfiguratorItemRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(LaserTransfiguratorItem object) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "geo/laser_transfigurator.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(LaserTransfiguratorItem object) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "textures/custom_models/laser_transfigurator.png");
            }

            @Override
            public ResourceLocation getAnimationResource(LaserTransfiguratorItem animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "animations/laser_transfigurator.json");
            }

            @Override
            public RenderType getRenderType(LaserTransfiguratorItem animatable, ResourceLocation texture) {
                return RenderType.entityTranslucent(getTextureResource(animatable));
            }
        });
    }
}
