package com.cyanogen.experienceobelisk.block.bibliophage;

import com.cyanogen.experienceobelisk.block_entities.bibliophage.AbstractInfectedBookshelfEntity;
import com.cyanogen.experienceobelisk.block_entities.bibliophage.InfectedBookshelfEntity;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class InfectedBookshelfBlock extends BookshelfBlock implements EntityBlock {

    public InfectedBookshelfBlock(float enchantPowerBonus) {
        super(enchantPowerBonus);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {

        if(context instanceof EntityCollisionContext entityCollisionContext){
            Entity e = entityCollisionContext.getEntity();
            if(e instanceof ExperienceOrb){
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, getter, pos, context);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);

        if(stack.is(RegisterItems.ATTUNEMENT_STAFF.get()) && !player.isShiftKeyDown()
                && level.getBlockEntity(pos) instanceof AbstractInfectedBookshelfEntity bookshelf){

            Component message;
            int decayValue = bookshelf.getDecayValue();
            int spawns = bookshelf.getSpawns();
            int durability = 100 - (decayValue * 100 / spawns);
            MutableComponent durabilityStatus = Component.literal(durability+"%");
            boolean isDisabled = bookshelf.getRedstoneEnabled();

            if(durability > 50){ //green
                durabilityStatus.withStyle(ChatFormatting.GREEN);
            }
            else if(durability  > 25){ //yellow
                durabilityStatus.withStyle(ChatFormatting.YELLOW);
            }
            else{ //red
                durabilityStatus.withStyle(ChatFormatting.RED);
            }

            if(isDisabled){
                message = Component.translatable("message.experienceobelisk.binding_wand.query_bookshelf_disabled", durabilityStatus);
            }
            else{
                message = Component.translatable("message.experienceobelisk.binding_wand.query_bookshelf", durabilityStatus);
            }

            if(!level.isClientSide){
                player.displayClientMessage(message, true);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, result);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if(level.getBlockEntity(pos) instanceof AbstractInfectedBookshelfEntity bookshelf){
            int remaining = bookshelf.getSpawns() - bookshelf.getDecayValue();
            int remainingProduction = remaining * bookshelf.getOrbValue();

            int totalValue = (int) (remainingProduction * 0.377 * Math.random()); //between 0 - 0.377 of the remaining XP
            int orbCount = (int) (2 + Math.random() * 3); //between 2 and 5 orbs
            int orbValue = totalValue / orbCount;

            if(!level.isClientSide && totalValue > 10 && !player.isCreative()){
                ServerLevel server = (ServerLevel) level;

                for(int i = 0; i < orbCount; i++){
                    server.addFreshEntity(new ExperienceOrb(level, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, orbValue));
                }
            }
        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    //-----BLOCK ENTITY-----//

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == RegisterBlockEntities.INFECTED_BOOKSHELF_BE.get() ? InfectedBookshelfEntity::tick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegisterBlockEntities.INFECTED_BOOKSHELF_BE.get().create(pos, state);
    }
}
