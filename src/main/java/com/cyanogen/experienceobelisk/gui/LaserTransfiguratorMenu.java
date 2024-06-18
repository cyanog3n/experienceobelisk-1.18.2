package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.registries.RegisterMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LaserTransfiguratorMenu extends AbstractContainerMenu {

    SimpleContainer container = new SimpleContainer(4);

    public LaserTransfiguratorMenu(int id, Inventory inventory, FriendlyByteBuf data){
        this(id, inventory, inventory.player, new BlockPos(0,0,0));
    }

    //-----SLOTS-----//

    public LaserTransfiguratorMenu(int id, Inventory inventory, Player player, BlockPos pos){

        super(RegisterMenus.LASER_TRANSFIGURATOR_MENU.get(), id);

        // INPUT 1
        this.addSlot(new Slot(this.container, 0, 17, 18));
        // INPUT 2
        this.addSlot(new Slot(this.container, 0, 17, 38));
        // INPUT 3
        this.addSlot(new Slot(this.container, 0, 17, 58));
        // OUTPUT 1
        this.addSlot(new Slot(this.container, 0, 37, 38){
            @Override
            public boolean mayPlace(ItemStack p_40231_) {
                return false;
            }
        });

    }


    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return false;
    }
}
