package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class LaserTransfiguratorRecipe implements Recipe<SimpleContainer> {

    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int cost;
    private final ResourceLocation id;


    public LaserTransfiguratorRecipe(NonNullList<Ingredient> input, ItemStack output, int cost, ResourceLocation id){
        this.inputs = input;
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
                if(inputs.get(perm[0]).test(stack1) && inputs.get(perm[1]).test(stack2) && inputs.get(perm[2]).test(stack3)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess accesss) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<LaserTransfiguratorRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "laser_transfiguration";
    }

    public static class Serializer implements RecipeSerializer<LaserTransfiguratorRecipe> {

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ExperienceObelisk.MOD_ID, "laser_transfiguration");

        @Override
        public LaserTransfiguratorRecipe fromJson(ResourceLocation id, JsonObject recipe) {

            NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);
            JsonArray ingredients = GsonHelper.getAsJsonArray(recipe, "ingredients");
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(recipe, "result"));

            for(int i = 0; i < inputs.size() -1; i++){ //none of the arrays are size 2??
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new LaserTransfiguratorRecipe(inputs, result, 0, id);
        }

        @Override
        public @Nullable LaserTransfiguratorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); i++){
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }

            ItemStack result = buffer.readItem();

            return new LaserTransfiguratorRecipe(inputs, result, 0, id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LaserTransfiguratorRecipe recipe) {

            buffer.writeInt(recipe.inputs.size());

            for(Ingredient ingredient : recipe.getIngredients()){
                ingredient.toNetwork(buffer);
            }

            buffer.writeItemStack(recipe.getResultItem(null), false);
        }
    }


}
