package com.cyanogen.experienceobelisk.block_entities.bookworm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VermiferousArchiversBookshelfEntity extends AbstractVermiferousBookshelfEntity {

    public VermiferousArchiversBookshelfEntity(BlockPos pos, BlockState state) {
        super(pos, state);

        super.orbValue = 2;
        super.durability = 1200;

    }

}
