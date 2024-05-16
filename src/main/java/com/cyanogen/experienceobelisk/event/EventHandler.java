package com.cyanogen.experienceobelisk.event;

import com.cyanogen.experienceobelisk.config.Config;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event){

        if(Config.COMMON.formatting.get()){
            CustomAnvilRecipes.nameFormattingRecipes(event);
        }

        if(Config.COMMON.anvilRepair.get()){
            CustomAnvilRecipes.repairRecipes(event);
        }
    }


}
