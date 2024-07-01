package com.cyanogen.experienceobelisk.recipe;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import com.cyanogen.experienceobelisk.utils.ExperienceUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.extensions.IForgeFont;
import org.jetbrains.annotations.Nullable;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LaserTransfigurationCategory implements IRecipeCategory<LaserTransfiguratorRecipe>{

    IRecipeCategoryRegistration registration;
    IGuiHelper guiHelper;
    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/recipes/laser_transfigurator_jei.png");
    private final IDrawableAnimated arrow;

    public LaserTransfigurationCategory(IRecipeCategoryRegistration registration){
        this.registration = registration;
        this.guiHelper = registration.getJeiHelpers().getGuiHelper();

        this.arrow = guiHelper.drawableBuilder(texture,0,87,29,5)
                .buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
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

        arrow.draw(guiGraphics, 108, 47);

        Font font = Minecraft.getInstance().font;
        int cost = recipe.getCost();
        int processTime = recipe.getProcessTime();
        int levelCost = ExperienceUtils.xpToLevels(cost);

        Component costLabel = Component.literal(cost + " XP / " + levelCost + " Lv");
        Component timeLabel = Component.literal(processTime / 20 + "s");
        int costLabelWidth = font.width(costLabel);
        int timeLabelWidth = font.width(timeLabel);
        int grey = 0x7E7E7E;

        guiGraphics.drawString(font, costLabel.getVisualOrderText(),
                getWidth() - 4 - costLabelWidth,getHeight() - 9, grey, false);
        guiGraphics.drawString(font, timeLabel.getVisualOrderText(),
                getWidth() - 4 - timeLabelWidth,getHeight() - 20, grey, false);


        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserTransfiguratorRecipe recipe, IFocusGroup focuses) {

        ItemStack result = recipe.getResultItem(null);
        int[] x = {19,50,70};
        int[] y = {35,52,18};

        for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : recipe.getIngredientMap().entrySet()){

            int position = entry.getValue().getA() - 1;
            int count = entry.getValue().getB();
            Ingredient ingredient = entry.getKey();

            if(count != -99){
                builder.addSlot(RecipeIngredientRole.INPUT, x[position], y[position]).setSlotName("input" + position).addItemStacks(getItemListWithCounts(ingredient, count));
            }

        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 140,35).setSlotName("output").addItemStack(result);
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
