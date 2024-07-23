package com.cyanogen.experienceobelisk.recipe.jei;

import com.cyanogen.experienceobelisk.gui.MolecularMetamorpherMenu;
import com.cyanogen.experienceobelisk.network.shared.UpdateInventory;
import com.cyanogen.experienceobelisk.recipe.MolecularMetamorpherRecipe;
import com.cyanogen.experienceobelisk.registries.RegisterMenus;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public @Nullable IRecipeTransferError transferRecipe(MolecularMetamorpherMenu menu, MolecularMetamorpherRecipe recipe,
                                                         IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {

        return checkAndTransfer(menu, recipe, recipeSlots, player, maxTransfer, doTransfer);
    }

    public IRecipeTransferError checkAndTransfer(MolecularMetamorpherMenu menu, MolecularMetamorpherRecipe recipe,
                                                 IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer){

        ItemStack[] playerItems = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
        int[] playerItemCount = {0,0,0};
        int[] requiredCount = {0,0,0};
        int[] countToTransfer;

        IRecipeTransferError error = checkOnly(playerItems, playerItemCount, requiredCount, menu, recipe, player, recipeSlots);

        if(maxTransfer){
            countToTransfer = new int[]{Math.min(playerItemCount[0], playerItems[0].getMaxStackSize()),
                                        Math.min(playerItemCount[1], playerItems[1].getMaxStackSize()),
                                        Math.min(playerItemCount[2], playerItems[2].getMaxStackSize())};
        }
        else{
            countToTransfer = requiredCount;
        }

        if(error == null && doTransfer){
            return transferOnly(playerItems, countToTransfer, menu, player);
        }
        else{
            return error;
        }
    }

    public IRecipeTransferError checkOnly(ItemStack[] playerItems, int[] playerItemCount, int[] requiredCount, MolecularMetamorpherMenu menu,
                                          MolecularMetamorpherRecipe recipe, Player player, IRecipeSlotsView recipeSlots){

        //check if player inventory has space to move menu items into
        int[] spaces = {-1,-1,-1};
        for(int i = 0; i < 3; i++){

            ItemStack menuStack = menu.getSlot(i).getItem();

            if(menuStack.isEmpty()){
                spaces[i] = 999;
            }
            else{
                for(int k = 0; k < player.getInventory().items.size(); k++){

                    ItemStack playerStack = player.getInventory().getItem(k);

                    if(!(k == spaces[0] || k == spaces[1] || k == spaces[2])){
                        if(ItemStack.isSameItemSameTags(menuStack, playerStack) && menuStack.getCount() + playerStack.getCount() <= menuStack.getMaxStackSize()){
                            spaces[i] = k;
                        }
                        else if(playerStack.isEmpty()){
                            spaces[i] = k;
                        }
                    }
                }
            }
        }

        if(spaces[0] == -1 || spaces[1] == -1 || spaces[2] == -1){
            return helper.createUserErrorWithTooltip(Component.translatable("jei.experienceobelisk.error.inventory_full"));
        }

        //now check if player has enough items
        getItemInfo(playerItems, playerItemCount, requiredCount, menu, recipe, player);

        if(playerItemCount[0] >= requiredCount[0] && playerItemCount[1] >= requiredCount[1] && playerItemCount[2] >= requiredCount[2]){
            return null;
        }
        else{
            List<IRecipeSlotView> slotsList = new ArrayList<>();
            for(int i = 0; i < 3; i++){

                Optional<IRecipeSlotView> slot = recipeSlots.findSlotByName("input"+i);

                if(playerItemCount[i] < requiredCount[i] && slot.isPresent()){
                    slotsList.add(slot.get());
                }
            }

            Component component = Component.translatable("jei.experienceobelisk.error.missing_items");
            return helper.createUserErrorForMissingSlots(component, slotsList);
        }

    }

    public IRecipeTransferError transferOnly(ItemStack[] playerItems, int[] countToTransfer, MolecularMetamorpherMenu menu, Player player){

        //clear out menu
        boolean uncleared = false;
        for(int i = 0; i < 3; i++){

            ItemStack stack = menu.getSlot(i).getItem();
            if(!stack.isEmpty() && !(playerItems[i].isEmpty() || countToTransfer[i] <= 0)){
                if(!player.getInventory().add(stack)){
                    uncleared = true;
                    break;
                }
            }
        }

        if(uncleared){
            System.out.println("[Cognition JEI Plugin] Player inventory unexpectedly full");
            return helper.createInternalError();
        }

        //transfer items into the menu
        for(int i = 0; i < 3; i++){

            ItemStack ingredientStack = playerItems[i];

            if(!(ingredientStack.isEmpty() || ingredientStack.is(Items.AIR) || countToTransfer[i] <= 0)){

                for(int k = 0; k < player.getInventory().items.size(); k++){

                    ItemStack playerStack = player.getInventory().getItem(k);

                    if(ItemStack.isSameItemSameTags(playerStack, ingredientStack)){

                        countToTransfer[i] -= menu.put(playerStack, countToTransfer[i]);
                    }

                    if(countToTransfer[i] <= 0){
                        break;
                    }
                }
            }
        }

        //update player inventory and container
        UpdateInventory.updateInventoryFromClient(player);
        return null;
    }

    public void getItemInfo(ItemStack[] playerItems, int[] playerItemCount, int[] requiredCount, MolecularMetamorpherMenu menu,
                            MolecularMetamorpherRecipe recipe, Player player){

        //fills each of the passed in arrays with information
        //playerItems -- the items in the player's inventory and the menu (if any) which are valid ingredients for each recipe slot
        //playerItemCount -- the total count of each item, across the inventory and menu
        //requiredCount -- the count required by the recipe for each ingredient
        //this is done rather than using recipe.match() as the extra information is useful for later

        for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : recipe.getIngredientMapNoFiller().entrySet()){

            Ingredient ingredient = entry.getKey();
            int position = entry.getValue().getA() - 1;
            int count = entry.getValue().getB();
            requiredCount[position] = count;

            for(ItemStack ingredientStack : ingredient.getItems()){

                playerItemCount[position] = 0;

                for(int i = 0; i < player.getInventory().items.size(); i++){
                    ItemStack playerStack = player.getInventory().getItem(i);

                    if(ItemStack.isSameItemSameTags(playerStack, ingredientStack)){

                        playerItems[position] = ingredientStack.copy();
                        playerItemCount[position] += playerStack.getCount();
                    }
                }

                for(int i = 0; i < 3; i++){
                    ItemStack menuStack = menu.getSlot(i).getItem();

                    if(ItemStack.isSameItemSameTags(menuStack, ingredientStack)){

                        playerItems[position] = ingredientStack.copy();
                        playerItemCount[position] += menuStack.getCount();
                    }
                }

                if(playerItemCount[position] >= count){
                    break;
                }
            }
        }

    }

}
