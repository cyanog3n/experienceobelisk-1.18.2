package com.cyanogen.experienceobelisk.block.bookworm;

import com.cyanogen.experienceobelisk.block_entities.bookworm.VermiferousArchiversBookshelfEntity;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VermiferousArchiversBookshelfBlock extends Block implements EntityBlock {

    public VermiferousArchiversBookshelfBlock() {
        super(Properties.of());
    }

    //-----BLOCK ENTITY-----//

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == RegisterBlockEntities.VERMIFEROUS_ARCHIVERS_BOOKSHELF_BE.get() ? VermiferousArchiversBookshelfEntity::tick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegisterBlockEntities.VERMIFEROUS_ARCHIVERS_BOOKSHELF_BE.get().create(pos, state);
    }

}
