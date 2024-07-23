package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfectedArchiversBookshelfEntity extends AbstractInfectedBookshelfEntity {

    public InfectedArchiversBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.INFECTED_ARCHIVERS_BOOKSHELF_BE.get(), pos, state);

        super.spawnDelayMin = 120;
        super.spawnDelayMax = 200;
        super.orbValue = 12;
        super.spawns = 400;
    }

}
