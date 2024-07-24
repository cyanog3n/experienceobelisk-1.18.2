package com.cyanogen.experienceobelisk.renderer;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.block_entities.MolecularMetamorpherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class MolecularMetamorpherBlockRenderer extends GeoBlockRenderer<MolecularMetamorpherEntity> {

    public MolecularMetamorpherBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new AnimatedGeoModel<>() {

            @Override
            public ResourceLocation getModelResource(MolecularMetamorpherEntity animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "geo/molecular_metamorpher.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(MolecularMetamorpherEntity entity) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "textures/custom_models/molecular_metamorpher.png");
            }

            @Override
            public ResourceLocation getAnimationResource(MolecularMetamorpherEntity animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "animations/molecular_metamorpher.json");
            }
        });
    }

    @Override
    public RenderType getRenderType(MolecularMetamorpherEntity animatable, float partialTick, PoseStack poseStack,
                                    @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

}

