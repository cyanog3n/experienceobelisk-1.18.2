package com.cyanogen.experienceobelisk.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class MiscUtils {

    public static int dyeColorToTextColor(int dyeColor){

        return switch (dyeColor) {
            case 0 -> 15; //white -> white
            case 1 -> 6; //orange -> gold
            case 2 -> 5; //magenta -> dark purple
            case 3 -> 11; //light blue -> aqua
            case 4 -> 14; //yellow -> yellow
            case 5 -> 10; //lime -> green
            case 6 -> 12; //pink -> red
            case 7 -> 7; //gray -> gray
            case 8 -> 9; //light gray -> blue
            case 9 -> 3; //cyan -> dark aqua
            case 10 -> 13; //purple -> light purple
            case 11 -> 1; //blue -> dark blue
            case 12 -> 8; //brown -> dark gray
            case 13 -> 2; //green -> dark green
            case 14 -> 4; //red -> dark red
            case 15 -> 0; //black -> black
            default -> 15;
        };
    }

    public static List<Item> getValidFormattingItems(){
        List<Item> validFormattingItems = new ArrayList<>();
        validFormattingItems.add(Items.END_CRYSTAL);
        validFormattingItems.add(Items.ECHO_SHARD);
        validFormattingItems.add(Items.TRIDENT);
        validFormattingItems.add(Items.NETHER_STAR);
        validFormattingItems.add(Items.TNT);

        return validFormattingItems;
    }

    public static char itemToFormat(int index){

        return switch (index) {
            case 0 -> 'k'; //end crystal -> obfuscated
            case 1 -> 'l'; //echo shard -> bold
            case 2 -> 'm'; //trident -> strikethrough
            case 3 -> 'n'; //nether star -> underline
            case 4 -> 'r'; //tnt -> reset
            default -> 'f';
        };

    }

    public static double straightLineDistance(BlockPos a, BlockPos b){

        double deltaX = Math.abs(a.getX() - b.getX());
        double deltaY = Math.abs(a.getY() - b.getY());
        double deltaZ = Math.abs(a.getZ() - b.getZ());

        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2) + Math.pow(deltaZ,2));
    }
}
