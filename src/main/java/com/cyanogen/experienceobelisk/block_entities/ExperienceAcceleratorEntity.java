package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;

public class ExperienceAcceleratorEntity extends ExperienceReceivingEntity implements GeoBlockEntity {

    public ExperienceAcceleratorEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.EXPERIENCEACCELERATOR_BE.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        AABB area = new AABB(
                pos.getX() + 1,
                pos.getY(),
                pos.getZ(),
                pos.getX() + 2,
                pos.getY() + 1,
                pos.getZ() + 1);

        List<ExperienceOrb> list = level.getEntitiesOfClass(ExperienceOrb.class, area);

        if(!list.isEmpty()) for(ExperienceOrb orb : list){
            orb.addDeltaMovement(new Vec3(20,0,0));
        }
    }
}
