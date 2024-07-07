package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.gui.MolecularMetamorpherMenu;
import com.cyanogen.experienceobelisk.registries.RegisterMenus;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MolecularMetamorpherTransferHandler implements IRecipeTransferHandler<MolecularMetamorpherMenu, MolecularMetamorpherRecipe> {

    public final IRecipeTransferHandlerHelper helper;

    public MolecularMetamorpherTransferHandler(IRecipeTransferRegistration registration){
        this.helper = registration.getTransferHelper();
    }

    @Override
    public Class<? extends MolecularMetamorpherMenu> getContainerClass() {
        return MolecularMetamorpherMenu.class;
    }

    @Override
    public Optional<MenuType<MolecularMetamorpherMenu>> getMenuType() {
        return Optional.of(RegisterMenus.MOLECULAR_METAMORPHER_MENU.get());
    }

    @Override
    public RecipeType<MolecularMetamorpherRecipe> getRecipeType() {
        return CognitionJeiPlugin.metamorpherType;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(MolecularMetamorpherMenu container, MolecularMetamorpherRecipe recipe,
                                                         IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {

        if(doTransfer){
            if(maxTransfer){
                return checkAndTransferMax();
            }
            else{
                return checkAndTransferOnce(container, recipe, player);
            }
        }
        else{
            return checkOnly(recipe, player, recipeSlots);
        }
    }

    public IRecipeTransferError checkOnly(MolecularMetamorpherRecipe recipe, Player player, IRecipeSlotsView recipeSlots){

        int[] ingredientCount = {0,0,0};
        int[] requiredCount = {0,0,0};

        for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : recipe.getIngredientMap().entrySet()){

            Ingredient ingredient = entry.getKey();
            int position = entry.getValue().getA() - 1;
            int count = entry.getValue().getB();
            requiredCount[position] = count;

            for(ItemStack ingredientStack : ingredient.getItems()){

                for(Slot slot : player.inventoryMenu.slots){
                    ItemStack playerStack = slot.getItem();

                    if(ItemStack.isSameItemSameTags(playerStack, ingredientStack)){

                        ingredientCount[position] += playerStack.getCount();
                    }

                    if(ingredientCount[position] >= count){
                        break;
                    }
                }

                if(ingredientCount[position] >= count){
                    break;
                }
            }
        }

        if(ingredientCount[0] >= requiredCount[0] && ingredientCount[1] >= requiredCount[1] && ingredientCount[2] >= requiredCount[2]){
            return null;
        }
        else{
            List<IRecipeSlotView> slotsList = new ArrayList<>();
            for(int i = 0; i < 3; i++){

                Optional<IRecipeSlotView> slot = recipeSlots.findSlotByName("input"+i);

                if(ingredientCount[i] < requiredCount[i] && slot.isPresent()){
                    slotsList.add(slot.get());
                }
            }

            Component component = Component.literal("Missing Items").withStyle(ChatFormatting.RED);
            return helper.createUserErrorForMissingSlots(component, slotsList);
        }

    }

    public IRecipeTransferError checkAndTransferOnce(MolecularMetamorpherMenu container, MolecularMetamorpherRecipe recipe, Player player){

        return null;
    }

    public IRecipeTransferError checkAndTransferMax(){
        return null;
    }

}
