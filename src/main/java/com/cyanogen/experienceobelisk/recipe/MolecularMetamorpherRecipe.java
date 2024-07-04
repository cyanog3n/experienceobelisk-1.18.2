package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Tuple;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MolecularMetamorpherRecipe implements Recipe<SimpleContainer> {

    private final ImmutableMap<Ingredient, Tuple<Integer, Integer>> ingredients; //A -- ingredient no., B -- count
    private final ItemStack output;
    private final int cost;
    private final int processTime;
    private final ResourceLocation id;

    public MolecularMetamorpherRecipe(ImmutableMap<Ingredient, Tuple<Integer, Integer>> ingredients, ItemStack output, int cost, int processTime, ResourceLocation id){
        this.ingredients = ingredients;
        this.output = output;
        this.cost = cost;
        this.processTime = processTime;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {

        ArrayList<ItemStack> contents = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            contents.add(i, container.getItem(i));
        }

        Map<Ingredient, Tuple<Integer, Integer>> ingredientMap = getIngredientMapNoFiller();
        ArrayList<Ingredient> ingredientSet = new ArrayList<>(ingredientMap.keySet());

        if(!ingredientMap.isEmpty()){
            for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : ingredientMap.entrySet()){

                Ingredient ingredient = entry.getKey();
                int count = entry.getValue().getB();

                for(ItemStack stack : contents){
                    if(ingredient.test(stack) && stack.getCount() >= count){
                        ingredientSet.remove(entry.getKey());
                        break;
                    }
                }
            }
        }
        else{
            return false;
        }

        return ingredientSet.isEmpty();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(this.ingredients.keySet());

        return list;
    }


    public ImmutableMap<Ingredient, Tuple<Integer, Integer>> getIngredientMap(){
        return this.ingredients;
    }

    public Map<Ingredient, Tuple<Integer, Integer>> getIngredientMapNoFiller(){
        Map<Ingredient, Tuple<Integer, Integer>> ingredients = new HashMap<>();

        for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : getIngredientMap().entrySet()){
            if(entry.getValue().getB() != -99){
                ingredients.put(entry.getKey(), entry.getValue());
            }
        }
        return ingredients;
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

    public static class Type implements RecipeType<MolecularMetamorpherRecipe>{
        public static final Type INSTANCE = new Type();
        public static final String ID = "molecular_metamorphosis";
    }

    //-----SERIALIZER-----//

    public static class Serializer implements RecipeSerializer<MolecularMetamorpherRecipe> {

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ExperienceObelisk.MOD_ID, "molecular_metamorphosis");

        @Override
        public MolecularMetamorpherRecipe fromJson(ResourceLocation id, JsonObject recipe) {

            Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getNonNull(recipe, "ingredient1"));
            Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getNonNull(recipe, "ingredient2"));
            Ingredient ingredient3 = Ingredient.fromJson(GsonHelper.getNonNull(recipe, "ingredient3"));
            int count1 = GsonHelper.getAsInt(recipe, "count1");
            int count2 = GsonHelper.getAsInt(recipe, "count2");
            int count3 = GsonHelper.getAsInt(recipe, "count3");

            Map<Ingredient, Tuple<Integer, Integer>> ingredients = new HashMap<>();
            ingredients.put(ingredient1, new Tuple<>(1, count1));
            ingredients.put(ingredient2, new Tuple<>(2, count2));
            ingredients.put(ingredient3, new Tuple<>(3, count3));

            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(recipe, "result"));
            int cost = GsonHelper.getAsInt(recipe, "cost");
            int processTime = GsonHelper.getAsInt(recipe, "processTime");

            return new MolecularMetamorpherRecipe(ImmutableMap.copyOf(ingredients), result, cost, processTime, id);
        }

        @Override
        public @Nullable MolecularMetamorpherRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
            Ingredient ingredient2 = Ingredient.fromNetwork(buffer);
            Ingredient ingredient3 = Ingredient.fromNetwork(buffer);
            int count1 = buffer.readInt();
            int count2 = buffer.readInt();
            int count3 = buffer.readInt();

            Map<Ingredient, Tuple<Integer, Integer>> ingredients = new HashMap<>();
            ingredients.put(ingredient1, new Tuple<>(1, count1));
            ingredients.put(ingredient2, new Tuple<>(2, count2));
            ingredients.put(ingredient3, new Tuple<>(3, count3));

            ItemStack result = buffer.readItem();
            int cost = buffer.readInt();
            int processTime = buffer.readInt();

            return new MolecularMetamorpherRecipe(ImmutableMap.copyOf(ingredients), result, cost, processTime, id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MolecularMetamorpherRecipe recipe) {

            for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : recipe.getIngredientMap().entrySet()){
                entry.getKey().toNetwork(buffer);
                buffer.writeInt(entry.getValue().getB());
            }

            buffer.writeItemStack(recipe.getResultItem(null), false);
            buffer.writeInt(recipe.cost);
            buffer.writeInt(recipe.processTime);
        }
    }


}
