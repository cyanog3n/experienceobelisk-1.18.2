package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.block.ExperienceAcceleratorBlock;
import com.cyanogen.experienceobelisk.block.LinearExperienceAcceleratorBlock;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;

public class LinearExperienceAcceleratorEntity extends ExperienceReceivingEntity implements GeoBlockEntity {

    public LinearExperienceAcceleratorEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.LINEAREXPERIENCEACCELERATOR_BE.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        double speed = 2.5;

        Direction facing = state.getValue(LinearExperienceAcceleratorBlock.FACING);
        int x = 0;
        int z = 0;

        switch (facing){
            case NORTH -> z = -1;
            case SOUTH -> z = 1;
            case EAST -> x = 1;
            case WEST -> x = -1;
        }

        AABB area = new AABB(
                pos.getX(),
                pos.getY() + 1,
                pos.getZ(),
                pos.getX() + 1,
                pos.getY() + 2,
                pos.getZ() + 1);

        List<ExperienceOrb> list = level.getEntitiesOfClass(ExperienceOrb.class, area);

        if(!list.isEmpty()) for(ExperienceOrb orb : list){
            orb.addDeltaMovement(new Vec3(speed * x,0,speed * z));
        }
    }
}