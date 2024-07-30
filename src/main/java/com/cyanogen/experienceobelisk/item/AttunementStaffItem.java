package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.block_entities.AbstractAcceleratorEntity;
import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import com.cyanogen.experienceobelisk.block_entities.ExperienceReceivingEntity;
import com.cyanogen.experienceobelisk.block_entities.bibliophage.AbstractInfectedBookshelfEntity;
import com.cyanogen.experienceobelisk.config.Config;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import com.cyanogen.experienceobelisk.utils.MiscUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if(player.isShiftKeyDown() && stack.is(RegisterItems.ATTUNEMENT_STAFF.get()) && stack.getOrCreateTag().contains("type")){
            reset(stack);
            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.unbind_obelisk"), true);

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity entity = level.getBlockEntity(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if(player != null && player.isShiftKeyDown()){
            if(entity instanceof ExperienceObeliskEntity obelisk){
                handleObelisk(obelisk, stack, player);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if(entity instanceof ExperienceReceivingEntity receiver){
                handleExperienceReceivingBlock(receiver, stack, player, level);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if(entity instanceof AbstractAcceleratorEntity accelerator){
                handleAccelerator(accelerator, player);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if(entity instanceof AbstractInfectedBookshelfEntity bookshelf){
                handleBookshelf(bookshelf, player);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);
    }

    public void handleObelisk(ExperienceObeliskEntity obelisk, ItemStack stack, Player player){

        BlockPos thisPos = obelisk.getBlockPos();
        CompoundTag tag = stack.getOrCreateTag();

        tag.putInt("boundX", thisPos.getX());
        tag.putInt("boundY", thisPos.getY());
        tag.putInt("boundZ", thisPos.getZ());
        tag.putString("type", "obelisk");

        player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.bind_obelisk"), true);
    }

    public void handleExperienceReceivingBlock(ExperienceReceivingEntity receiver, ItemStack stack, Player player, Level level){

        final double range = Config.COMMON.bindingRange.get();

        BlockPos thisPos = receiver.getBlockPos();
        CompoundTag tag = stack.getOrCreateTag();

        if(tag.getString("type").equals("obelisk")){
            BlockPos savedPos = new BlockPos(
                    tag.getInt("boundX"),
                    tag.getInt("boundY"),
                    tag.getInt("boundZ"));

            BlockEntity savedEntity = level.getBlockEntity(savedPos);

            if(receiver.isBound && savedPos.equals(receiver.getBoundPos())){
                receiver.setUnbound();
                player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.unbind_target"), true);
            }
            else if(MiscUtils.straightLineDistance(thisPos, savedPos) <= range){

                if(savedEntity instanceof ExperienceObeliskEntity){
                    receiver.setBoundPos(savedPos);
                    receiver.setBound();

                    player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.bind_target",
                            Component.literal(savedPos.toShortString()).withStyle(ChatFormatting.GREEN)), true);
                }
                else{
                    player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.obelisk_doesnt_exist",
                            Component.literal(savedPos.toShortString())).withStyle(ChatFormatting.RED), true);
                }

            }
            else{
                player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.obelisk_too_far"), true);
            }
        }
        else if(receiver.isBound){
            receiver.setUnbound();
            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.unbind_target"), true);
        }
    }

    public void handleAccelerator(AbstractAcceleratorEntity accelerator, Player player){

        accelerator.toggleRedstoneEnabled();

        if(accelerator.redstoneEnabled){
            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.enable_redstone"), true);
        }
        else{
            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.disable_redstone"), true);
        }
    }

    public void handleBookshelf(AbstractInfectedBookshelfEntity bookshelf, Player player){
        boolean status = bookshelf.toggleActivity();
        if(status){
            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.disable_bookshelf"), true);
        }
        else{
            player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.enable_bookshelf"), true);
        }

    }

    public void reset(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        tag.remove("type");
        tag.remove("boundX");
        tag.remove("boundY");
        tag.remove("boundZ");
    }

}
