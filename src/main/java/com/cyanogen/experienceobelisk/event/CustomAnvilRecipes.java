package com.cyanogen.experienceobelisk.event;

import com.cyanogen.experienceobelisk.utils.MiscUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.AnvilUpdateEvent;

import java.util.ArrayList;
import java.util.List;

public class CustomAnvilRecipes {

    public static void nameFormattingRecipes(AnvilUpdateEvent event){
        ItemStack right = event.getRight();
        ItemStack left = event.getLeft();
        MutableComponent name;

        List<Item> validFormattingItems = new ArrayList<>();
        validFormattingItems.add(Items.END_CRYSTAL);
        validFormattingItems.add(Items.ECHO_SHARD);
        validFormattingItems.add(Items.CRYING_OBSIDIAN);
        validFormattingItems.add(Items.NAUTILUS_SHELL);
        validFormattingItems.add(Items.TNT);

        if(event.getName() != null && event.getName().length() >= 1){ //if renaming field isn't empty
            name = Component.literal(event.getName()).withStyle(left.getHoverName().getStyle());
        }
        else{
            name = left.getHoverName().copy();
        }

        if(right.getItem() instanceof DyeItem || validFormattingItems.contains(right.getItem())){

            MutableComponent newName = name;

            if(right.getItem() instanceof DyeItem dye){
                int dyeColor = dye.getDyeColor().getId();
                ChatFormatting format = ChatFormatting.getById(MiscUtils.dyeColorToTextColor(dyeColor));

                if (format != null) {
                    newName = name.withStyle(format);
                }
            }
            else if(validFormattingItems.contains(right.getItem())){
                int index = validFormattingItems.indexOf(right.getItem());
                char code = MiscUtils.itemToFormat(index);
                ChatFormatting format = ChatFormatting.getByCode(code);

                if (format != null) {
                    newName = newName.withStyle(format);
                }
            }

            ItemStack output = left.copy();
            output.setHoverName(newName);
            event.setOutput(output);
            event.setCost(1);
            event.setMaterialCost(1);
        }
    }

}
