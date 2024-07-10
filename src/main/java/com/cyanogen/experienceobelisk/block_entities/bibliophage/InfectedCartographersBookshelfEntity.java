package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfectedCartographersBookshelfEntity extends AbstractInfectedBookshelfEntity {

    public InfectedCartographersBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.INFECTED_CARTOGRAPHERS_BOOKSHELF_BE.get(), pos, state);

        super.spawnDelayMin = 20;
        super.spawnDelayMax = 40;
        super.orbValue = 10;
        super.spawns = 10;

        //produces very little XP and decays very quickly. Meant as a quick source of Forgotten Dust

    }

}
