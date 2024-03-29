package com.cyanogen.experienceobelisk.event;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event){

        CustomAnvilRecipes.nameFormattingRecipes(event);
        CustomAnvilRecipes.repairRecipes(event);
        //todo: add config options

    }

}
