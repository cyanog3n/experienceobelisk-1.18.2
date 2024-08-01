package com.cyanogen.experienceobelisk.block_entities.bibliophage;

import com.cyanogen.experienceobelisk.registries.RegisterBlocks;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

    int timeTillSpawn = -99; //the current time in ticks until the bookshelf is due to spawn an orb
    int spawnDelayMin; //the minimum spawn delay for the bookshelf
    int spawnDelayMax; //the maximum spawn delay for the bookshelf
    int orbValue; //the value of orbs to spawn
    int spawns; //the number of times a bookshelf can spawn an orb before decaying
    int decayValue = 0; //the number of times a bookshelf has spawned an orb
    double infectivity = 0.02; //the chance for a bookshelf to infect another adjacent bookshelf every second
    boolean isDisabled = false; //whether or not the bookshelf is disabled. When disabled, bookshelves will not infect adjacents, produce XP, or decay

    //-----------BEHAVIOR-----------//

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(blockEntity instanceof AbstractInfectedBookshelfEntity bookshelf && !bookshelf.isDisabled){

            if(bookshelf.decayValue >= bookshelf.spawns){
                bookshelf.decay(level, pos);
            }
            else{

                if(bookshelf.timeTillSpawn == -99){
                    bookshelf.resetSpawnDelay();
                }
                else if(bookshelf.timeTillSpawn <= 0){
                    bookshelf.handleExperience(level, pos);
                    bookshelf.incrementDecayValue();
                    bookshelf.resetSpawnDelay();
                }
                else{
                    bookshelf.decrementSpawnDelay();
                }

                if(level.getGameTime() % 20 == 0 && Math.random() <= bookshelf.infectivity){
                    bookshelf.infectAdjacent(level, pos);
                }

            }

        }

    }

    public void infectAdjacent(Level level, BlockPos pos){

        Map<BlockPos, Block> adjacentMap = new HashMap<>();
        List<BlockPos> posList = new ArrayList<>();

        if(!level.isClientSide){
            for(BlockPos adjacentPos : getAdjacents(pos)){
                if(getValidBlocksForInfection().contains(level.getBlockState(adjacentPos).getBlock())){

                    Block adjacentBlock = level.getBlockState(adjacentPos).getBlock();
                    adjacentMap.put(adjacentPos, adjacentBlock);
                    posList.add(adjacentPos);
                }
            }
        }

        if(!adjacentMap.isEmpty()){

            int index = (int) Math.floor(Math.random() * posList.size());
            BlockPos posToInfect = posList.get(index);
            Block block = adjacentMap.get(posToInfect);

            infectBlock(level, posToInfect, block);
        }
    }

    public void resetSpawnDelay(){
        this.timeTillSpawn = (int) (spawnDelayMin + Math.floor((spawnDelayMax - spawnDelayMin) * Math.random()));
        this.setChanged();
    }

    public void decrementSpawnDelay(){
        this.timeTillSpawn -= 1;
        this.setChanged();
    }

    public void handleExperience(Level level, BlockPos pos){

        int value = orbValue;

        if(!level.isClientSide){
            ServerLevel server = (ServerLevel) level;
            ExperienceOrb orb = new ExperienceOrb(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, value);
            orb.setDeltaMovement(0,0,0);

            server.addFreshEntity(orb);
        }
    }

    public void incrementDecayValue(){
        this.decayValue += 1;
        this.setChanged();
    }

    public void decay(Level level, BlockPos pos){

        setDisabled(true);

        if(!level.isClientSide){
            ServerLevel server = (ServerLevel) level;
            ItemStack forgottenDust = new ItemStack(RegisterItems.FORGOTTEN_DUST.get(), 4);
            Block.popResource(server, pos, forgottenDust);
        }
        level.playSound(null, pos, SoundEvents.WART_BLOCK_BREAK, SoundSource.BLOCKS, 1f,1f); //play break sound
        level.levelEvent(null, 2001, pos, Block.getId(RegisterBlocks.FORGOTTEN_DUST_BLOCK.get().defaultBlockState())); //spawn destroy particles

        this.setRemoved();
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
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

    public boolean toggleActivity(){
        this.isDisabled = !this.isDisabled;
        this.setChanged();

        return this.isDisabled;
    }

    public void setDisabled(boolean disabled){
        this.isDisabled = disabled;
        this.setChanged();
    }

    public boolean getDisabled(){return this.isDisabled;}

    public int getDecayValue(){
        return this.decayValue;
    }

    public int getOrbValue(){
        return this.orbValue;
    }

    public int getSpawns(){
        return this.spawns;
    }

    //-----------NBT-----------//

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);

        this.decayValue = tag.getInt("DecayValue");
        this.timeTillSpawn = tag.getInt("SpawnDelay");
        this.isDisabled = tag.getBoolean("IsDisabled");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.putInt("DecayValue", decayValue);
        tag.putInt("SpawnDelay", timeTillSpawn);
        tag.putBoolean("IsDisabled", isDisabled);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();

        tag.putInt("DecayValue", decayValue);
        tag.putInt("SpawnDelay", timeTillSpawn);
        tag.putBoolean("IsDisabled", isDisabled);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }



}
