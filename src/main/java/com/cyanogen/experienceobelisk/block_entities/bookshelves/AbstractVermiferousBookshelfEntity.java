package com.cyanogen.experienceobelisk.block_entities.bookshelves;

import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVermiferousBookshelfEntity extends BlockEntity {

    public AbstractVermiferousBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.VERMIFEROUS_BOOKSHELF_BE.get(), pos, state);
    }

    int decayValue = 0;
    int orbValue;
    int durability;


    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(level.getGameTime() % 20 == 0 && blockEntity instanceof AbstractVermiferousBookshelfEntity bookshelf){

            bookshelf.infectAdjacent(level, pos);

            if(bookshelf.decayValue >= bookshelf.durability){
                BlockState newBlock = RegisterBlocks.IGNORAMUS_DUST_BLOCK.get().defaultBlockState();
                level.setBlockAndUpdate(pos, newBlock);

                //turn bookshelf into a block of ignoramus dust
                //play some sound & particle effect i guess
            }
            else{
                bookshelf.incrementDecayValue();
                bookshelf.handleExperience(level, pos);
            }

        }

    }

    public List<Block> getValidBlocksForInfection(){
        List<Block> list = new ArrayList<>();
        list.add(Blocks.BOOKSHELF);
        //add shit here


        return list;
    }

    public int getDecayValue(){
        return this.decayValue;
    }

    public void incrementDecayValue(){
        this.decayValue += 1;
        setChanged();
    }

    public void infectAdjacent(Level level, BlockPos pos){

        AABB area = new AABB(pos.west().getX(), pos.below().getY(), pos.north().getZ(), pos.east().getX(), pos.above().getY(), pos.south().getZ());
        List<BlockPos> list = BlockPos.betweenClosedStream(area).toList();
        List<BlockPos> bookshelfList = new ArrayList<>();

        for(BlockPos position : list){
            if(level.getBlockState(position).is(Blocks.BOOKSHELF)){
                bookshelfList.add(position);
            }
        }

        int index = (int) Math.floor(Math.random() * bookshelfList.size());
        level.setBlockAndUpdate(bookshelfList.get(index), RegisterBlocks.VERMIFEROUS_BOOKSHELF.get().defaultBlockState());
        level.addParticle((ParticleOptions) ParticleTypes.DUST, 0,0,0,0,0,0);
    }

    public void handleExperience(Level level, BlockPos pos){

        if(!level.isClientSide){
            ServerLevel server = (ServerLevel) level;
            ExperienceOrb orb = new ExperienceOrb(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, orbValue);
            server.addFreshEntity(orb);
        }
    }

}
