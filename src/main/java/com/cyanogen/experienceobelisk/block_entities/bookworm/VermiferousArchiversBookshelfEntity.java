package com.cyanogen.experienceobelisk.block_entities.bookworm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VermiferousArchiversBookshelfEntity extends AbstractVermiferousBookshelfEntity {

    public VermiferousArchiversBookshelfEntity(BlockPos pos, BlockState state) {
        super(pos, state);

        super.spawnDelayMin = 120;
        super.spawnDelayMax = 200;
        super.orbValue = 24;
        super.durability = 1200;
    }

}
