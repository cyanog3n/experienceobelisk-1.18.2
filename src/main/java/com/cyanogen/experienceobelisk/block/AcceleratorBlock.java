package com.cyanogen.experienceobelisk.block;

import com.cyanogen.experienceobelisk.block_entities.AcceleratorEntity;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class AcceleratorBlock extends ExperienceReceivingBlock implements EntityBlock {

    public AcceleratorBlock() {
        super(Properties.of()
                .strength(9f)
                .sound(SoundType.NETHERITE_BLOCK)
                .destroyTime(1.2f)
                .requiresCorrectToolForDrops()
                .explosionResistance(9f)
                .noOcclusion()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction looking = context.getNearestLookingDirection();
        if(looking.getAxis().isVertical()){
            looking = looking.getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, looking);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        Direction useDirection = player.getDirection();

        if(stack.is(RegisterItems.ATTUNEMENT_STAFF.get())){

            if(player.isShiftKeyDown()){
                if(level.getBlockEntity(pos) instanceof AcceleratorEntity accelerator){
                    accelerator.toggleRedstoneEnabled();

                    System.out.println("triggered (block)");

                    if(accelerator.redstoneEnabled){
                        player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.enable_redstone"), true);
                    }
                    else{
                        player.displayClientMessage(Component.translatable("message.experienceobelisk.binding_wand.disable_redstone"), true);
                    }

                }
            }
            else{
                Direction direction = state.getValue(FACING);

                if(useDirection.getAxisDirection().equals(Direction.AxisDirection.POSITIVE)){
                    state = state.setValue(FACING, direction.getCounterClockWise(useDirection.getAxis()));
                }
                else{
                    state = state.setValue(FACING, direction.getClockWise(useDirection.getAxis()));
                }

                level.setBlockAndUpdate(pos, state);
            }
        }

        return super.use(state, level, pos, player, hand, result);
    }

    //-----BLOCK ENTITY-----//

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == RegisterBlockEntities.ACCELERATOR_BE.get() ? AcceleratorEntity::tick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegisterBlockEntities.ACCELERATOR_BE.get().create(pos, state);
    }
}
