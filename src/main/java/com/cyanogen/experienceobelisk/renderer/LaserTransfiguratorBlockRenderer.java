package com.cyanogen.experienceobelisk.renderer;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.block_entities.LaserTransfiguratorEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LaserTransfiguratorBlockRenderer extends GeoBlockRenderer<LaserTransfiguratorEntity> {

    public LaserTransfiguratorBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new GeoModel<>() {

            @Override
            public ResourceLocation getModelResource(LaserTransfiguratorEntity animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "geo/laser_transfigurator.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(LaserTransfiguratorEntity entity) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "textures/custom_models/laser_transfigurator.png");
            }

            @Override
            public ResourceLocation getAnimationResource(LaserTransfiguratorEntity animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "animations/laser_transfigurator.json");
            }

            @Override
            public RenderType getRenderType(LaserTransfiguratorEntity animatable, ResourceLocation texture) {
                return RenderType.entityTranslucent(getTextureResource(animatable));
            }
        });
    }



}

