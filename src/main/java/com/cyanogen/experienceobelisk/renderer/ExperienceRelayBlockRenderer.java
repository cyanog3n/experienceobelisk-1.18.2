package com.cyanogen.experienceobelisk.renderer;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.block_entities.ExperienceFountainEntity;
import com.cyanogen.experienceobelisk.block_entities.ExperienceRelayEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ExperienceRelayBlockRenderer extends GeoBlockRenderer<ExperienceRelayEntity> {

    public ExperienceRelayBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new GeoModel<>() {

            @Override
            public ResourceLocation getModelResource(ExperienceRelayEntity animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "geo/experience_relay_sketch.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(ExperienceRelayEntity entity) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "textures/custom_models/experience_relay_sketch.png");
            }

            @Override
            public ResourceLocation getAnimationResource(ExperienceRelayEntity animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "animations/experience_relay_sketch.json");
            }

            @Override
            public RenderType getRenderType(ExperienceRelayEntity animatable, ResourceLocation texture) {
                return RenderType.entityTranslucent(getTextureResource(animatable));
            }
        });
    }

}

