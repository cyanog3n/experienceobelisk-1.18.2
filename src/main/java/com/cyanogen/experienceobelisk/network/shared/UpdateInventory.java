package com.cyanogen.experienceobelisk.network.shared;

import com.cyanogen.experienceobelisk.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UpdateInventory {

    public static CompoundTag container;
    public static CompoundTag inventory;

    public UpdateInventory(CompoundTag container, CompoundTag inventory) {
        UpdateInventory.container = container;
        UpdateInventory.inventory = inventory;
    }

    public UpdateInventory(FriendlyByteBuf buffer) {
        container = buffer.readNbt();
        inventory = buffer.readNbt();

    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeNbt(container);
        buffer.writeNbt(inventory);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            assert sender != null;

            ListTag inventoryList = (ListTag) inventory.get("Inventory");
            ListTag containerList = container.getList("Container", 10);

            sender.getInventory().load(inventoryList);

            for(Slot slot : sender.containerMenu.slots){
                CompoundTag tag = containerList.getCompound(slot.index);
                slot.set(ItemStack.of(tag));
            }

            success.set(true);

        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }

    public static void updateInventoryFromClient(Player player){
        ListTag inventoryList = new ListTag();
        player.getInventory().save(inventoryList);

        ListTag containerList = new ListTag();
        for (Slot slot : player.containerMenu.slots) {
            CompoundTag tag = new CompoundTag();
            slot.getItem().save(tag);
            containerList.add(slot.index, tag);
        }

        CompoundTag inventoryTag = new CompoundTag();
        inventoryTag.put("Inventory", inventoryList);

        CompoundTag containerTag = new CompoundTag();
        containerTag.put("Container", containerList);

        PacketHandler.INSTANCE.sendToServer(new UpdateInventory(containerTag, inventoryTag));
    }
}
