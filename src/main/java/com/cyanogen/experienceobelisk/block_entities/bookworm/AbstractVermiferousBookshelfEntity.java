package com.cyanogen.experienceobelisk.block_entities.bookworm;

import com.cyanogen.experienceobelisk.registries.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static com.cyanogen.experienceobelisk.item.BookwormItem.getValidBlocksForInfection;
import static com.cyanogen.experienceobelisk.item.BookwormItem.infectBlock;

public abstract class AbstractVermiferousBookshelfEntity extends BlockEntity {

    public AbstractVermiferousBookshelfEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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

        if(blockEntity instanceof AbstractVermiferousBookshelfEntity bookshelf && !bookshelf.isDisplay){

            if(bookshelf.decayValue >= bookshelf.durability){
                BlockState dustBlock = RegisterBlocks.IGNORAMUS_DUST_BLOCK.get().defaultBlockState();
                level.setBlockAndUpdate(pos, dustBlock);

                //todo: play some sound & particle effect
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

            }

            if(level.getGameTime() % 20 == 0){
                bookshelf.incrementDecayValue(level, pos);

                if(Math.random() <= 0.025){
                    bookshelf.infectAdjacent(level, pos);
                }
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

            infectBlock(level, posToInfect, block);
            //level.addParticle((ParticleOptions) ParticleTypes.DUST, pos.getX(), pos.getY(), pos.getZ(),0,0,0);
            //find out how to add particles
        }
    }

    public void incrementDecayValue(Level level, BlockPos pos){

        double threshold;

        if(getAdjacentBlockStates(level, pos).contains(RegisterBlocks.COGNITIVE_ALLOY_BLOCK.get().defaultBlockState())){
            //change to enlightened alloy later
            threshold = 0.5;
        }
        else{
            threshold = 0.75;
        }

        if(Math.random() <= threshold){
            this.decayValue += 1;
        }
        setChanged();
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

        if(getAdjacentBlockStates(level, pos).contains(RegisterBlocks.COGNITIVE_CRYSTAL_BLOCK.get().defaultBlockState())){
            //change to enlightened crystal later
            value = (int) (value * 1.5);
        }

        if(!level.isClientSide){
            ServerLevel server = (ServerLevel) level;
            ExperienceOrb orb = new ExperienceOrb(server, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, value);
            orb.setDeltaMovement(0,0,0);

            server.addFreshEntity(orb);
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
