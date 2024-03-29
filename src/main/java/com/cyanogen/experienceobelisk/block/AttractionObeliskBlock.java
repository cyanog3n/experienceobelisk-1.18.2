package com.cyanogen.experienceobelisk.block;

import com.cyanogen.experienceobelisk.block_entities.AttractionObeliskEntity;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AttractionObeliskBlock extends Block implements EntityBlock {

    public AttractionObeliskBlock() {
        super(Properties.of()
                .strength(9f)
                .destroyTime(1.2f)
                .explosionResistance(9f)
                .noOcclusion()
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegisterBlockEntities.ATTRACTIONOBELISK_BE.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == RegisterBlockEntities.ATTRACTIONOBELISK_BE.get() ? AttractionObeliskEntity::tick : null;
    }
}
