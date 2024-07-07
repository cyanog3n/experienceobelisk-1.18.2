package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.gui.MolecularMetamorpherMenu;
import com.cyanogen.experienceobelisk.gui.MolecularMetamorpherScreen;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import com.cyanogen.experienceobelisk.registries.RegisterMenus;
import com.google.common.collect.ImmutableMap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@mezz.jei.api.JeiPlugin
public class CognitionJeiPlugin implements IModPlugin {

    public static final RecipeType<MolecularMetamorpherRecipe> metamorpherType =
            RecipeType.create(MolecularMetamorpherRecipe.Type.ID, ExperienceObelisk.MOD_ID, MolecularMetamorpherRecipe.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

        registration.addRecipeCategories(new MolecularMetamorpherCategory(registration));
        IModPlugin.super.registerCategories(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        //RECIPES
        List<MolecularMetamorpherRecipe> metamorpherRecipes = new ArrayList<>();

        for(Recipe<?> recipe : Minecraft.getInstance().level.getRecipeManager().getRecipes()){
            if(recipe instanceof MolecularMetamorpherRecipe metamorpherRecipe){
                metamorpherRecipes.add(metamorpherRecipe);
            }
        }

        registration.addRecipes(metamorpherType, metamorpherRecipes);

        //INFO
        ItemStack FORGOTTEN_DUST = new ItemStack(RegisterItems.FORGOTTEN_DUST.get());
        ItemStack EXPERIENCE_BOTTLE = new ItemStack(Items.EXPERIENCE_BOTTLE);

        registration.addIngredientInfo(FORGOTTEN_DUST, VanillaTypes.ITEM_STACK, Component.translatable("jei.experienceobelisk.description.forgotten_dust"));
        registration.addIngredientInfo(EXPERIENCE_BOTTLE, VanillaTypes.ITEM_STACK, Component.translatable("jei.experienceobelisk.description.experience_bottle"));

        IModPlugin.super.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        ItemStack stack = new ItemStack(RegisterItems.MOLECULAR_METAMORPHER_ITEM.get());
        registration.addRecipeCatalyst(stack, metamorpherType);

        IModPlugin.super.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        //registration.addRecipeTransferHandler(new MolecularMetamorpherTransferHandler(registration), metamorpherType);

        registration.addRecipeTransferHandler(MolecularMetamorpherMenu.class, null, metamorpherType, 0, 3, 4, 36);

        IModPlugin.super.registerRecipeTransferHandlers(registration);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MolecularMetamorpherScreen.class,107,45,32,10, metamorpherType);
        IModPlugin.super.registerGuiHandlers(registration);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExperienceObelisk.MOD_ID, "jei_plugin");
    }

}
