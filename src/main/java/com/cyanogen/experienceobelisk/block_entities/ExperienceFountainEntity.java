package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class ExperienceFountainEntity extends ExperienceReceivingEntity implements IAnimatable {

    public ExperienceFountainEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.EXPERIENCE_FOUNTAIN_BE.get(), pos, state);
    }

    //-----------ANIMATIONS-----------//

    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.transitionLengthTicks = 0;

        BlockEntity entity = event.getAnimatable();

        //conditions for the green glow showing up:
        // - must have a linked obelisk
        // - must have player standing on it OR redstone signal

        if(level != null && entity instanceof ExperienceFountainEntity fountain){

            boolean hasNeighborSignal = level.hasNeighborSignal(fountain.getBlockPos());
            boolean isActive = fountain.isBound && (hasNeighborSignal || fountain.hasPlayerAbove);

            if(controller.getCurrentAnimation() == null ||
                    !toName(fountain.activityState,isActive).equals(controller.getCurrentAnimation().animationName)){
                //only reset the animation when there is a discrepancy in states

                switch(fountain.activityState){
                    case 0 -> {
                        if(isActive){
                            controller.setAnimation(new AnimationBuilder().addAnimation("active-slow"));
                        }
                        else{
                            controller.setAnimation(new AnimationBuilder().addAnimation("slow"));
                        }
                    }
                    case 1 -> {
                        if(isActive){
                            controller.setAnimation(new AnimationBuilder().addAnimation("active-moderate"));
                        }
                        else{
                            controller.setAnimation(new AnimationBuilder().addAnimation("moderate"));
                        }
                    }
                    case 2 -> {
                        if(isActive){
                            controller.setAnimation(new AnimationBuilder().addAnimation("active-fast"));
                        }
                        else{
                            controller.setAnimation(new AnimationBuilder().addAnimation("fast"));
                        }
                    }
                    case 3 -> {
                        if(isActive){
                            controller.setAnimation(new AnimationBuilder().addAnimation("active-hyperspeed"));
                        }
                        else{
                            controller.setAnimation(new AnimationBuilder().addAnimation("hyperspeed"));
                        }
                    }
                }
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "experience_fountain_block_controller", 0, this::predicate));
    }

    private final AnimationFactory manager = GeckoLibUtil.createFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    //-----------PASSIVE BEHAVIOR-----------//

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(blockEntity instanceof ExperienceFountainEntity fountain && fountain.isBound){

            BlockEntity boundEntity = level.getBlockEntity(fountain.getBoundPos());

            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            List<Player> playerList = level.getEntitiesOfClass(Player.class, new AABB(
                    x + 0.25,y + 0.5,z + 0.25,x + 0.75,y + 1.065,z + 0.75));

            if(!playerList.isEmpty() && !fountain.hasPlayerAbove){
                level.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.2f, 0.6f);
                fountain.hasPlayerAbove = true;
                level.sendBlockUpdated(pos, state, state, 2);
            }
            else if(playerList.isEmpty() && fountain.hasPlayerAbove){
                level.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.2f, 0.2f);
                fountain.hasPlayerAbove = false;
                level.sendBlockUpdated(pos, state, state, 2);
            }

            if(boundEntity instanceof ExperienceObeliskEntity obelisk
                    && !level.isClientSide
                    && obelisk.getFluidAmount() > 0
                    && (level.hasNeighborSignal(pos) || fountain.hasPlayerAbove)){

                int value = 4;
                int interval = 20;

                switch (fountain.getActivityState()) {
                    case 1 -> { //40xp/s
                        value = 20;
                        interval = 10;
                    }
                    case 2 -> { //400xp/s
                        value = 100;
                        interval = 5;
                    }
                    case 3 -> { //4000xp/s
                        value = 400;
                        interval = 2;
                    }
                }

                if(value > obelisk.getFluidAmount() / 20){
                    value = obelisk.getFluidAmount() / 20;
                }

                if(level.getGameTime() % interval == 0){
                    ServerLevel server = (ServerLevel) level;
                    ExperienceOrb orb = new ExperienceOrb(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, value);
                    obelisk.drain(value * 20);
                    orb.setDeltaMovement(0, 0.25, 0);
                    server.addFreshEntity(orb);
                }
            }
        }
    }

    //-----------NBT-----------//

    private int activityState = 0;
    public boolean hasPlayerAbove = false;

    public int getActivityState(){
        return activityState;
    }

    public void cycleActivityState(){
        activityState = activityState + 1;
        if(activityState > 3){
            activityState = 0;
        }
        setChanged();
    }

    public String toName(int activityState, boolean isActive){

        switch(activityState){
            case 0 -> {
                return isActive ? "active-slow" : "slow";
            }
            case 1 -> {
                return isActive ? "active-moderate" : "moderate";
            }
            case 2 -> {
                return isActive ? "active-fast" : "fast";
            }
            case 3 -> {
                return isActive ? "active-hyperspeed" : "hyperspeed";
            }
        }
        return null;
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        this.activityState = tag.getInt("ActivityState");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tag.putInt("ActivityState", activityState);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("ActivityState", activityState);

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if(tag != null){
            this.activityState = tag.getInt("ActivityState");
            this.hasPlayerAbove = tag.getBoolean("PlayerAbove");
        }
        super.onDataPacket(net, pkt);
    }


}
