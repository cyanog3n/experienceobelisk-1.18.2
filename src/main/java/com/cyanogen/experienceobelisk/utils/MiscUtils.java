package com.cyanogen.experienceobelisk.utils;

import net.minecraft.core.BlockPos;

public class MiscUtils {

    public static double straightLineDistance(BlockPos a, BlockPos b){

        double deltaX = Math.abs(a.getX() - b.getX());
        double deltaY = Math.abs(a.getY() - b.getY());
        double deltaZ = Math.abs(a.getZ() - b.getZ());

        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2) + Math.pow(deltaZ,2));
    }

}
