package com.cyanogen.experienceobelisk.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ExperienceJellyItem extends Item {

    public ExperienceJellyItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder().nutrition(5).saturationMod(0.6F).build()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        FoodData data = player.getFoodData();
        int foodLevel = data.getFoodLevel();

        if(data.needsFood() || player.isCreative()){

            data.setFoodLevel(Math.min(20, foodLevel + 5));
            data.setSaturation(Math.min(data.getFoodLevel(), data.getSaturationLevel() + 0.6f));
            player.playSound(SoundEvents.GENERIC_EAT);

            if(!player.isCreative()){
                stack.shrink(1);
            }

            return InteractionResultHolder.consume(stack);
        }
        else{
            return super.use(level, player, hand);
        }
    }

    @Override
    public boolean isEdible() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable("tooltip.experienceobelisk.experience_jelly.notes"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

}
