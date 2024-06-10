package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VermiferousEnchantedBookshelfEntity extends AbstractVermiferousBookshelfEntity {

    public VermiferousEnchantedBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.VERMIFEROUS_ENCHANTED_BOOKSHELF_BE.get(), pos, state);

        super.spawnDelayMin = 120;
        super.spawnDelayMax = 200;
        super.orbValue = 48;
        super.durability = 600;
    }

}
