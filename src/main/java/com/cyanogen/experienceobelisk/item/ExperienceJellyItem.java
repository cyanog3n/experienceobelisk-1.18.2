package com.cyanogen.experienceobelisk.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
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

    private final int nutrition = 5;
    private final float saturation = 0.8f;

    public ExperienceJellyItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);
        FoodData data = player.getFoodData();

        if(data.needsFood() || player.isCreative()){

            data.eat(nutrition, saturation);
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

        tooltip.add(Component.translatable("tooltip.experienceobelisk.experience_jelly.comment"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

}
