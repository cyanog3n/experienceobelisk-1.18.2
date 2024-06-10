package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.registries.RegisterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
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

        if(getValidBlocksForInfection().contains(block)){
            infectBlock(level, pos, block);
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
            level.setBlockAndUpdate(pos, RegisterBlocks.VERMIFEROUS_BOOKSHELF.get().defaultBlockState());
        }
        else if(block.equals(RegisterBlocks.ENCHANTED_BOOKSHELF.get())){
            level.setBlockAndUpdate(pos, RegisterBlocks.VERMIFEROUS_ENCHANTED_BOOKSHELF.get().defaultBlockState());
        }
        else if(block.equals(RegisterBlocks.ARCHIVERS_BOOKSHELF.get())){
            level.setBlockAndUpdate(pos, RegisterBlocks.VERMIFEROUS_ARCHIVERS_BOOKSHELF.get().defaultBlockState());
        }
        else if(block.equals(RegisterBlocks.CARTOGRAPHERS_BOOKSHELF.get())){
            level.setBlockAndUpdate(pos, RegisterBlocks.VERMIFEROUS_CARTOGRAPHERS_BOOKSHELF.get().defaultBlockState());
        }
    }

}
