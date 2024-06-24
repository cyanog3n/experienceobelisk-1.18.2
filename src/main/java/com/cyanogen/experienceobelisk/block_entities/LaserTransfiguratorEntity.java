package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.recipe.LaserTransfiguratorRecipe;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LaserTransfiguratorEntity extends ExperienceReceivingEntity implements GeoBlockEntity {

    public LaserTransfiguratorEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.LASER_TRANSFIGURATOR_BE.get(), pos, state);
    }

    boolean isProcessing = false;
    int processTime = 0;
    int processProgress = 0;
    ItemStack outputItem = ItemStack.EMPTY;

    //-----------ANIMATIONS-----------//

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    protected <E extends ExperienceFountainEntity> PlayState controller(final AnimationState<E> state) {
        return null;
    }

    //-----------BEHAVIOR-----------//

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {

        if(blockEntity instanceof LaserTransfiguratorEntity transfigurator && transfigurator.isProcessing){

            if(transfigurator.processProgress >= transfigurator.processTime){
                transfigurator.dispenseResult();

                transfigurator.setOutputItem(ItemStack.EMPTY);
                transfigurator.setProcessProgress(0);
                transfigurator.setProcessTime(0);
                transfigurator.setProcessing(false);

                System.out.println("Done!");
            }
            else{
                transfigurator.incrementProcessProgress();
            }
        }


    }

    //-----------ITEM HANDLER-----------//

    protected ItemStackHandler itemHandler = transfiguratorHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private ItemStackHandler transfiguratorHandler(){

        return new ItemStackHandler(4){

            @Override
            protected void onContentsChanged(int slot) {

                System.out.println("Change in contents detected.");

                handleRecipe(NETHERITE_RECIPE);

                super.onContentsChanged(slot);
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot != 3;
            }
        };
    }

    public ItemStackHandler getItemHandler(){
        return itemHandler;
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == ForgeCapabilities.ITEM_HANDLER)
            return handler.cast();
        return super.getCapability(capability, facing);
    }

    //-----------RECIPE HANDLER-----------//

    public boolean canPerformRecipe(ItemStack ingredient1, ItemStack ingredient2, ItemStack ingredient3, ItemStack output, int cost){

        return !isProcessing //no recipe already in progress
                && hasIngredients(ingredient1, ingredient2, ingredient3) //has enough of the required ingredients
                && getBoundObelisk() != null //has been bound to a valid obelisk
                && getBoundObelisk().getFluidAmount() >= cost * 20 //obelisk has enough Cognitium
                && (itemHandler.getStackInSlot(3).getItem().equals(output.getItem()) || itemHandler.getStackInSlot(3).isEmpty()) //results slot empty or same as output
                && itemHandler.getStackInSlot(3).getCount() <= 64 - output.getCount(); //results slot can accommodate output
    }

    public boolean hasIngredients(ItemStack ingredient1, ItemStack ingredient2, ItemStack ingredient3){

        //the single application where i'd rather be using R than java

        List<Item> itemHandlerContents = new ArrayList<>(3);
        List<Integer> itemHandlerCounts = new ArrayList<>(3);
        List<Item> recipeIngredients = new ArrayList<>(3);
        List<Integer> recipeCounts = new ArrayList<>(3);

        for(int i = 0; i < itemHandler.getSlots(); i++){
            itemHandlerContents.add(i, itemHandler.getStackInSlot(i).getItem());
            itemHandlerCounts.add(i, itemHandler.getStackInSlot(i).getCount());
        }

        if(!ingredient1.isEmpty()){
            recipeIngredients.add(0, ingredient1.getItem());
            recipeCounts.add(0, ingredient1.getCount());
        }
        if(!ingredient2.isEmpty()){
            recipeIngredients.add(1, ingredient2.getItem());
            recipeCounts.add(1, ingredient2.getCount());
        }
        if(!ingredient3.isEmpty()){
            recipeIngredients.add(2, ingredient3.getItem());
            recipeCounts.add(2, ingredient3.getCount());
        }

        System.out.println(itemHandlerContents);
        System.out.println(itemHandlerCounts);
        System.out.println(recipeIngredients);
        System.out.println(recipeCounts);

        boolean enoughAll = false;

        if(itemHandlerContents.containsAll(recipeIngredients)){
            for(Item item : recipeIngredients){

                int indexHandler = itemHandlerContents.indexOf(item);
                int indexRecipe = recipeIngredients.indexOf(item);

                if(itemHandlerCounts.get(indexHandler) >= recipeCounts.get(indexRecipe)){
                    enoughAll = true;
                }
                else{
                    enoughAll = false;
                    break;
                }
            }
        }

        System.out.println("Ingredients Validated: " + enoughAll);

        return enoughAll;
    }

    public void initiateRecipe(ItemStack ingredient1, ItemStack ingredient2, ItemStack ingredient3, ItemStack output, int processTime){

        System.out.println("Recipe Initiated...");

        this.setOutputItem(output);
        this.setProcessProgress(0);
        this.setProcessTime(processTime);
        this.setProcessing(true);

        for(int i = 0; i < itemHandler.getSlots(); i++){
            ItemStack stack = itemHandler.getStackInSlot(i);

            if(stack.is(ingredient1.getItem())){
                stack.shrink(ingredient1.getCount());
            }
            else if(stack.is(ingredient2.getItem())){
                stack.shrink(ingredient2.getCount());
            }
            else if(stack.is(ingredient3.getItem())){
                stack.shrink(ingredient3.getCount());
            }

            //behavior might be a little weird if user has put in two stacks of the same item
            //to check out.

            itemHandler.setStackInSlot(i, stack);
        }

    }

    public void handleRecipe(Recipe recipe){

        ItemStack ingredient1 = recipe.ingredient1;
        ItemStack ingredient2 = recipe.ingredient2;
        ItemStack ingredient3 = recipe.ingredient3;
        ItemStack output = recipe.output;
        int cost = recipe.cost;
        int processTime = recipe.processTime;

        if(canPerformRecipe(ingredient1, ingredient2, ingredient3, output, cost)){
            System.out.println("Recipe Validated.");
            initiateRecipe(ingredient1, ingredient2, ingredient3, output, processTime);
        }

        //test recipe for making netherite
        //this will be replaced with recipe serializer later
    }

    public void dispenseResult(){

        ItemStack result = outputItem;
        ItemStack existingStack = itemHandler.getStackInSlot(3);

        System.out.println("Dispensing result: " + outputItem);

        if(existingStack.getItem().equals(result.getItem())){
            existingStack.grow(1);
            itemHandler.setStackInSlot(3, existingStack);
        }
        else{
            itemHandler.setStackInSlot(3, result);
        }
    }

    public Optional<LaserTransfiguratorRecipe> getRecipe(){

        SimpleContainer container = new SimpleContainer(3);
        container.setItem(0, itemHandler.getStackInSlot(0));
        container.setItem(1, itemHandler.getStackInSlot(1));
        container.setItem(2, itemHandler.getStackInSlot(2));

        return this.level.getRecipeManager().getRecipeFor(LaserTransfiguratorRecipe.Type.INSTANCE, container, level);
    }

    public ExperienceObeliskEntity getBoundObelisk(){

        if(this.level.getBlockEntity(this.getBoundPos()) instanceof ExperienceObeliskEntity obelisk){
            return obelisk;
        }
        else{
            return null;
        }
    }

    public void setProcessing(boolean isProcessing){
        this.isProcessing = isProcessing;
        setChanged();
    }

    public int getProcessTime(){
        return processTime;
    }

    public void setProcessTime(int time){
        this.processTime = time;
        setChanged();
    }

    public int getProcessProgress(){
        return processProgress;
    }

    public void setProcessProgress(int progress){
        this.processProgress = progress;
        setChanged();
    }

    public void incrementProcessProgress(){
        this.processProgress += 1;
        setChanged();
    }

    public void setOutputItem(ItemStack stack){
        this.outputItem = stack;
        setChanged();
    }

    //-----------NBT-----------//

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);

        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.isProcessing = tag.getBoolean("IsProcessing");
        this.processTime = tag.getInt("ProcessTime");
        this.processProgress = tag.getInt("ProcessProgress");
        this.outputItem = ItemStack.of(tag.getCompound("OutputItem"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putBoolean("IsProcessing", isProcessing);
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);
        tag.put("OutputItem", outputItem.getOrCreateTag());
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();

        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putBoolean("IsProcessing", isProcessing);
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);
        tag.put("OutputItem", outputItem.getOrCreateTag());

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //-----------TEST RECIPES-----------//

    private class Recipe{

        ItemStack ingredient1;
        ItemStack ingredient2;
        ItemStack ingredient3;
        ItemStack output;
        int cost;
        int processTime;

        private Recipe(ItemStack ingredient1, ItemStack ingredient2, ItemStack ingredient3, ItemStack output, int cost, int processTime){
            this.ingredient1 = ingredient1;
            this.ingredient2 = ingredient2;
            this.ingredient3 = ingredient3;
            this.output = output;
            this.cost = cost;
            this.processTime = processTime;
        }

    }

    Recipe NETHERITE_RECIPE = new Recipe(
            new ItemStack(Items.NETHERITE_SCRAP, 3),
            new ItemStack(Items.GOLD_INGOT, 6),
            ItemStack.EMPTY,
            new ItemStack(Items.NETHERITE_INGOT, 1),
            30,200);

}
