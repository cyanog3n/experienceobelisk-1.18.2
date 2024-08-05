package com.cyanogen.experienceobelisk.block;

import com.cyanogen.experienceobelisk.block_entities.MolecularMetamorpherEntity;
import com.cyanogen.experienceobelisk.gui.MolecularMetamorpherMenu;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class MolecularMetamorpherBlock extends ExperienceReceivingBlock implements EntityBlock {

    public MolecularMetamorpherBlock() {
        super(Properties.of()
                .strength(9f)
                .destroyTime(1.2f)
                .explosionResistance(9f)
                .noOcclusion()
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

        if(super.use(state, level, pos, player, hand, result) != InteractionResult.PASS){
            return InteractionResult.CONSUME;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            NetworkHooks.openScreen((ServerPlayer) player, state.getMenuProvider(level,pos), pos);
            return InteractionResult.CONSUME;
        }
    }

    VoxelShape base = Shapes.create(new AABB(0 / 16D,0 / 16D,0 / 16D,16 / 16D,8 / 16D,16 / 16D));
    VoxelShape pedestal = Shapes.create(new AABB(2 / 16D,8 / 16D,2 / 16D,14 / 16D,10 / 16D,14 / 16D));
    VoxelShape emitter = Shapes.create(new AABB(5.5 / 16D,17 / 16D,5.5 / 16D,10.5 / 16D,26 / 16D,10.5 / 16D));
    VoxelShape baseWhole = Shapes.join(base, pedestal, BooleanOp.OR);
    VoxelShape whole = Shapes.join(baseWhole, emitter, BooleanOp.OR);

    @Override
    public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
        return true;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter getter, BlockPos pos) {
        return whole;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return whole;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter getter, BlockPos pos) {

        if(getter.getBlockEntity(pos) instanceof MolecularMetamorpherEntity metamorpher && metamorpher.isBusy()){
            return 7;
        }
        else{
            return 0;
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {

        IItemHandler inputHandler = null;
        IItemHandler outputHandler = null;
        if(level.getBlockEntity(pos) instanceof MolecularMetamorpherEntity entity){
            inputHandler = entity.getInputHandler();
            outputHandler = entity.getOutputHandler();
        }
        final IItemHandler inputs = inputHandler;
        final IItemHandler output = outputHandler;

        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("Molecular Metamorpher");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return new MolecularMetamorpherMenu(containerId, inventory, inputs, output, player, pos);
            }
        };

    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == RegisterBlockEntities.MOLECULAR_METAMORPHER_BE.get() ? MolecularMetamorpherEntity::tick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return RegisterBlockEntities.MOLECULAR_METAMORPHER_BE.get().create(pos, state);
    }
}
