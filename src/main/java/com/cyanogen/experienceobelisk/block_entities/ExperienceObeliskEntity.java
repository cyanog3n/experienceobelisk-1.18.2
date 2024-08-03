package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.config.Config;
import com.cyanogen.experienceobelisk.network.experience_obelisk.UpdateContents;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterFluids;
import com.cyanogen.experienceobelisk.registries.RegisterTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.cyanogen.experienceobelisk.utils.ExperienceUtils.levelsToXP;
import static com.cyanogen.experienceobelisk.utils.ExperienceUtils.xpToLevels;
import static com.cyanogen.experienceobelisk.utils.MiscUtils.isSameAnimation;


public class ExperienceObeliskEntity extends BlockEntity implements IAnimatable{

    public ExperienceObeliskEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.EXPERIENCE_OBELISK_BE.get(), pos, state);
    }

    //-----------ANIMATIONS-----------//

    //events that control what animation is being played
    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.transitionLengthTicks = 0;

        BlockEntity entity = event.getAnimatable();
        Animation animation = event.getController().getCurrentAnimation();
        AnimationBuilder animationToPlay;

        if(level != null
                && entity instanceof ExperienceObeliskEntity obelisk
                && obelisk.redstoneEnabled
                && !level.hasNeighborSignal(obelisk.getBlockPos())){
            animationToPlay = new AnimationBuilder().addAnimation("idle.inactive");
        }
        else{
            animationToPlay = new AnimationBuilder().addAnimation("idle");
        }

        if(controller.getCurrentAnimation() == null
                || !isSameAnimation(animation, animationToPlay)
                || controller.getAnimationState().equals(AnimationState.Stopped)){

            controller.setAnimation(animationToPlay);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "experience_obelisk_block_controller", 0, this::predicate));
    }

    private final AnimationFactory manager = GeckoLibUtil.createFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    //-----------PASSIVE BEHAVIOR-----------//

    protected boolean redstoneEnabled = false;
    protected double radius = 2.5;

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        boolean isRedstonePowered = level.hasNeighborSignal(pos);

        if(blockEntity instanceof ExperienceObeliskEntity obelisk){

            boolean absorb = !obelisk.isRedstoneEnabled() || isRedstonePowered;
            double radius = obelisk.getRadius();
            int space = obelisk.getSpace();

            if(absorb && level.getGameTime() % 10 == 0){
                AABB area = new AABB(
                        pos.getX() - radius,
                        pos.getY() - radius,
                        pos.getZ() - radius,
                        pos.getX() + radius,
                        pos.getY() + radius,
                        pos.getZ() + radius);

                List<ExperienceOrb> list = level.getEntitiesOfClass(ExperienceOrb.class, area);

                if(!list.isEmpty()) for(int i = 0; i < Math.min(30,list.size()); i++){

                    ExperienceOrb orb = list.get(i);
                    CompoundTag tag = new CompoundTag();
                    orb.addAdditionalSaveData(tag);

                    int value = orb.value;
                    int count = tag.getInt("Count");

                    int amount = value * 20 * count;
                    if(space >= amount){
                        obelisk.fill(amount);
                        space = space - amount;
                        orb.discard();
                    }
                }
            }
        }
    }

    public boolean isRedstoneEnabled(){
        return this.redstoneEnabled;
    }

    public void setRedstoneEnabled(boolean state){
        this.redstoneEnabled = state;
        this.setChanged();
    }

    public double getRadius(){
        return this.radius;
    }

    public void setRadius(double radius){
        this.radius = radius;
        this.setChanged();
    }

    @Override
    public void setChanged() {
        if(this.level != null){
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
        }
        super.setChanged();
    }

    //-----------FLUID HANDLER-----------//

    protected FluidTank tank = experienceObeliskTank();

    private final LazyOptional<IFluidHandler> handler = LazyOptional.of(() -> tank);

    private static final Fluid cognitium = RegisterFluids.COGNITIUM.get().getSource();

    public static final int capacity = Config.COMMON.capacity.get() % 20 == 0 ? Config.COMMON.capacity.get() : Config.COMMON.defaultCapacity;

    private FluidTank experienceObeliskTank() {
        return new FluidTank(capacity){

            @Override
            protected void onContentsChanged()
            {
                setChanged();
            }

            @Override
            public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
                return isFluidValid(stack);
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                String fluidName = String.valueOf(ForgeRegistries.FLUIDS.getKey(stack.getFluid()));

                if(stack.getFluid() == cognitium){
                    return true;
                }
                else{
                    return stack.getFluid().is(RegisterTags.Fluids.EXPERIENCE) && Config.COMMON.allowedFluids.get().contains(fluidName);
                }
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {

                if(isFluidValid(resource)){
                    setChanged();
                    return super.fill(new FluidStack(cognitium, resource.getAmount()), action);
                }
                else{
                    return 0;
                }
            }

            @NotNull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                setChanged();
                return super.drain(maxDrain, action);
            }

            @NotNull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                setChanged();
                return super.drain(resource, action);
            }

            @Override
            public void setFluid(FluidStack stack)
            {
                this.fluid = stack;
                setChanged();
            }

            @Override
            public int getTanks() {
                return 1;
            }
        };
    }

    public int fill(int amount){
        return tank.fill(new FluidStack(cognitium, amount), IFluidHandler.FluidAction.EXECUTE);
    }

    public void drain(int amount)
    {
        tank.drain(new FluidStack(cognitium, amount), IFluidHandler.FluidAction.EXECUTE);
    }

    public void setFluid(int amount)
    {
        tank.setFluid(new FluidStack(cognitium, amount));
    }

    public int getFluidAmount(){
        return tank.getFluidAmount();
    }

    public int getSpace(){ return tank.getSpace(); }

    public int getExperiencePoints(){
        return getFluidAmount() / 20;
    }

    public int getLevels(){
        return xpToLevels(getExperiencePoints());
    }

    public double getProgressToNextLevel(){

        int n = getExperiencePoints() - levelsToXP(getLevels()); //remaining xp after levels are removed
        int m = levelsToXP(getLevels() + 1) - levelsToXP(getLevels()); //total xp to get to next level

        return (double) n/m;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == ForgeCapabilities.FLUID_HANDLER)
            return handler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        handler.invalidate();
        super.invalidateCaps();
    }

    //-----------NBT-----------//

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        tank.readFromNBT(tag);

        this.radius = tag.getDouble("Radius");
        this.redstoneEnabled = tag.getBoolean("isRedstoneControllable");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tank.writeToNBT(tag);

        tag.putDouble("Radius", radius);
        tag.putBoolean("isRedstoneControllable", redstoneEnabled);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();
        tank.writeToNBT(tag);

        tag.putDouble("Radius", radius);
        tag.putBoolean("isRedstoneControllable", redstoneEnabled);

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    //-----------LOGIC-----------//

    public static long getTotalXP(Player player){
        return levelsToXP(player.experienceLevel) + Math.round(player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public void handleRequest(UpdateContents.Request request, int XP, ServerPlayer sender){

        long playerXP = getTotalXP(sender);
        long finalXP;

        if(request == UpdateContents.Request.FILL && this.getSpace() != 0){

            //-----FILLING-----//

            //final amount of experience points the player will have after storing n levels
            finalXP = levelsToXP(sender.experienceLevel - XP) + Math.round(sender.experienceProgress *
                    (levelsToXP(sender.experienceLevel - XP + 1) - levelsToXP(sender.experienceLevel - XP)));

            long addAmount = (playerXP - finalXP) * 20;

            //if amount to add exceeds remaining capacity
            if(addAmount >= this.getSpace()){
                sender.giveExperiencePoints(-this.fill(this.getSpace()) / 20); //fill up however much is left and deduct that amount frm player
            }

            //normal operation
            else if(sender.experienceLevel >= XP){

                this.fill((int) (addAmount));
                sender.giveExperienceLevels(-XP);

            }
            //if player has less than the required XP
            else if (playerXP >= 1){

                this.fill((int) (playerXP * 20));
                sender.setExperiencePoints(0);
                sender.setExperienceLevels(0);

            }
        }

        //-----DRAINING-----//

        else if(request == UpdateContents.Request.DRAIN){

            int amount = this.getFluidAmount();

            finalXP = levelsToXP(sender.experienceLevel + XP) + Math.round(sender.experienceProgress *
                    (levelsToXP(sender.experienceLevel + XP + 1) - levelsToXP(sender.experienceLevel + XP)));

            long drainAmount = (finalXP - playerXP) * 20;

            //normal operation
            if(amount >= drainAmount){

                this.drain((int) drainAmount);
                sender.giveExperienceLevels(XP);

            }
            else if(amount >= 1){

                sender.giveExperiencePoints(amount / 20);
                this.setFluid(0);
            }
        }

        //-----FILL OR DRAIN ALL-----//

        else if(request == UpdateContents.Request.FILL_ALL){

            if(playerXP * 20 <= this.getSpace()){
                this.fill((int) (playerXP * 20));
                sender.setExperiencePoints(0);
                sender.setExperienceLevels(0);
            }
            else{
                sender.giveExperiencePoints(-this.getSpace() / 20);
                this.setFluid(capacity);
            }

        }
        else if(request == UpdateContents.Request.DRAIN_ALL){

            sender.giveExperiencePoints(this.getFluidAmount() / 20);
            this.setFluid(0);
        }
    }


}


