package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.renderer.LaserTransfiguratorItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class LaserTransfiguratorItem extends BlockItem implements GeoItem {

    public LaserTransfiguratorItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    //-----ANIMATIONS-----//

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, this::controller));

    }

    protected <E extends LaserTransfiguratorItem> PlayState controller(final AnimationState<E> state) {

        AnimationController<E> controller = state.getController();
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new LaserTransfiguratorItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    //-----CUSTOM HOVER TEXT-----//


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        boolean isEmpty = stack.getOrCreateTag().getCompound("BlockEntityTag").getCompound("Inventory").getList("Items", 10).isEmpty();
        //Each itemstack should be a compound, which has an ID of 10. See net.minecraft.nbt.Tag

        if(!isEmpty){
            tooltip.add(Component.translatable("tooltip.experienceobelisk.laser_transfigurator.item_contents"));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
