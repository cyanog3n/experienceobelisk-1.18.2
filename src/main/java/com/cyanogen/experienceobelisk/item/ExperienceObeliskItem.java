package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.renderer.ExperienceObeliskItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

import static com.cyanogen.experienceobelisk.utils.ExperienceUtils.xpToLevels;


public class ExperienceObeliskItem extends BlockItem implements IAnimatable{

    public ExperienceObeliskItem(Block block, Properties p) {
        super(block, p);
    }

    //-----ANIMATIONS-----//

    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.transitionLengthTicks = 0;
        controller.setAnimation(new AnimationBuilder().addAnimation("idle", true));

        return PlayState.CONTINUE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new ExperienceObeliskItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    private final AnimationFactory manager = GeckoLibUtil.createFactory(this);;
    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    //-----CUSTOM HOVER TEXT-----//

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        int amount = stack.getOrCreateTag().getCompound("BlockEntityTag").getInt("Amount");
        int levels = xpToLevels(amount / 20);

        if(stack.hasTag()){
            tooltip.add(Component.translatable("tooltip.experienceobelisk.experience_obelisk.item_fluid_amount",
                    Component.literal(amount + " mB").withStyle(ChatFormatting.GOLD)));

            tooltip.add(Component.translatable("tooltip.experienceobelisk.experience_obelisk.item_levels",
                    Component.literal(String.valueOf(levels)).withStyle(ChatFormatting.GREEN)));
        }

        super.appendHoverText(stack, level, tooltip, flag);

    }

}
