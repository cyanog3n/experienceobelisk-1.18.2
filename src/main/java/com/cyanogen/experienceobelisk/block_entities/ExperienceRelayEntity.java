package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExperienceRelayEntity extends ExperienceReceivingEntity{

    public ExperienceRelayEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.EXPERIENCERELAY_BE.get(), pos, state);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(blockEntity instanceof ExperienceRelayEntity relay && relay.isBound){

            BlockEntity boundEntity = level.getBlockEntity(relay.getBoundPos());

            if(boundEntity instanceof ExperienceRelayEntity sendingRelay && sendingRelay.getFluidAmount() != 0 && relay.getSpace() != 0){
                int sendAmount = Math.min(sendingRelay.getFluidAmount(), relay.getSpace());

                relay.fill(sendAmount);
                sendingRelay.drain(sendAmount);
            }
            else if(boundEntity instanceof ExperienceObeliskEntity obelisk && obelisk.getFluidAmount() != 0 && relay.getSpace() != 0){
                int sendAmount = Math.min(obelisk.getFluidAmount(), relay.getSpace());

                relay.fill(sendAmount);
                obelisk.drain(sendAmount);
            }
        }

    }

    //-----------FLUID HANDLER-----------//

    protected FluidTank tank = experienceRelayTank();

    private final LazyOptional<IFluidHandler> handler = LazyOptional.of(() -> tank);

    private static final Fluid cognitium = RegisterFluids.COGNITIUM.get().getSource();

    private FluidTank experienceRelayTank() {
        return new FluidTank(1000){

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
                return stack.getFluid() == cognitium;
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

    //-----------NBT-----------//

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
        tank.readFromNBT(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
        tank.writeToNBT(tag);
    }

    //sends CompoundTag out with nbt data
    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();
        tank.writeToNBT(tag);

        return tag;
    }

    //gets packet to send to client
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == ForgeCapabilities.FLUID_HANDLER)
            return handler.cast();
        return super.getCapability(capability, facing);
    }

}
