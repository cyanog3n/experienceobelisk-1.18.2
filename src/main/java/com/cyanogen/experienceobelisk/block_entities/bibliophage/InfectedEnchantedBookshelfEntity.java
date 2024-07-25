package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfectedEnchantedBookshelfEntity extends AbstractInfectedBookshelfEntity {

    public InfectedEnchantedBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.INFECTED_ENCHANTED_BOOKSHELF_BE.get(), pos, state);

        super.spawnDelayMin = 80;
        super.spawnDelayMax = 160;
        super.orbValue = 12;
        super.spawns = 200;
    }

}
