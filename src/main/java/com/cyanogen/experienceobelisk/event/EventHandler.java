package com.cyanogen.experienceobelisk.event;

import com.cyanogen.experienceobelisk.block_entities.bibliophage.AbstractInfectedBookshelfEntity;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onHighlightBlock(RenderHighlightEvent.Block event){

        Entity entity = event.getCamera().getEntity();
        Level level = entity.level();
        BlockPos pos = event.getTarget().getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(entity instanceof Player player){

            if(player.getItemInHand(InteractionHand.MAIN_HAND).is(RegisterItems.ATTUNEMENT_STAFF.get())){

                Component component;

                if(blockEntity instanceof AbstractInfectedBookshelfEntity bookshelf){


                }
            }

        }

    }


}
