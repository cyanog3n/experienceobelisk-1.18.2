package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class InfectedCartographersBookshelfEntity extends AbstractInfectedBookshelfEntity {

    public InfectedCartographersBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.INFECTED_CARTOGRAPHERS_BOOKSHELF_BE.get(), pos, state);

        super.spawnDelayMin = 70;
        super.spawnDelayMax = 90;
        super.orbValue = 60;
        super.durability = 60;

    }

}
