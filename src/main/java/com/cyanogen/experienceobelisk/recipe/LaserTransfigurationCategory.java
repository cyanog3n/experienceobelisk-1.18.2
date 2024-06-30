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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaserTransfigurationCategory implements IRecipeCategory<LaserTransfiguratorRecipe>{

    IRecipeCategoryRegistration registration;
    IGuiHelper guiHelper;
    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/experience_obelisk.png");

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
        return guiHelper.createBlankDrawable(100, 50);
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
        //add text for XP amount
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserTransfiguratorRecipe recipe, IFocusGroup focuses) {

        List<Ingredient> ingredients = recipe.getIngredientListWithEmpty();
        ItemStack result = recipe.getResultItem(null);

        builder.addSlot(RecipeIngredientRole.INPUT, 19,35).setSlotName("input1").addIngredients(ingredients.get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 50,53).setSlotName("input2").addIngredients(ingredients.get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 69,17).setSlotName("input3").addIngredients(ingredients.get(2));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 140,35).setSlotName("output").addItemStack(result);

        builder.setShapeless();


    }


}
