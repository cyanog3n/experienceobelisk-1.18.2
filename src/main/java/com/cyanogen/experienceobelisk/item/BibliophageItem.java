package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.registries.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class BibliophageItem extends Item {

    public BibliophageItem(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Block block = level.getBlockState(pos).getBlock();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if(getValidBlocksForInfection().contains(block)){
            infectBlock(level, pos, block);

            if(player != null && !player.isCreative()){
                stack.shrink(1);
            }

            if(!level.isClientSide){
                ServerLevel server = (ServerLevel) level;
                level.playSound(null, pos, SoundEvents.WART_BLOCK_BREAK, SoundSource.BLOCKS, 1f,1f);
                server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, RegisterBlocks.INFECTED_BOOKSHELF.get().defaultBlockState())
                        , pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 25, 0.1,0.1,0.1,2);
                //type, xpos, ypos, zpos, count, xdelta, ydelta, zdelta, maxspeed
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useOn(context);
    }

    public static List<Block> getValidBlocksForInfection(){
        List<Block> list = new ArrayList<>();
        list.add(Blocks.BOOKSHELF);
        list.add(RegisterBlocks.ENCHANTED_BOOKSHELF.get());
        list.add(RegisterBlocks.ARCHIVERS_BOOKSHELF.get());
        list.add(RegisterBlocks.CARTOGRAPHERS_BOOKSHELF.get());

        return list;
    }

    public static void infectBlock(Level level, BlockPos pos, Block block){

        if(block.equals(Blocks.BOOKSHELF)){
            level.setBlockAndUpdate(pos, RegisterBlocks.INFECTED_BOOKSHELF.get().defaultBlockState());
        }
        else if(block.equals(RegisterBlocks.ENCHANTED_BOOKSHELF.get())){
            level.setBlockAndUpdate(pos, RegisterBlocks.INFECTED_ENCHANTED_BOOKSHELF.get().defaultBlockState());
        }
        else if(block.equals(RegisterBlocks.ARCHIVERS_BOOKSHELF.get())){
            level.setBlockAndUpdate(pos, RegisterBlocks.INFECTED_ARCHIVERS_BOOKSHELF.get().defaultBlockState());
        }
        else if(block.equals(RegisterBlocks.CARTOGRAPHERS_BOOKSHELF.get())){
            level.setBlockAndUpdate(pos, RegisterBlocks.INFECTED_CARTOGRAPHERS_BOOKSHELF.get().defaultBlockState());
        }
    }

}
