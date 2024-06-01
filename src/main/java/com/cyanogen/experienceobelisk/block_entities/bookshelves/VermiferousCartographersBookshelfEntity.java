package com.cyanogen.experienceobelisk.block_entities.bookshelves;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class VermiferousCartographersBookshelfEntity extends AbstractVermiferousBookshelfEntity {

    public VermiferousCartographersBookshelfEntity(BlockPos pos, BlockState state) {
        super(pos, state);

        super.orbValue = 0;
        super.durability = 1400;

    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(level.getGameTime() % 20 == 0 && blockEntity instanceof AbstractVermiferousBookshelfEntity bookshelf){

            bookshelf.incrementDecayValue();
            bookshelf.infectAdjacent(level, pos);

        }

    }

}
