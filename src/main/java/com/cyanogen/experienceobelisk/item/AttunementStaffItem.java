package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import com.cyanogen.experienceobelisk.block_entities.ExperienceReceivingEntity;
import com.cyanogen.experienceobelisk.utils.MiscUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AttunementStaffItem extends Item {

    public AttunementStaffItem(Properties p) {
        super(p);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity entity = level.getBlockEntity(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        CompoundTag tag = stack.getOrCreateTag();

        if(player != null && player.isShiftKeyDown()){

            if(entity instanceof ExperienceObeliskEntity){

                if(tag.contains("boundX") && tag.getInt("boundX") == pos.getX()){
                    tag.remove("boundX");
                    tag.remove("boundY");
                    tag.remove("boundZ");

                    player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.unbind_obelisk"), true);
                }
                else{
                    tag.putInt("boundX", pos.getX());
                    tag.putInt("boundY", pos.getY());
                    tag.putInt("boundZ", pos.getZ());

                    player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.bind_obelisk"), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if(entity instanceof ExperienceReceivingEntity receivingEntity){

                if(receivingEntity.isBound){
                    receivingEntity.setUnbound();
                    player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.unbind_target"), true);
                }
                else if(tag.contains("boundX")){     //check if wand has an obelisk stored

                    BlockPos obeliskPos = new BlockPos(tag.getInt("boundX"), tag.getInt("boundY"), tag.getInt("boundZ"));

                    if(level.getBlockEntity(obeliskPos) instanceof ExperienceObeliskEntity){       //check if obelisk at location still exists

                        if(MiscUtils.straightLineDistance(pos, obeliskPos) <= 48){     //check if obelisk is within the effective radius
                            receivingEntity.setBoundPos(obeliskPos);
                            receivingEntity.setBound();

                            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.bind_target",
                                    Component.literal(obeliskPos.toShortString()).withStyle(ChatFormatting.GREEN)), true);
                        }
                        else{
                            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.obelisk_too_far"), true);
                        }
                    }
                    else{
                        player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.obelisk_doesnt_exist",
                                Component.literal(obeliskPos.toShortString())).withStyle(ChatFormatting.RED), true);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);

    }

}
