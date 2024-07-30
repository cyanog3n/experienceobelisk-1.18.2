package com.cyanogen.experienceobelisk.utils;

import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getLinesFromString(String input, int lineWidth, Font font){

        List<FormattedText> lines = font.getSplitter().splitLines(input, lineWidth, Style.EMPTY);
        List<String> outputLines = new ArrayList<>();

        for(FormattedText line : lines){
            outputLines.add(line.getString());
        }

        return outputLines;
    }

}
