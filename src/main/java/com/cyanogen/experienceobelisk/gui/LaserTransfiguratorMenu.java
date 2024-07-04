package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.LaserTransfiguratorEntity;
import com.cyanogen.experienceobelisk.registries.RegisterMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LaserTransfiguratorMenu extends AbstractContainerMenu {

    SimpleContainer container = new SimpleContainer(5);
    LaserTransfiguratorEntity transfigurator;

    public LaserTransfiguratorMenu(int id, Inventory inventory, FriendlyByteBuf data){
        this(id, inventory, null, inventory.player, new BlockPos(0,0,0));

        Level level = inventory.player.level();
        BlockPos pos = data.readBlockPos();
        this.transfigurator = (LaserTransfiguratorEntity) level.getBlockEntity(pos);
    }

    //-----SLOTS-----//

    public LaserTransfiguratorMenu(int id, Inventory inventoryPlayer, IItemHandler inventoryBlock, Player player, BlockPos pos){

        super(RegisterMenus.LASER_TRANSFIGURATOR_MENU.get(), id);

        if(inventoryBlock != null){
            // INPUT 1
            this.addSlot(new SlotItemHandler(inventoryBlock, 0, 19, 35));
            // INPUT 2
            this.addSlot(new SlotItemHandler(inventoryBlock, 1, 50, 52));
            // INPUT 3
            this.addSlot(new SlotItemHandler(inventoryBlock, 2, 70, 18));
            // OUTPUT 1
            this.addSlot(new SlotItemHandler(inventoryBlock, 3, 140, 35){
                @Override
                public boolean mayPlace(ItemStack p_40231_) {
                    return false;
                }
            });
        }
        else{
            // INPUT 1
            this.addSlot(new Slot(this.container, 0, 19, 35));
            // INPUT 2
            this.addSlot(new Slot(this.container, 1, 50, 52));
            // INPUT 3
            this.addSlot(new Slot(this.container, 2, 70, 18));
            // OUTPUT 1
            this.addSlot(new Slot(this.container, 3, 140, 35){
                @Override
                public boolean mayPlace(ItemStack p_40231_) {
                    return false;
                }
            });
        }

        addPlayerInventory(inventoryPlayer);
        addPlayerHotbar(inventoryPlayer);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot.hasItem()) {

            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if(index <= 3){ //moving from menu to player inventory
                if(this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)){
                    transfigurator.setChangedWhileProcessing(true);
                }
                else{
                    return ItemStack.EMPTY;
                }
            } //moving from player inventory to menu
            else{
                if(this.moveItemStackTo(itemstack1, 0, 3, false)){
                    transfigurator.setChangedWhileProcessing(true);
                }
                else{
                    return ItemStack.EMPTY;
                }
            }

            if(itemstack1.isEmpty()){
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
