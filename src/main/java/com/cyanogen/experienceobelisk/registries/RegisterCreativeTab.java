package com.cyanogen.experienceobelisk.registries;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class RegisterCreativeTab {
    
    public static final CreativeModeTab MOD_TAB = new CreativeModeTab("experienceobelisk"){
        
        @Override
        public ItemStack makeIcon() {
            return RegisterItems.EXPERIENCE_OBELISK_ITEM.get().getDefaultInstance();
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> itemList) {

            //BASIC INGREDIENTS
            itemList.add(RegisterItems.COGNITIVE_FLUX.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_AMALGAM.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_ALLOY.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_CRYSTAL.get().getDefaultInstance());
            itemList.add(RegisterItems.ASTUTE_ASSEMBLY.get().getDefaultInstance());
            itemList.add(RegisterItems.PRIMORDIAL_ASSEMBLY.get().getDefaultInstance());

            //TOOLSETS
            itemList.add(RegisterItems.COGNITIVE_SWORD.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_SHOVEL.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_PICKAXE.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_AXE.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_HOE.get().getDefaultInstance());

            //FUNCTIONAL ITEMS
            itemList.add(RegisterItems.ATTUNEMENT_STAFF.get().getDefaultInstance());
            itemList.add(RegisterItems.ENLIGHTENED_AMULET.get().getDefaultInstance());
            itemList.add(RegisterItems.BIBLIOPHAGE.get().getDefaultInstance());

            //FUNCTIONAL BLOCKS
            itemList.add(RegisterItems.EXPERIENCE_OBELISK_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.EXPERIENCE_FOUNTAIN_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.PRECISION_DISPELLER_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.MOLECULAR_METAMORPHER_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.ACCELERATOR_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.LINEAR_ACCELERATOR_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.ENCHANTED_BOOKSHELF_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.ARCHIVERS_BOOKSHELF_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.INFECTED_BOOKSHELF_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.INFECTED_ENCHANTED_BOOKSHELF_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.INFECTED_ARCHIVERS_BOOKSHELF_ITEM.get().getDefaultInstance());

            //DECORATIVE / OTHER BLOCKS
            itemList.add(RegisterItems.COGNITIVE_ALLOY_BLOCK_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_CRYSTAL_BLOCK_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.WHISPERGLASS_ITEM.get().getDefaultInstance());

            //MISC
            itemList.add(RegisterItems.FORGOTTEN_DUST.get().getDefaultInstance());
            itemList.add(RegisterItems.FORGOTTEN_DUST_BLOCK_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.NIGHTMARE_BOTTLE.get().getDefaultInstance());
            itemList.add(RegisterItems.DAYDREAM_BOTTLE.get().getDefaultInstance());
            itemList.add(RegisterItems.EXPERIENCE_JELLY.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIUM_BUCKET.get().getDefaultInstance());
            
          
        }
    };
}
