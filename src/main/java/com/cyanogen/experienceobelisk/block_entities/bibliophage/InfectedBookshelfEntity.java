package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.config.Config;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfectedBookshelfEntity extends AbstractInfectedBookshelfEntity {

    public InfectedBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.INFECTED_BOOKSHELF_BE.get(), pos, state);

        super.spawnDelayMin = 150;
        super.spawnDelayMax = 250;
        super.orbValue = 6;
        super.spawns = 100;
    }

    @Override
    public void onLoad() {
        super.spawnDelayMin = Config.COMMON.infectedSpawnDelayMin.get();
        super.spawnDelayMax = Config.COMMON.infectedSpawnDelayMax.get();
        super.orbValue = Config.COMMON.infectedOrbValue.get();
        super.spawns = Config.COMMON.infectedSpawns.get();
    }

}
