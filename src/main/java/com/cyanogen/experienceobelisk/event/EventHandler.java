package com.cyanogen.experienceobelisk.event;

import com.cyanogen.experienceobelisk.utils.MiscUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class EventHandler {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event){

        ItemStack item = event.getItemStack();
        String modId = item.getItem().getCreatorModId(item);
        String translationKey = item.getDescriptionId() + ".description";
        Component description = Component.translatable(translationKey);

        boolean hasDescription = !description.getString().contains(".description"); //check if the key exists
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344); //check if LSHIFT or RSHIFT are held

        if(modId != null && modId.equals("experienceobelisk") && hasDescription){

            if(isShiftDown){

                int index = 1;

                for(String line : MiscUtils.getLinesFromString(description.getString(), 180, Minecraft.getInstance().font)){
                    Component descriptionLine = Component.literal(line).withStyle(ChatFormatting.GRAY);
                    event.getToolTip().add(index, descriptionLine);
                    index++;
                }
            }
            else{
                event.getToolTip().add(1, Component.translatable("tooltip.experienceobelisk.shift_for_info"));
            }
        }

    }


}
