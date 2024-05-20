package com.cyanogen.experienceobelisk.block;

import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class WhisperglassBlock extends AbstractGlassBlock {

    public WhisperglassBlock() {
        super(Properties.of(Material.GLASS)
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
