package com.cyanogen.experienceobelisk.block;

import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class WhisperglassBlock extends AbstractGlassBlock {

    public WhisperglassBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                .sound(SoundType.GLASS)
                .strength(2.8f)
                .destroyTime(1.2f)
                .requiresCorrectToolForDrops()
                .friction(1.0f)
                .noOcclusion()
                .isViewBlocking((state,getter,pos)->false)
        );
    }
}
