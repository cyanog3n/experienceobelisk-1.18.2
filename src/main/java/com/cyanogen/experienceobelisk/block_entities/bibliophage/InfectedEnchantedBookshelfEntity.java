package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.config.Config;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InfectedEnchantedBookshelfEntity extends AbstractInfectedBookshelfEntity {

    public InfectedEnchantedBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.INFECTED_ENCHANTED_BOOKSHELF_BE.get(), pos, state);

        super.spawnDelayMin = 100;
        super.spawnDelayMax = 300;
        super.orbValue = 12;
        super.spawns = 200;
    }

    @Override
    public void onLoad() {
        super.spawnDelayMin = Config.COMMON.enchantedSpawnDelayMin.get();
        super.spawnDelayMax = Config.COMMON.enchantedSpawnDelayMax.get();
        super.orbValue = Config.COMMON.enchantedOrbValue.get();
        super.spawns = Config.COMMON.enchantedSpawns.get();
    }

}
