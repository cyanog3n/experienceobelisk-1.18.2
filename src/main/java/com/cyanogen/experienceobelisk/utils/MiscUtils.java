package com.cyanogen.experienceobelisk.utils;

import net.minecraft.core.BlockPos;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public class MiscUtils {

    public static double straightLineDistance(BlockPos a, BlockPos b){

        double deltaX = Math.abs(a.getX() - b.getX());
        double deltaY = Math.abs(a.getY() - b.getY());
        double deltaZ = Math.abs(a.getZ() - b.getZ());

        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2) + Math.pow(deltaZ,2));
    }

    public static boolean isSameAnimation(Animation animation, AnimationBuilder builder){
        //only to be used for builders containing a single animation
        return builder.getRawAnimationList().get(0).animationName.equals(animation.animationName);
    }

}
