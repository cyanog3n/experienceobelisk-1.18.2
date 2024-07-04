package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.gui.LaserTransfiguratorMenu;
import com.cyanogen.experienceobelisk.gui.LaserTransfiguratorScreen;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import com.cyanogen.experienceobelisk.registries.RegisterMenus;
import com.cyanogen.experienceobelisk.registries.RegisterRecipes;
import com.google.common.collect.ImmutableMap;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public IRecipeTransferHandler<LaserTransfiguratorMenu, LaserTransfiguratorRecipe> getCustomRecipeTransferHandler(){
        return new IRecipeTransferHandler<>() {
            @Override
            public Class<? extends LaserTransfiguratorMenu> getContainerClass() {
                return LaserTransfiguratorMenu.class;
            }

            @Override
            public Optional<MenuType<LaserTransfiguratorMenu>> getMenuType() {
                return Optional.of(RegisterMenus.LASER_TRANSFIGURATOR_MENU.get());
            }

            @Override
            public RecipeType<LaserTransfiguratorRecipe> getRecipeType() {
                return transfiguratorType;
            }

            @Override
            public @Nullable IRecipeTransferError transferRecipe(LaserTransfiguratorMenu container, LaserTransfiguratorRecipe recipe,
                                                                 IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {

                ImmutableMap<Ingredient, Tuple<Integer, Integer>> ingredientMap = recipe.getIngredientMap();
                boolean[] hasItemForSlot = {false, false, false};
                int[] trackCounts = {0, 0, 0};

                for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : ingredientMap.entrySet()){

                    Ingredient ingredient = entry.getKey();
                    int position = entry.getValue().getA() - 1;
                    int count = entry.getValue().getB();
                    trackCounts[position] = count;

                    for(int i = 0; i < player.inventoryMenu.getSize(); i++){
                        Slot slot = player.inventoryMenu.getSlot(i);
                        ItemStack stack = slot.getItem();

                        if(ingredient.test(stack)){
                            hasItemForSlot[position] = true;
                            trackCounts[position] = trackCounts[position] - stack.getCount();

                            if(doTransfer){

                            }

                            if(trackCounts[position] <= 0){
                                break;
                            }
                        }

                    }
                }

                boolean hasAllItems = hasItemForSlot[0] && hasItemForSlot[1] && hasItemForSlot[2];




                return null;
            }
        };
    }
}
