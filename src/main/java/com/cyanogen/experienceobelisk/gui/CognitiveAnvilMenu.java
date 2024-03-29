package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.utils.ExperienceUtils;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public class CognitiveAnvilMenu extends AnvilMenu{

    public CognitiveAnvilMenu(int id, Inventory inventory) {
        super(id, inventory);
    }

    public CognitiveAnvilMenu(int id, Inventory inventory, FriendlyByteBuf data){
        super(id, inventory);
    }

    @Override
    public void removed(Player player) {

        ItemStack item1 = this.getSlot(0).getItem();
        ItemStack item2 = this.getSlot(1).getItem();

        if(!item1.isEmpty() || !item2.isEmpty()){
            if(!player.addItem(item1)){
                player.drop(item1, false);
            }
            if(!player.addItem(item2)){
                player.drop(item2, false);
            }
        }

        super.removed(player);
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        int cost = this.getCost();
        int costXP = ExperienceUtils.levelsToXP(cost);

        if(!player.isCreative()){
            player.giveExperienceLevels(cost);
            player.giveExperiencePoints(-costXP);
        }

        super.onTake(player, stack);
    }

}
