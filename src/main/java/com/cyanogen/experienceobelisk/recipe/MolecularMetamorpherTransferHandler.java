package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.gui.MolecularMetamorpherMenu;
import com.cyanogen.experienceobelisk.network.shared.UpdateInventory;
import com.cyanogen.experienceobelisk.registries.RegisterMenus;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
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

        if(doTransfer){
            return checkAndTransfer(menu, recipe, player, recipeSlots, maxTransfer);
        }
        else{
            ItemStack[] playerItems = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
            int[] playerItemCount = {0,0,0};
            int[] requiredCount = {0,0,0};

            return checkOnly(playerItems, playerItemCount, requiredCount, recipe, player, recipeSlots);
        }
    }

    public void check(ItemStack[] playerItems, int[] playerItemCount, int[] requiredCount, MolecularMetamorpherRecipe recipe, Player player){

        //fills each of the passed in arrays with information
        //playerItems -- the items in the player's inventory (if any) which are valid ingredients for each recipe slot
        //playerItemCount -- the count of each item
        //requiredCount -- the count required by the recipe for each ingredient

        for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : recipe.getIngredientMap().entrySet()){

            Ingredient ingredient = entry.getKey();
            int position = entry.getValue().getA() - 1;
            int count = entry.getValue().getB();
            requiredCount[position] = count;

            for(ItemStack ingredientStack : ingredient.getItems()){

                playerItemCount[position] = 0;

                for(int i = 0; i < player.getInventory().getContainerSize(); i++){
                    ItemStack playerStack = player.getInventory().getItem(i);

                    if(ItemStack.isSameItemSameTags(playerStack, ingredientStack)){

                        playerItems[position] = playerStack.copy();
                        playerItemCount[position] += playerStack.getCount();
                    }

                    if(playerItemCount[position] >= count){
                        break;
                    }
                }

                if(playerItemCount[position] >= count){
                    break;
                }
            }
        }

    }

    public IRecipeTransferError checkOnly(ItemStack[] playerItems, int[] playerItemCount, int[] requiredCount,
                                          MolecularMetamorpherRecipe menu, Player player, IRecipeSlotsView recipeSlots){

        check(playerItems, playerItemCount, requiredCount, menu, player);

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

            Component component = Component.literal("Missing Items").withStyle(ChatFormatting.RED);
            return helper.createUserErrorForMissingSlots(component, slotsList);
        }

    }

    public IRecipeTransferError checkAndTransfer(MolecularMetamorpherMenu menu, MolecularMetamorpherRecipe recipe,
                                                     Player player, IRecipeSlotsView recipeSlots, boolean maxTransfer){

        ItemStack[] playerItems = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
        int[] playerItemCount = {0,0,0};
        int[] requiredCount = {0,0,0};
        int[] countToTransfer;

        IRecipeTransferError checkOnly = checkOnly(playerItems, playerItemCount, requiredCount, recipe, player, recipeSlots);

        if(maxTransfer){
            countToTransfer = playerItemCount;
        }
        else{
            countToTransfer = requiredCount;
        }

        if(checkOnly == null){

            System.out.println("--------- [1] check returned all items present in sufficient quantities");

            for(int i = 0; i < 3; i++){

                ItemStack ingredientStack = playerItems[i];

                for(int k = 0; k < player.getInventory().getContainerSize(); k++){

                    ItemStack playerStack = player.getInventory().getItem(k);
                    ItemStack containerStack = menu.getSlot(i).getItem();

                    if(ItemStack.isSameItemSameTags(playerStack, ingredientStack)){

                        int transfer = Math.min(playerStack.getCount(), countToTransfer[i]);

                        System.out.println("--------- [2] found item for ingredient " + i + ": " + playerStack.getItem());

                        if(ItemStack.isSameItemSameTags(containerStack, ingredientStack)){

                            transfer = Math.min(transfer, countToTransfer[i] - containerStack.getCount());
                            containerStack.grow(transfer);

                            System.out.println("--------- [3a] transferred " + transfer + " items to the container");
                        }
                        else{
                            if(!containerStack.isEmpty() && !player.addItem(containerStack)){
                                Component component = Component.literal("Inventory Full").withStyle(ChatFormatting.RED);
                                return helper.createUserErrorWithTooltip(component);
                            }

                            ItemStack s = playerStack.copyWithCount(transfer);
                            menu.getSlot(i).set(s);

                            System.out.println("--------- [3b] transferred " + transfer + " items to slot " + i);
                        }

                        playerStack.shrink(transfer);

                        countToTransfer[i] -= transfer;
                    }

                    if(countToTransfer[i] <= 0){
                        break;
                    }
                }
            }

            UpdateInventory.updateInventoryFromClient(player);
        }
        else{
            return checkOnly;
        }

        System.out.println("--------- [4] transfer concluded");

        return null;
    }

}
