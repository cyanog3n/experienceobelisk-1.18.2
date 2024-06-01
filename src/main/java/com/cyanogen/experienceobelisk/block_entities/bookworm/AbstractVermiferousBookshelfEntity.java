package com.cyanogen.experienceobelisk.block_entities.bookworm;

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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractVermiferousBookshelfEntity extends BlockEntity {

    public AbstractVermiferousBookshelfEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.VERMIFEROUS_BOOKSHELF_BE.get(), pos, state);
    }

    int decayValue = 0;
    int orbValue;
    int durability;

    //-----------BEHAVIOR-----------//

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(level.getGameTime() % 20 == 0 && blockEntity instanceof AbstractVermiferousBookshelfEntity bookshelf){

            if(Math.random() <= 0.15){
                bookshelf.infectAdjacent(level, pos);
            }

            if(bookshelf.decayValue >= bookshelf.durability){
                BlockState dustBlock = RegisterBlocks.IGNORAMUS_DUST_BLOCK.get().defaultBlockState();
                level.setBlockAndUpdate(pos, dustBlock);

                //todo: play some sound & particle effect
            }
            else{
                List<VermiferousCartographersBookshelfEntity> cartoBookshelves = bookshelf.getAdjacentCartographersBookshelves(level, pos);
                if(cartoBookshelves.isEmpty()){
                    bookshelf.incrementDecayValue();
                }
                else{
                    cartoBookshelves.get((int) Math.floor(Math.random() * cartoBookshelves.size())).decayValue += 1;
                }

                bookshelf.handleExperience(level, pos);
            }

        }

    }

    public void infectAdjacent(Level level, BlockPos pos){

        List<BlockPos> posList = new ArrayList<>();
        List<Block> blockList = new ArrayList<>();

        for(BlockPos adjacent : getAdjacents(pos)){
            if(getValidBlocksForInfection().contains(level.getBlockState(adjacent).getBlock())){
                posList.add(adjacent);
                blockList.add(level.getBlockState(adjacent).getBlock());
            }
        }

        if(!posList.isEmpty()){

            int index = (int) Math.floor(Math.random() * posList.size());
            BlockPos posToInfect = posList.get(index);
            Block block = blockList.get(index);

            if(block.equals(Blocks.BOOKSHELF)){
                level.setBlockAndUpdate(posToInfect, RegisterBlocks.VERMIFEROUS_BOOKSHELF.get().defaultBlockState());
            }
            else if(block.equals(RegisterBlocks.ENCHANTED_BOOKSHELF.get())){
                level.setBlockAndUpdate(posToInfect, RegisterBlocks.VERMIFEROUS_ENCHANTED_BOOKSHELF.get().defaultBlockState());
            }
            else if(block.equals(RegisterBlocks.ARCHIVERS_BOOKSHELF.get())){
                level.setBlockAndUpdate(posToInfect, RegisterBlocks.VERMIFEROUS_ARCHIVERS_BOOKSHELF.get().defaultBlockState());
            }
            else if(block.equals(RegisterBlocks.CARTOGRAPHERS_BOOKSHELF.get())){
                level.setBlockAndUpdate(posToInfect, RegisterBlocks.VERMIFEROUS_CARTOGRAPHERS_BOOKSHELF.get().defaultBlockState());
            }

            level.addParticle((ParticleOptions) ParticleTypes.DUST, pos.getX(), pos.getY(), pos.getZ(),0,0,0);
        }
    }

    public void handleExperience(Level level, BlockPos pos){

        if(!level.isClientSide){
            ServerLevel server = (ServerLevel) level;
            ExperienceOrb orb = new ExperienceOrb(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, orbValue);
            server.addFreshEntity(orb);
        }
    }

    public List<Block> getValidBlocksForInfection(){
        List<Block> list = new ArrayList<>();
        list.add(Blocks.BOOKSHELF);
        list.add(RegisterBlocks.ENCHANTED_BOOKSHELF.get());
        list.add(RegisterBlocks.ARCHIVERS_BOOKSHELF.get());
        list.add(RegisterBlocks.CARTOGRAPHERS_BOOKSHELF.get());

        return list;
    }

    public List<BlockPos> getAdjacents(BlockPos pos){
        List<BlockPos> list = new ArrayList<>();
        list.add(pos.above());
        list.add(pos.below());
        list.add(pos.north());
        list.add(pos.south());
        list.add(pos.east());
        list.add(pos.west());

        return list;
    }

    public List<VermiferousCartographersBookshelfEntity> getAdjacentCartographersBookshelves(Level level, BlockPos pos){

        List<VermiferousCartographersBookshelfEntity> list = new ArrayList<>();

        for(BlockPos adjacent : getAdjacents(pos)){
            BlockEntity entity = level.getBlockEntity(adjacent);
            if(entity instanceof VermiferousCartographersBookshelfEntity){
                list.add((VermiferousCartographersBookshelfEntity) entity);
            }
        }

        return list;
    }

    public void incrementDecayValue(){
        this.decayValue += 1;
        setChanged();
    }



}
