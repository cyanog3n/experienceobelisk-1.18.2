package com.cyanogen.experienceobelisk.registries;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.block_entities.*;
import com.cyanogen.experienceobelisk.block_entities.bibliophage.InfectedArchiversBookshelfEntity;
import com.cyanogen.experienceobelisk.block_entities.bibliophage.InfectedBookshelfEntity;
import com.cyanogen.experienceobelisk.block_entities.bibliophage.InfectedCartographersBookshelfEntity;
import com.cyanogen.experienceobelisk.block_entities.bibliophage.InfectedEnchantedBookshelfEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExperienceObelisk.MOD_ID);

    private static com.mojang.datafixers.types.Type<?> Type;

    public static final RegistryObject<BlockEntityType<ExperienceObeliskEntity>> EXPERIENCE_OBELISK_BE = BLOCK_ENTITIES.register("experienceobelisk_be",
            ()-> BlockEntityType.Builder.of(ExperienceObeliskEntity::new, RegisterBlocks.EXPERIENCE_OBELISK.get()).build(Type));

    public static final RegistryObject<BlockEntityType<ExperienceFountainEntity>> EXPERIENCE_FOUNTAIN_BE = BLOCK_ENTITIES.register("experiencefountain_be",
            ()-> BlockEntityType.Builder.of(ExperienceFountainEntity::new, RegisterBlocks.EXPERIENCE_FOUNTAIN.get()).build(Type));

    public static final RegistryObject<BlockEntityType<PrecisionDispellerEntity>> PRECISION_DISPELLER_BE = BLOCK_ENTITIES.register("precisiondispeller_be",
            ()-> BlockEntityType.Builder.of(PrecisionDispellerEntity::new, RegisterBlocks.PRECISION_DISPELLER.get()).build(Type));

    public static final RegistryObject<BlockEntityType<AcceleratorEntity>> ACCELERATOR_BE = BLOCK_ENTITIES.register("accelerator_be",
            ()-> BlockEntityType.Builder.of(AcceleratorEntity::new, RegisterBlocks.ACCELERATOR.get()).build(Type));

    public static final RegistryObject<BlockEntityType<LinearAcceleratorEntity>> LINEAR_ACCELERATOR_BE = BLOCK_ENTITIES.register("linearaccelerator_be",
            ()-> BlockEntityType.Builder.of(LinearAcceleratorEntity::new, RegisterBlocks.LINEAR_ACCELERATOR.get()).build(Type));

    public static final RegistryObject<BlockEntityType<InfectedBookshelfEntity>> INFECTED_BOOKSHELF_BE = BLOCK_ENTITIES.register("infectedbookshelf_be",
            ()-> BlockEntityType.Builder.of(InfectedBookshelfEntity::new, RegisterBlocks.INFECTED_BOOKSHELF.get()).build(Type));

    public static final RegistryObject<BlockEntityType<InfectedEnchantedBookshelfEntity>> INFECTED_ENCHANTED_BOOKSHELF_BE = BLOCK_ENTITIES.register("infectedenchantedbookshelf_be",
            ()-> BlockEntityType.Builder.of(InfectedEnchantedBookshelfEntity::new, RegisterBlocks.INFECTED_ENCHANTED_BOOKSHELF.get()).build(Type));

    public static final RegistryObject<BlockEntityType<InfectedArchiversBookshelfEntity>> INFECTED_ARCHIVERS_BOOKSHELF_BE = BLOCK_ENTITIES.register("infectedarchiversbookshelf_be",
            ()-> BlockEntityType.Builder.of(InfectedArchiversBookshelfEntity::new, RegisterBlocks.INFECTED_ARCHIVERS_BOOKSHELF.get()).build(Type));

    public static final RegistryObject<BlockEntityType<InfectedCartographersBookshelfEntity>> INFECTED_CARTOGRAPHERS_BOOKSHELF_BE = BLOCK_ENTITIES.register("infectedcartographersbookshelf_be",
            ()-> BlockEntityType.Builder.of(InfectedCartographersBookshelfEntity::new, RegisterBlocks.INFECTED_CARTOGRAPHERS_BOOKSHELF.get()).build(Type));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }

}
