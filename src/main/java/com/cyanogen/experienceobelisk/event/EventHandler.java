package com.cyanogen.experienceobelisk.event;

import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event){

        CustomAnvilRecipes.handleCustomRecipes(event);
        //todo: add config options - enable recipes for all anvils, enable only for cognitium anvils, disable recipes

    }

}
