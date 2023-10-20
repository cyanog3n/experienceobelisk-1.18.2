package com.cyanogen.experienceobelisk.network.experienceobelisk;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;


public class UpdateToServer {

    public static BlockPos pos;
    public static int XP;
    public static Request request;

    public UpdateToServer(BlockPos pos, int XP, Request request) {
        this.pos = pos;
        this.XP = XP;
        this.request = request;
    }

    public enum Request{
        FILL,
        DRAIN,
        FILL_ALL,
        DRAIN_ALL
    }

    public UpdateToServer(FriendlyByteBuf buffer) {

        pos = buffer.readBlockPos();
        XP = buffer.readInt();
        request = buffer.readEnum(Request.class);

    }

    public void encode(FriendlyByteBuf buffer){

        buffer.writeBlockPos(pos);
        buffer.writeInt(XP);
        buffer.writeEnum(request);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {

        final var success = new AtomicBoolean(false);

        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            assert sender != null;
            BlockEntity serverEntity = sender.level.getBlockEntity(pos);

            if(serverEntity instanceof ExperienceObeliskEntity xpobelisk){

                xpobelisk.handleRequest(request, XP, sender);
                success.set(true);

            }

        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }

}
