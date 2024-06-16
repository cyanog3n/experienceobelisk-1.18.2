package com.cyanogen.experienceobelisk.registries;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegisterCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExperienceObelisk.MOD_ID);

    public static final RegistryObject<CreativeModeTab> EXPERIENCE_OBELISK_TAB = TABS.register("experienceobelisk_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.experienceobelisk"))
                    .icon(RegisterItems.EXPERIENCE_OBELISK_ITEM.get()::getDefaultInstance)
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .displayItems((p_270258_, output) -> {

                        //BASIC INGREDIENTS
                        output.accept(RegisterItems.COGNITIVE_FLUX.get());
                        output.accept(RegisterItems.COGNITIVE_AMALGAM.get());
                        output.accept(RegisterItems.COGNITIVE_ALLOY.get());
                        output.accept(RegisterItems.COGNITIVE_CRYSTAL.get());
                        output.accept(RegisterItems.ASTUTE_ASSEMBLY.get());
                        output.accept(RegisterItems.PRIMORDIAL_ASSEMBLY.get());

                        //TOOLSETS
                        output.accept(RegisterItems.COGNITIVE_SWORD.get());
                        output.accept(RegisterItems.COGNITIVE_SHOVEL.get());
                        output.accept(RegisterItems.COGNITIVE_PICKAXE.get());
                        output.accept(RegisterItems.COGNITIVE_AXE.get());
                        output.accept(RegisterItems.COGNITIVE_HOE.get());

                        //FUNCTIONAL ITEMS
                        output.accept(RegisterItems.ATTUNEMENT_STAFF.get());
                        output.accept(RegisterItems.ENLIGHTENED_AMULET.get());
                        output.accept(RegisterItems.BIBLIOPHAGE.get());

                        //FUNCTIONAL BLOCKS
                        output.accept(RegisterItems.EXPERIENCE_OBELISK_ITEM.get());
                        output.accept(RegisterItems.EXPERIENCE_FOUNTAIN_ITEM.get());
                        output.accept(RegisterItems.PRECISION_DISPELLER_ITEM.get());
                        output.accept(RegisterItems.ACCELERATOR_ITEM.get());
                        output.accept(RegisterItems.LINEAR_ACCELERATOR_ITEM.get());
                        output.accept(RegisterItems.ENCHANTED_BOOKSHELF_ITEM.get());
                        output.accept(RegisterItems.ARCHIVERS_BOOKSHELF_ITEM.get());
                        output.accept(RegisterItems.INFECTED_BOOKSHELF_ITEM.get());
                        output.accept(RegisterItems.INFECTED_ENCHANTED_BOOKSHELF_ITEM.get());
                        output.accept(RegisterItems.INFECTED_ARCHIVERS_BOOKSHELF_ITEM.get());

                        //DECORATIVE / OTHER BLOCKS
                        output.accept(RegisterItems.COGNITIVE_ALLOY_BLOCK_ITEM.get());
                        output.accept(RegisterItems.COGNITIVE_CRYSTAL_BLOCK_ITEM.get());
                        output.accept(RegisterItems.WHISPERGLASS_ITEM.get());

                        //MISC
                        output.accept(RegisterItems.FORGOTTEN_DUST.get());
                        output.accept(RegisterItems.FORGOTTEN_DUST_BLOCK_ITEM.get());
                        output.accept(RegisterItems.NIGHTMARE_BOTTLE.get());
                        output.accept(RegisterItems.DAYDREAM_BOTTLE.get());
                        output.accept(RegisterItems.COGNITIUM_BUCKET.get());
                    })
                    .build());

    public static void register(IEventBus eventBus){
        TABS.register(eventBus);
    }
}
