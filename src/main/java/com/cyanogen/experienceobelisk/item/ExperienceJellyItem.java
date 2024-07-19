package com.cyanogen.experienceobelisk.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class ExperienceJellyItem extends Item {

    public ExperienceJellyItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build()).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        FoodData data = player.getFoodData();
        int foodLevel = data.getFoodLevel();

        if(data.needsFood() || player.isCreative()){

            data.setFoodLevel(Math.min(20, foodLevel + 4));
            data.setSaturation(Math.min(data.getFoodLevel(), data.getSaturationLevel() + 0.6f));
            player.playSound(SoundEvents.GENERIC_EAT);

            if(!player.isCreative()){
                stack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        else{
            return super.use(level, player, hand);
        }
    }

    @Override
    public boolean isEdible() {
        return false;
    }

}
