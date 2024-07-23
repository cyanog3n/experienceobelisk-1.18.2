package com.cyanogen.experienceobelisk.recipe.jei;

import com.cyanogen.experienceobelisk.ExperienceObelisk;
import com.cyanogen.experienceobelisk.recipe.MolecularMetamorpherRecipe;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import com.cyanogen.experienceobelisk.utils.ExperienceUtils;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cyanogen.experienceobelisk.utils.RecipeUtils.getItemListWithCounts;

public class MolecularMetamorpherCategory implements IRecipeCategory<MolecularMetamorpherRecipe>{

    IRecipeCategoryRegistration registration;
    IGuiHelper guiHelper;
    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/recipes/molecular_metamorpher_jei.png");
    private final IDrawableAnimated arrow;

    public MolecularMetamorpherCategory(IRecipeCategoryRegistration registration){
        this.registration = registration;
        this.guiHelper = registration.getJeiHelpers().getGuiHelper();

        this.arrow = guiHelper.drawableBuilder(texture,0,87,29,5)
                .buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<MolecularMetamorpherRecipe> getRecipeType() {
        return RecipeType.create(MolecularMetamorpherRecipe.Type.ID, ExperienceObelisk.MOD_ID, MolecularMetamorpherRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.experienceobelisk.molecular_metamorpher");
    }

    @Override
    public IDrawable getBackground() {
        return guiHelper.createDrawable(texture, 0, 0, 176, 87);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(MolecularMetamorpherRecipe recipe) {
        return IRecipeCategory.super.getRegistryName(recipe);
    }

    @Override
    public IDrawable getIcon() {

        ItemStack icon = new ItemStack(RegisterItems.MOLECULAR_METAMORPHER_ITEM.get());
        return guiHelper.createDrawableItemStack(icon);
    }

    @Override
    public void draw(MolecularMetamorpherRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        arrow.draw(guiGraphics, 108, 47);

        Font font = Minecraft.getInstance().font;
        int cost = recipe.getCost();
        int processTime = recipe.getProcessTime();
        int levelCost = ExperienceUtils.xpToLevels(cost);

        Component costLabel = Component.translatable("jei.experienceobelisk.category.base_level_cost", levelCost);
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
    public List<Component> getTooltipStrings(MolecularMetamorpherRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {

        Font font = Minecraft.getInstance().font;
        int cost = recipe.getCost();
        int levelCost = ExperienceUtils.xpToLevels(cost);

        Component costLabel = Component.translatable("jei.experienceobelisk.category.base_level_cost", levelCost);
        int costLabelWidth = font.width(costLabel);

        List<Component> tooltip = new ArrayList<>();

        int x1 = getWidth() - 4 - costLabelWidth;
        int x2 = getWidth();
        int y1 = getHeight() - 9;
        int y2 = getHeight();

        if(mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2){
            tooltip.add(Component.translatable("tooltip.experienceobelisk.experience_obelisk.xp",
                    Component.literal(String.valueOf(cost)).withStyle(ChatFormatting.GREEN)));

            return tooltip;
        }

        return IRecipeCategory.super.getTooltipStrings(recipe, recipeSlotsView, mouseX, mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MolecularMetamorpherRecipe recipe, IFocusGroup focuses) {

        ItemStack result = recipe.getResultItem(null);
        int[] x = {19,50,70};
        int[] y = {35,52,18};

        for(Map.Entry<Ingredient, Tuple<Integer, Integer>> entry : recipe.getIngredientMapNoFiller().entrySet()){

            int position = entry.getValue().getA() - 1;
            int count = entry.getValue().getB();
            Ingredient ingredient = entry.getKey();

            builder.addSlot(RecipeIngredientRole.INPUT, x[position], y[position])
                    .setSlotName("input" + position)
                    .addItemStacks(getItemListWithCounts(ingredient, count));
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 140,35).setSlotName("output").addItemStack(result);

        if(!recipe.getId().equals(new ResourceLocation(ExperienceObelisk.MOD_ID, "item_name_formatting"))){
            builder.setShapeless();
        }

    }


}
