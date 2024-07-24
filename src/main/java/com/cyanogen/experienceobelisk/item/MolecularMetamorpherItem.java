package com.cyanogen.experienceobelisk.item;

import com.cyanogen.experienceobelisk.renderer.MolecularMetamorpherItemRenderer;
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

public class MolecularMetamorpherItem extends BlockItem implements IAnimatable {

    public MolecularMetamorpherItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    //-----ANIMATIONS-----//

    private static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle");

    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController controller = event.getController();
        controller.transitionLengthTicks = 0;
        controller.setAnimation(IDLE);

        return PlayState.CONTINUE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new MolecularMetamorpherItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "molecular_metamorpher_item_controller", 0, this::predicate));
    }

    private final AnimationFactory manager = GeckoLibUtil.createFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    //-----CUSTOM HOVER TEXT-----//


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        boolean isEmpty = stack.getOrCreateTag().getCompound("BlockEntityTag").getCompound("Inventory").getList("Items", 10).isEmpty();
        //Each itemstack should be a compound, which has an ID of 10. See net.minecraft.nbt.Tag

        if(!isEmpty){
            tooltip.add(Component.translatable("tooltip.experienceobelisk.molecular_metamorpher.item_contents"));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
