package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import com.cyanogen.experienceobelisk.block_entities.ExperienceReceivingEntity;
import com.cyanogen.experienceobelisk.block_entities.ExperienceRelayEntity;
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

    public static final int range = 48;

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    /*
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity entity = level.getBlockEntity(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        CompoundTag tag = stack.getOrCreateTag();

        if(player != null && player.isShiftKeyDown()){

            if(entity instanceof ExperienceObeliskEntity || entity instanceof ExperienceRelayEntity){

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
                //todo: generify messages
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
     */

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if(player.isShiftKeyDown() && stack.is(RegisterItems.ATTUNEMENT_STAFF.get())){
            reset(stack);
            System.out.println("Saved block successfully forgotten");

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
                handleObelisk(obelisk, stack, player, level);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if(entity instanceof ExperienceRelayEntity relay){
                handleRelay(relay, stack, player, level);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            else if(entity instanceof ExperienceReceivingEntity receiver){
                handleExperienceReceivingBlock(receiver, stack, player, level);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);
    }

    public void handleObelisk(ExperienceObeliskEntity obelisk, ItemStack stack, Player player, Level level){

        BlockPos thisPos = obelisk.getBlockPos();
        CompoundTag tag = stack.getOrCreateTag();

        if(tag.getString("type").equals("relay")){
            BlockPos savedPos = new BlockPos(
                    tag.getInt("boundX"),
                    tag.getInt("boundY"),
                    tag.getInt("boundZ"));

            BlockEntity savedEntity = level.getBlockEntity(savedPos);

            if(thisPos.equals(savedPos)){
                //display error msg: Cannot bind a block to itself!
                System.out.println("Cannot bind a block to itself!");
            }
            else if(MiscUtils.straightLineDistance(thisPos, savedPos) <= range && savedEntity instanceof ExperienceRelayEntity){
                obelisk.setBoundPos(savedPos);
                obelisk.setBound();
                //display success msg: Target bound to relay at X Y Z
                System.out.println("Target bound to relay at " + savedPos);
            }
            else{
                //display error msg: the saved block is too far away or does not exist
                System.out.println("The saved block is too far away or does not exist");
            }
        }
        else{

            tag.putInt("boundX", thisPos.getX());
            tag.putInt("boundY", thisPos.getY());
            tag.putInt("boundZ", thisPos.getZ());
            tag.putString("type", "obelisk");

            //display success msg: Block position saved
            System.out.println("Obelisk position saved");

        }
    }

    public void handleRelay(ExperienceRelayEntity relay, ItemStack stack, Player player, Level level){

        BlockPos thisPos = relay.getBlockPos();
        CompoundTag tag = stack.getOrCreateTag();

        if(tag.getString("type").equals("obelisk") || tag.getString("type").equals("relay")){
            BlockPos savedPos = new BlockPos(
                    tag.getInt("boundX"),
                    tag.getInt("boundY"),
                    tag.getInt("boundZ"));

            BlockEntity savedEntity = level.getBlockEntity(savedPos);

            if(thisPos.equals(savedPos)){
                //display error msg: Cannot bind a block to itself!
                System.out.println("Cannot bind a block to itself!");
            }
            else if(MiscUtils.straightLineDistance(thisPos, savedPos) <= range
                    && (savedEntity instanceof ExperienceObeliskEntity || savedEntity instanceof ExperienceRelayEntity)){
                relay.setBoundPos(savedPos);
                relay.setBound();
                reset(stack);
                //display success msg: Target bound to obelisk/relay at X Y Z
                System.out.println("Target bound to obelisk / relay at " + savedPos);
            }
            else{
                //display error msg: the saved obelisk/relay is too far away or does not exist
                System.out.println("The saved block is too far away or does not exist");
            }
        }
        else{

            tag.putInt("boundX", thisPos.getX());
            tag.putInt("boundY", thisPos.getY());
            tag.putInt("boundZ", thisPos.getZ());
            tag.putString("type", "relay");

            //display success msg: Block position saved
            System.out.println("Relay position saved");
        }
    }

    public void handleExperienceReceivingBlock(ExperienceReceivingEntity receiver, ItemStack stack, Player player, Level level){

        BlockPos thisPos = receiver.getBlockPos();
        CompoundTag tag = stack.getOrCreateTag();

        if(tag.getString("type").equals("obelisk")){
            BlockPos savedPos = new BlockPos(
                    tag.getInt("boundX"),
                    tag.getInt("boundY"),
                    tag.getInt("boundZ"));

            BlockEntity savedEntity = level.getBlockEntity(savedPos);

            if(MiscUtils.straightLineDistance(thisPos, savedPos) <= range && savedEntity instanceof ExperienceObeliskEntity){
                receiver.setBoundPos(savedPos);
                receiver.setBound();
                //display success msg: Target bound to obelisk/relay at X Y Z
                System.out.println("Target bound to obelisk at " + savedPos);
            }
            else{
                //display error msg: saved obelisk/relay is too far away or does not exist
                System.out.println("The saved block is too far away or does not exist");
            }
        }
        else{
            //display error msg: this wand does not have a saved obelisk
            System.out.println("This wand does not have a saved block");
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
