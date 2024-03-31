package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ExperienceRelayEntity extends ExperienceReceivingEntity{

    public ExperienceRelayEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.EXPERIENCERELAY_BE.get(), pos, state);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {




    }
}
