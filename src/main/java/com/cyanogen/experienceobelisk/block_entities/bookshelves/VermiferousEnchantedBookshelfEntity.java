package com.cyanogen.experienceobelisk.block_entities.bookshelves;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VermiferousEnchantedBookshelfEntity extends AbstractVermiferousBookshelfEntity {

    public VermiferousEnchantedBookshelfEntity(BlockPos pos, BlockState state) {
        super(pos, state);

        super.orbValue = 5;
        super.durability = 800;

    }

}
