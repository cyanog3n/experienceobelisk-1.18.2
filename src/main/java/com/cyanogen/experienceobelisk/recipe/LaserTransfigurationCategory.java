package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class LaserTransfigurationCategory implements IRecipeCategory<LaserTransfiguratorRecipe>{

    IRecipeCategoryRegistration registration;

    public LaserTransfigurationCategory(IRecipeCategoryRegistration registration){
        this.registration = registration;
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
        return null;
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(LaserTransfiguratorRecipe recipe) {
        return IRecipeCategory.super.getRegistryName(recipe);
    }

    @Override
    public IDrawable getIcon() {

        ItemStack icon = new ItemStack(RegisterItems.LASER_TRANSFIGURATOR_ITEM.get());
        return registration.getJeiHelpers().getGuiHelper().createDrawableItemStack(icon);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserTransfiguratorRecipe recipe, IFocusGroup focuses) {

    }


}
