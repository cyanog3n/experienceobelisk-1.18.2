package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.block.ExperienceAcceleratorBlock;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
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

        double speed = 2.5;

        Direction facing = state.getValue(ExperienceAcceleratorBlock.FACING);
        int x = 0;
        int y = 0;
        int z = 0;

        switch (facing){
            case UP -> y = 1;
            case DOWN -> y = -1;
            case NORTH -> z = -1;
            case SOUTH -> z = 1;
            case EAST -> x = 1;
            case WEST -> x = -1;
        }

        AABB area = new AABB(
                pos.getX() + x,
                pos.getY() + y,
                pos.getZ() + z,
                pos.getX() + 1 + x,
                pos.getY() + 1 + y,
                pos.getZ() + 1 + z);

        List<ExperienceOrb> list = level.getEntitiesOfClass(ExperienceOrb.class, area);

        if(!list.isEmpty()) for(ExperienceOrb orb : list){
            orb.addDeltaMovement(new Vec3(speed * x,speed * y,speed * z));
        }
    }
}