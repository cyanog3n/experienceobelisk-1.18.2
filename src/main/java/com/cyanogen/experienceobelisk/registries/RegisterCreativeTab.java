package com.cyanogen.experienceobelisk.registries;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class RegisterCreativeTab {
    public static final CreativeModeTab MOD_TAB = new CreativeModeTab("experienceobelisk") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegisterBlocks.EXPERIENCE_OBELISK.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> itemList) {

            itemList.add(RegisterItems.COGNITIVE_FLUX.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_AMALGAM.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_ALLOY.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_CRYSTAL.get().getDefaultInstance());
            itemList.add(RegisterItems.ASTUTE_ASSEMBLY.get().getDefaultInstance());

            itemList.add(RegisterItems.COGNITIVE_SWORD.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_SHOVEL.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_PICKAXE.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_AXE.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_HOE.get().getDefaultInstance());

            itemList.add(RegisterItems.ATTUNEMENT_STAFF.get().getDefaultInstance());
            itemList.add(RegisterItems.ENLIGHTENED_AMULET.get().getDefaultInstance());

            itemList.add(RegisterItems.EXPERIENCE_OBELISK_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.EXPERIENCE_FOUNTAIN_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.PRECISION_DISPELLER_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.ACCELERATOR_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.LINEAR_ACCELERATOR_ITEM.get().getDefaultInstance());

            itemList.add(RegisterItems.COGNITIVE_ALLOY_BLOCK_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.COGNITIVE_CRYSTAL_BLOCK_ITEM.get().getDefaultInstance());
            itemList.add(RegisterItems.WHISPERGLASS_ITEM.get().getDefaultInstance());

            itemList.add(RegisterItems.COGNITIUM_BUCKET.get().getDefaultInstance());
        }
    };
}
