package com.cyanogen.experienceobelisk.renderer;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.item.MolecularMetamorpherItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class MolecularMetamorpherItemRenderer extends GeoItemRenderer<MolecularMetamorpherItem> {

    public MolecularMetamorpherItemRenderer() {
        super(new AnimatedGeoModel<>() {
            @Override
            public ResourceLocation getModelResource(MolecularMetamorpherItem object) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "geo/molecular_metamorpher.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(MolecularMetamorpherItem object) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "textures/custom_models/molecular_metamorpher.png");
            }

            @Override
            public ResourceLocation getAnimationResource(MolecularMetamorpherItem animatable) {
                return new ResourceLocation(ExperienceObelisk.MOD_ID, "animations/molecular_metamorpher.json");
            }
        });
    }

    @Override
    public RenderType getRenderType(MolecularMetamorpherItem animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
