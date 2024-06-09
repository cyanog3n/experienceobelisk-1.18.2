package com.cyanogen.experienceobelisk.block.bookworm;

import com.cyanogen.experienceobelisk.block_entities.bookworm.AbstractVermiferousBookshelfEntity;
import com.cyanogen.experienceobelisk.block_entities.bookworm.VermiferousBookshelfEntity;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VermiferousBookshelfBlock extends Block implements EntityBlock {

    public VermiferousBookshelfBlock() {
        super(Properties.copy(Blocks.BOOKSHELF));
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
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return null;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if(level.getBlockEntity(pos) instanceof AbstractVermiferousBookshelfEntity bookshelf){
            int remaining = bookshelf.getDurability() - bookshelf.getDecayValue();
            int remainingProduction = remaining * bookshelf.getOrbValue() / (bookshelf.getAverageSpawnDelay() / 20);

            int totalValue = (int) (remainingProduction * 0.377 * Math.random());
            int orbCount = (int) (1 + Math.random() * 4);
            int orbValue = totalValue / orbCount;

            if(!level.isClientSide && totalValue > 10){
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
        return blockEntityType == RegisterBlockEntities.VERMIFEROUS_BOOKSHELF_BE.get() ? VermiferousBookshelfEntity::tick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegisterBlockEntities.VERMIFEROUS_BOOKSHELF_BE.get().create(pos, state);
    }
}
