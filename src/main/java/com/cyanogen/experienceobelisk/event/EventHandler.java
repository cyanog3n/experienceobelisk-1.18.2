package com.cyanogen.experienceobelisk.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onTooltip(ItemTooltipEvent event){
        HandleTooltip.handleTooltip(event);
    }


}
