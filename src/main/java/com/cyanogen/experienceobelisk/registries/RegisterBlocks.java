package com.cyanogen.experienceobelisk.registries;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.block.*;
import com.cyanogen.experienceobelisk.block.bookworm.VermiferousArchiversBookshelfBlock;
import com.cyanogen.experienceobelisk.block.bookworm.VermiferousBookshelfBlock;
import com.cyanogen.experienceobelisk.block.bookworm.VermiferousCartographersBookshelfBlock;
import com.cyanogen.experienceobelisk.block.bookworm.VermiferousEnchantedBookshelfBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class RegisterBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExperienceObelisk.MOD_ID);

    //-----FUNCTIONAL BLOCKS-----//

    public static final RegistryObject<Block> EXPERIENCE_OBELISK = BLOCKS.register("experience_obelisk", ExperienceObeliskBlock::new);
    public static final RegistryObject<Block> EXPERIENCE_FOUNTAIN = BLOCKS.register("experience_fountain", ExperienceFountainBlock::new);
    public static final RegistryObject<Block> PRECISION_DISPELLER = BLOCKS.register("precision_dispeller", PrecisionDispellerBlock::new);
    public static final RegistryObject<Block> ACCELERATOR = BLOCKS.register("accelerator", AcceleratorBlock::new);
    public static final RegistryObject<Block> LINEAR_ACCELERATOR = BLOCKS.register("linear_accelerator", LinearAcceleratorBlock::new);
    public static final RegistryObject<Block> VERMIFEROUS_BOOKSHELF = BLOCKS.register("vermiferous_bookshelf", VermiferousBookshelfBlock::new);
    public static final RegistryObject<Block> VERMIFEROUS_ENCHANTED_BOOKSHELF = BLOCKS.register("vermiferous_enchanted_bookshelf", VermiferousEnchantedBookshelfBlock::new);
    public static final RegistryObject<Block> VERMIFEROUS_ARCHIVERS_BOOKSHELF = BLOCKS.register("vermiferous_archivers_bookshelf", VermiferousArchiversBookshelfBlock::new);
    public static final RegistryObject<Block> VERMIFEROUS_CARTOGRAPHERS_BOOKSHELF = BLOCKS.register("vermiferous_cartographers_bookshelf", VermiferousCartographersBookshelfBlock::new);

    //-----DECORATIVE / CRAFTING-----//

    public static final RegistryObject<Block> COGNITIVE_ALLOY_BLOCK = BLOCKS.register("cognitive_alloy_block", CognitiveAlloyBlock::new);
    public static final RegistryObject<Block> COGNITIVE_CRYSTAL_BLOCK = BLOCKS.register("cognitive_crystal_block", CognitiveCrystalBlock::new);
    public static final RegistryObject<Block> WHISPERGLASS_BLOCK = BLOCKS.register("whisperglass", WhisperglassBlock::new);
    public static final RegistryObject<Block> IGNORAMUS_DUST_BLOCK = BLOCKS.register("ignoramus_dust_block", IgnoramusDustBlock::new);
    public static final RegistryObject<Block> ENCHANTED_BOOKSHELF = BLOCKS.register("enchanted_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> ARCHIVERS_BOOKSHELF = BLOCKS.register("archivers_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));
    public static final RegistryObject<Block> CARTOGRAPHERS_BOOKSHELF = BLOCKS.register("cartographers_bookshelf", () -> new Block(BlockBehaviour.Properties.copy(Blocks.BOOKSHELF)));


    //-----FLUID BLOCKS-----//

    public static final RegistryObject<LiquidBlock> COGNITIUM = BLOCKS.register("cognitium",
            () -> new LiquidBlock(RegisterFluids.COGNITIUM_FLOWING, BlockBehaviour.Properties.copy(Blocks.WATER)
                    .liquid()
                    .lightLevel(value -> 10)
                    .emissiveRendering((p_61036_, p_61037_, p_61038_) -> true)
            ));


    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

}
