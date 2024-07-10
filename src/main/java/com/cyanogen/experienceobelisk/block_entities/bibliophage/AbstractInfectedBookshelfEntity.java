package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.registries.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cyanogen.experienceobelisk.item.BibliophageItem.getValidBlocksForInfection;
import static com.cyanogen.experienceobelisk.item.BibliophageItem.infectBlock;

public abstract class AbstractInfectedBookshelfEntity extends BlockEntity {

    public AbstractInfectedBookshelfEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    int decayValue = 0; //the current decay value of the bookshelf
    int spawnDelay = -99; //the current time in ticks until the bookshelf is due to spawn an orb
    int spawnDelayMin; //the minimum spawn delay for the bookshelf
    int spawnDelayMax; //the maximum spawn delay for the bookshelf
    int orbValue; //the value of orbs to spawn
    int durability; //the durability of the bookshelf
    boolean isDisplay = false; //whether or not the bookshelf is a display block. with display=true, bookshelves will not infect adjacents, produce XP, or decay

    //-----------BEHAVIOR-----------//

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(blockEntity instanceof AbstractInfectedBookshelfEntity bookshelf && !bookshelf.isDisplay){

            if(bookshelf.decayValue >= bookshelf.durability){
                bookshelf.decay(level, pos);
            }
            else{

                if(bookshelf.spawnDelay == -99){
                    bookshelf.resetSpawnDelay();
                }
                else if(bookshelf.spawnDelay <= 0){
                    bookshelf.handleExperience(level, pos);
                    bookshelf.resetSpawnDelay();
                }
                else{
                    bookshelf.decrementSpawnDelay();
                }

                if(level.getGameTime() % 20 == 0){
                    bookshelf.incrementDecayValue(level, pos);

                    if(Math.random() <= 0.022){
                        bookshelf.infectAdjacent(level, pos);
                    }
                }

            }

        }

    }

    public void infectAdjacent(Level level, BlockPos pos){

        Map<BlockPos, Block> adjacentMap = new HashMap<>();
        List<BlockPos> posList = new ArrayList<>();

        for(BlockPos adjacentPos : getAdjacents(pos)){
            if(getValidBlocksForInfection().contains(level.getBlockState(adjacentPos).getBlock())){

                Block adjacentBlock = level.getBlockState(adjacentPos).getBlock();
                adjacentMap.put(adjacentPos, adjacentBlock);
                posList.add(adjacentPos);
            }
        }

        if(!adjacentMap.isEmpty()){

            int index = (int) Math.floor(Math.random() * posList.size());
            BlockPos posToInfect = posList.get(index);
            Block block = adjacentMap.get(posToInfect);

            infectBlock(level, posToInfect, block);
        }
    }

    public void incrementDecayValue(Level level, BlockPos pos){

        double threshold = 0.75;
//        int multiplier = enumerateAdjacentsOfType(level, pos, RegisterBlocks.COGNITIVE_ALLOY_BLOCK.get().defaultBlockState());
//
//        if(multiplier > 0){
//            threshold = threshold / Math.pow(1.5, multiplier);
//        }

        if(Math.random() <= threshold){
            this.decayValue += 1;
            setChanged();
        }

    }

    public void resetSpawnDelay(){
        this.spawnDelay = (int) (spawnDelayMin + Math.floor((spawnDelayMax - spawnDelayMin) * Math.random()));
        this.setChanged();
    }

    public void decrementSpawnDelay(){
        this.spawnDelay -= 1;
        this.setChanged();
    }

    public void handleExperience(Level level, BlockPos pos){

        int value = orbValue;
//        int multiplier = enumerateAdjacentsOfType(level, pos, RegisterBlocks.COGNITIVE_CRYSTAL_BLOCK.get().defaultBlockState());
//
//        if(multiplier > 0){
//            value = (int) (value * Math.pow(1.5, multiplier));
//        }

        if(!level.isClientSide){
            ServerLevel server = (ServerLevel) level;
            ExperienceOrb orb = new ExperienceOrb(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, value);
            orb.setDeltaMovement(0,0,0);

            server.addFreshEntity(orb);
        }
    }

    public void decay(Level level, BlockPos pos){

        BlockState dustBlock = RegisterBlocks.FORGOTTEN_DUST_BLOCK.get().defaultBlockState();

        level.playSound(null, pos, SoundEvents.WART_BLOCK_BREAK, SoundSource.BLOCKS, 1f,1f);
        level.levelEvent(null, 2001, pos, Block.getId(dustBlock)); //spawn destroy particles

        if(!level.isClientSide){
            ServerLevel server = (ServerLevel) level;
            server.setBlockAndUpdate(pos, dustBlock);
        }
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

    public List<BlockState> getAdjacentBlockStates(Level level, BlockPos pos){
        List<BlockState> list = new ArrayList<>();
        for(BlockPos adjacent : getAdjacents(pos)){
            list.add(level.getBlockState(adjacent));
        }

        return list;
    }

    public int enumerateAdjacentsOfType(Level level, BlockPos pos, BlockState state){

        int count = 0;

        for(BlockState adjacent : getAdjacentBlockStates(level, pos)){
            if(adjacent.equals(state)){
                count++;
            }
        }
        return count;
    }

    public boolean toggleDisplay(){
        this.isDisplay = !this.isDisplay;
        this.setChanged();

        return this.isDisplay;
    }

    public int getDecayValue(){
        return this.decayValue;
    }

    public int getOrbValue(){
        return this.orbValue;
    }

    public int getDurability(){
        return this.durability;
    }

    public int getAverageSpawnDelay(){
        return (this.spawnDelayMax + this.spawnDelayMin) / 2;
    }

    //-----------NBT-----------//

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);

        this.decayValue = tag.getInt("DecayValue");
        this.spawnDelay = tag.getInt("SpawnDelay");
        this.isDisplay = tag.getBoolean("IsDisplay");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.putInt("DecayValue", decayValue);
        tag.putInt("SpawnDelay", spawnDelay);
        tag.putBoolean("IsDisplay", isDisplay);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();

        tag.putInt("DecayValue", decayValue);
        tag.putInt("SpawnDelay", spawnDelay);
        tag.putBoolean("IsDisplay", isDisplay);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }



}
