package com.cyanogen.experienceobelisk.network.experienceobelisk;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class XPObeliskUpdateRedstone {

    public static BlockPos pos;
    public static boolean isControllable;

    public XPObeliskUpdateRedstone(BlockPos pos, boolean isControllable) {
        this.pos = pos;
        this.isControllable = isControllable;
    }

    public XPObeliskUpdateRedstone(FriendlyByteBuf buffer) {

        pos = buffer.readBlockPos();
        isControllable = buffer.readBoolean();

    }

    public void encode(FriendlyByteBuf buffer){

        buffer.writeBlockPos(pos);
        buffer.writeBoolean(isControllable);

    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            assert sender != null;

            BlockEntity serverEntity = sender.level.getBlockEntity(pos);

            if(serverEntity instanceof ExperienceObeliskEntity xpobelisk){

                xpobelisk.setRedstoneEnabled(isControllable);
            }

            success.set(true);

        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}
