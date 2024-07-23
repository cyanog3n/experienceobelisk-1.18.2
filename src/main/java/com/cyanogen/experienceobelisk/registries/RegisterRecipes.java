package com.cyanogen.experienceobelisk.registries;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.recipe.MolecularMetamorpherRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterRecipes{

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExperienceObelisk.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ExperienceObelisk.MOD_ID);

    public static final RegistryObject<RecipeSerializer<MolecularMetamorpherRecipe>> MOLECULAR_METAMORPHER_SERIALIZER =
            SERIALIZERS.register("molecular_metamorphosis", () -> MolecularMetamorpherRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<MolecularMetamorpherRecipe>> MOLECULAR_METAMORPHER_TYPE =
            TYPES.register("molecular_metamorphosis", () -> MolecularMetamorpherRecipe.Type.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
