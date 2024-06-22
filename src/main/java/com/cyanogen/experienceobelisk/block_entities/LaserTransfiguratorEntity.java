package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LaserTransfiguratorEntity extends ExperienceReceivingEntity implements GeoBlockEntity {

    public LaserTransfiguratorEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(RegisterBlockEntities.LASER_TRANSFIGURATOR_BE.get(), p_155229_, p_155230_);
    }

    int processTime = 0;
    int processProgress = 0;
    ItemStack outputItem = null;

    //-----------ANIMATIONS-----------//

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    protected <E extends ExperienceFountainEntity> PlayState controller(final AnimationState<E> state) {
        return null;
    }

    //-----------BEHAVIOR-----------//

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

    }

    //-----------ITEM HANDLER-----------//


    protected ItemStackHandler itemHandler = transfiguratorHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private ItemStackHandler transfiguratorHandler(){

        return new ItemStackHandler(4){

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if(slot == 4){
                    return false;
                }
                else{
                    return true;
                }
            }
        };
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == ForgeCapabilities.ITEM_HANDLER)
            return handler.cast();
        return super.getCapability(capability, facing);
    }

    //-----------NBT-----------//

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);

        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.processTime = tag.getInt("ProcessTime");
        this.processProgress = tag.getInt("ProcessProgress");
        this.outputItem = ItemStack.of(tag.getCompound("OutputItem"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);
        tag.put("OutputItem", outputItem.getOrCreateTag());
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();

        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);
        tag.put("OutputItem", outputItem.getOrCreateTag());

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
