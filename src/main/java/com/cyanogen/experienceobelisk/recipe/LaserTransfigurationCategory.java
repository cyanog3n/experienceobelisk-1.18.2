package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LaserTransfigurationCategory implements IRecipeCategory<LaserTransfiguratorRecipe>{

    IRecipeCategoryRegistration registration;
    IGuiHelper guiHelper;
    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/laser_transfigurator_jei.png");

    public LaserTransfigurationCategory(IRecipeCategoryRegistration registration){
        this.registration = registration;
        this.guiHelper = registration.getJeiHelpers().getGuiHelper();
    }

    @Override
    public RecipeType<LaserTransfiguratorRecipe> getRecipeType() {
        return RecipeType.create(LaserTransfiguratorRecipe.Type.ID, ExperienceObelisk.MOD_ID, LaserTransfiguratorRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.literal("Laser Transfigurator");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(texture, 0, 0, 176, 87);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(LaserTransfiguratorRecipe recipe) {
        return IRecipeCategory.super.getRegistryName(recipe);
    }

    @Override
    public IDrawable getIcon() {

        ItemStack icon = new ItemStack(RegisterItems.LASER_TRANSFIGURATOR_ITEM.get());
        return guiHelper.createDrawableItemStack(icon);
    }

    @Override
    public void draw(LaserTransfiguratorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        guiHelper.drawableBuilder().buildAnimated();
        //add text for XP amount

        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserTransfiguratorRecipe recipe, IFocusGroup focuses) {

        ItemStack result = recipe.getResultItem(null);
        //position of 1st slot
        int x = 21; //21, 36, 51
        int y = 21;

        for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : recipe.getIngredientMap().entrySet()){

            int position = entry.getValue().getA() - 1;
            int count = entry.getValue().getB();
            Ingredient ingredient = entry.getKey();

            if(count != -99){
                builder.addSlot(RecipeIngredientRole.INPUT, x + position * 30, y).setSlotName("input" + position).addItemStacks(getItemListWithCounts(ingredient, count));
            }

        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 21 + 4 * 30,21).setSlotName("output").addItemStack(result);
        builder.setShapeless();
    }

    public List<ItemStack> getItemListWithCounts(Ingredient ingredient, int count){
        List<ItemStack> list = new ArrayList<>();

        for(ItemStack stack : ingredient.getItems()){
            ItemStack stack2 = stack.copy();
            stack2.setCount(count);
            list.add(stack2);
        }

        return list;
    }


}
