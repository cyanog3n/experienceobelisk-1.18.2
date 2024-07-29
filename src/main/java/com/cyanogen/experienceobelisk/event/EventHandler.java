package com.cyanogen.experienceobelisk.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onTooltip(ItemTooltipEvent event){

        ItemStack item = event.getItemStack();
        String modId = item.getItem().getCreatorModId(item);
        String translationKey = item.getDescriptionId() + ".description";
        Component description = Component.translatable(translationKey);

        boolean hasDescription = !description.getString().contains(".description"); //check if the key exists
        boolean isShiftDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340);

        if(modId != null && modId.equals("experienceobelisk") && hasDescription){
            if(isShiftDown){
                event.getToolTip().add(1, description);
            }
            else{
                event.getToolTip().add(1, Component.translatable("tooltip.experienceobelisk.shift_for_info"));
            }
        }

    }


}
