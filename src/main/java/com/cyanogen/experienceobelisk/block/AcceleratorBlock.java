package com.cyanogen.experienceobelisk.block;

import com.cyanogen.experienceobelisk.block_entities.AcceleratorEntity;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
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
        return this.defaultBlockState().setValue(FACING, looking.getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        Direction useDirection = result.getDirection();

        if(stack.is(RegisterItems.ATTUNEMENT_STAFF.get())){

            Direction direction = state.getValue(FACING);
            state = state.setValue(FACING, direction.getClockWise(useDirection.getAxis()));
            level.setBlockAndUpdate(pos, state);
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
