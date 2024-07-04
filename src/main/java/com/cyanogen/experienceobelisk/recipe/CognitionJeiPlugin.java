package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.gui.LaserTransfiguratorMenu;
import com.cyanogen.experienceobelisk.gui.LaserTransfiguratorScreen;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

@mezz.jei.api.JeiPlugin
public class CognitionJeiPlugin implements IModPlugin {

    public final RecipeType<LaserTransfiguratorRecipe> transfiguratorType =
            RecipeType.create(LaserTransfiguratorRecipe.Type.ID, ExperienceObelisk.MOD_ID, LaserTransfiguratorRecipe.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

        registration.addRecipeCategories(new LaserTransfigurationCategory(registration));
        IModPlugin.super.registerCategories(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        //RECIPES
        List<LaserTransfiguratorRecipe> transfiguratorRecipes = new ArrayList<>();

        for(Recipe<?> recipe : Minecraft.getInstance().level.getRecipeManager().getRecipes()){
            if(recipe instanceof LaserTransfiguratorRecipe transfiguratorRecipe){
                transfiguratorRecipes.add(transfiguratorRecipe);
            }
        }

        registration.addRecipes(transfiguratorType, transfiguratorRecipes);

        //INFO
        ItemStack FORGOTTEN_DUST = new ItemStack(RegisterItems.FORGOTTEN_DUST.get());
        registration.addIngredientInfo(FORGOTTEN_DUST, VanillaTypes.ITEM_STACK, Component.literal("jei.experienceobelisk.description.forgotten_dust"));

        IModPlugin.super.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        ItemStack stack = new ItemStack(RegisterItems.LASER_TRANSFIGURATOR_ITEM.get());
        registration.addRecipeCatalyst(stack, transfiguratorType);

        IModPlugin.super.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {

        registration.addRecipeTransferHandler(LaserTransfiguratorMenu.class, null, transfiguratorType,
                0, 3, 4, 36);
        IModPlugin.super.registerRecipeTransferHandlers(registration);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(LaserTransfiguratorScreen.class,107,45,32,10, transfiguratorType);
        IModPlugin.super.registerGuiHandlers(registration);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExperienceObelisk.MOD_ID, "jei_plugin");
    }
}
