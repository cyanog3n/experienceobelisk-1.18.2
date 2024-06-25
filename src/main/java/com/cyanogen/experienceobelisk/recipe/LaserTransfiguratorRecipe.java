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

import java.util.ArrayList;

public class LaserTransfiguratorRecipe implements Recipe<SimpleContainer> {

    private final NonNullList<Ingredient> ingredients;
    private final ItemStack output;
    private final int cost;
    private final int processTime;
    private final ResourceLocation id;


    public LaserTransfiguratorRecipe(NonNullList<Ingredient> ingredients, ItemStack output, int cost, int processTime, ResourceLocation id){
        this.ingredients = ingredients;
        this.output = output;
        this.cost = cost;
        this.processTime = processTime;
        this.id = id;
    }

    //each input slot = one ingredient
    //each ingredient can only be represented by one item (eg. gold ingot) or multiple valid items (eg. all kinds of saplings)
    //so ingredients are functionally arrays

    @Override
    public boolean matches(SimpleContainer container, Level level) {

        ArrayList<ItemStack> contents = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            contents.add(i, container.getItem(i));
        }

        boolean slot1 = ingredients.get(0).test(contents.get(0));
        boolean slot2 = ingredients.get(1).test(contents.get(1));
        boolean slot3 = ingredients.get(2).test(contents.get(2));

        System.out.println(slot1 + " " + slot2 + " " + slot3);
        return slot1 && slot2 && slot3;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@Nullable RegistryAccess p_267052_) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public int getCost(){
        return cost;
    }

    public int getProcessTime(){
        return processTime;
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

    //-----SERIALIZER-----//

    public static class Serializer implements RecipeSerializer<LaserTransfiguratorRecipe> {

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ExperienceObelisk.MOD_ID, "laser_transfiguration");

        @Override
        public LaserTransfiguratorRecipe fromJson(ResourceLocation id, JsonObject recipe) {

            NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);
            JsonArray ingredients = GsonHelper.getAsJsonArray(recipe, "ingredients");
            ItemStack stackItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(recipe, "ingredient3"));

            //i need the ingredients in an array of type itemstack, with the counts properly represented

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(recipe, "result"));

            int cost = GsonHelper.getAsInt(recipe, "cost");
            int processTime = GsonHelper.getAsInt(recipe, "processTime");

            for(int i = 0; i < inputs.size(); i++){
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new LaserTransfiguratorRecipe(inputs, result, cost, processTime, id);
        }

        @Override
        public @Nullable LaserTransfiguratorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

            NonNullList<Ingredient> inputs = NonNullList.withSize(buffer.readInt(), Ingredient.EMPTY);
            for(int i = 0; i < inputs.size(); i++){
                inputs.set(i, Ingredient.fromNetwork(buffer));
            }
            ItemStack result = buffer.readItem();

            int cost = buffer.readInt();
            int processTime = buffer.readInt();

            return new LaserTransfiguratorRecipe(inputs, result, cost, processTime, id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LaserTransfiguratorRecipe recipe) {

            buffer.writeInt(recipe.ingredients.size());
            for(Ingredient ingredient : recipe.getIngredients()){
                ingredient.toNetwork(buffer);
            }
            buffer.writeItemStack(recipe.getResultItem(null), false);

            buffer.writeInt(recipe.cost);
            buffer.writeInt(recipe.processTime);
        }
    }


}
