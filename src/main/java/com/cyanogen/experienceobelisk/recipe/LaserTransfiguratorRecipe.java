package com.cyanogen.experienceobelisk.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class LaserTransfiguratorRecipe implements Recipe<SimpleContainer> {

    private final NonNullList<Ingredient> input;
    private final ItemStack output;
    private final int cost;
    private final ResourceLocation id;


    public LaserTransfiguratorRecipe(NonNullList<Ingredient> input, ItemStack output, int cost, ResourceLocation id){
        this.input = input;
        this.output = output;
        this.cost = cost;
        this.id = id;
    }


    @Override
    public boolean matches(SimpleContainer container, Level level) {

        ItemStack stack1 = container.getItem(0);
        ItemStack stack2 = container.getItem(1);
        ItemStack stack3 = container.getItem(2);

        int[][] permutations = {
                {0, 1, 2},
                {0, 2, 1},
                {1, 0, 2},
                {1, 2, 0},
                {2, 0, 1},
                {2, 1, 0}
        };

        if(!level.isClientSide){
            for (int[] perm : permutations) {
                if(input.get(perm[0]).test(stack1) && input.get(perm[1]).test(stack2) && input.get(perm[2]).test(stack3)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
