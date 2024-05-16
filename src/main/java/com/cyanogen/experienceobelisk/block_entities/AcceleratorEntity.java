package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.block.AcceleratorBlock;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AcceleratorEntity extends BlockEntity{

    public AcceleratorEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.ACCELERATOR_BE.get(), pos, state);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        double orbSpeed = 1.6;
        double entitySpeed = 0.7;

        Direction facing = state.getValue(AcceleratorBlock.FACING);
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

        List<Entity> list = level.getEntities(null, area);

        if(!list.isEmpty()) for(Entity entity : list){
            boolean isShiftPlayer = entity instanceof Player player && player.isShiftKeyDown();

            if(entity instanceof ExperienceOrb orb){
                orb.addDeltaMovement(new Vec3(orbSpeed * x,orbSpeed * y,orbSpeed * z));
            }
            else if(!isShiftPlayer){
                entity.addDeltaMovement(new Vec3(entitySpeed * x,entitySpeed * y,entitySpeed * z));
            }

        }
    }
}
