package com.cyanogen.experienceobelisk.block_entities.bookshelves;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VermiferousCartographersBookshelfEntity extends AbstractVermiferousBookshelfEntity {

    public VermiferousCartographersBookshelfEntity(BlockPos pos, BlockState state) {
        super(pos, state);

        super.orbValue = 0;
        super.durability = 1200;

    }

}
