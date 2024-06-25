package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.recipe.LaserTransfiguratorRecipe;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
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
import java.util.*;

public class LaserTransfiguratorEntity extends ExperienceReceivingEntity implements GeoBlockEntity {

    public LaserTransfiguratorEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.LASER_TRANSFIGURATOR_BE.get(), pos, state);
    }

    boolean isProcessing = false;
    int processTime = 0;
    int processProgress = 0;

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

        if(blockEntity instanceof LaserTransfiguratorEntity transfigurator){

            if(transfigurator.isProcessing){
                if(transfigurator.processProgress >= transfigurator.processTime){

                    transfigurator.setProcessing(false);
                    transfigurator.setProcessProgress(0);
                    transfigurator.setProcessTime(0);

                    transfigurator.dispenseResult();
                    System.out.println("Done!");
                }
                else{
                    transfigurator.incrementProcessProgress();
                }
            }
            else if(!transfigurator.inputsAreEmpty()){
                transfigurator.handleRecipes();
            }

        }

    }

    public boolean inputsAreEmpty(){
        if(itemHandler.getStackInSlot(0).is(Items.AIR)){

        }
        return itemHandler.getStackInSlot(0).isEmpty() && itemHandler.getStackInSlot(1).isEmpty() && itemHandler.getStackInSlot(2).isEmpty();
    }

    //-----------ITEM HANDLER-----------//

    protected ItemStackHandler itemHandler = transfiguratorHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private ItemStackHandler transfiguratorHandler(){

        return new ItemStackHandler(5){

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot <= 2;
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

    public void handleRecipes(){

        //handleRecipe(NETHERITE_RECIPE);
        //handleRecipe(LEATHER_RECIPE);
        //handleRecipe(PRIMORDIAL_RECIPE);
        handleJsonRecipes();
    }

    public void handleRecipe(Recipe recipe){

        ItemStack ingredient1 = recipe.ingredient1;
        ItemStack ingredient2 = recipe.ingredient2;
        ItemStack ingredient3 = recipe.ingredient3;
        ItemStack output = recipe.output;
        int cost = recipe.cost;
        int processTime = recipe.processTime;

        if(canPerformRecipe(output, cost)){

            System.out.println("----- Can perform recipe");

            if(deplete(ingredient1, ingredient2, ingredient3)){
                getBoundObelisk().drain(cost * 20);
                initiateRecipe(output, processTime);
            }
        }
    }

    public boolean canPerformRecipe(ItemStack output, int cost){

        //here we check for criteria that are independent of recipe parameters

        return getBoundObelisk() != null //has been bound to a valid obelisk
                && getBoundObelisk().getFluidAmount() >= cost * 20 //obelisk has enough Cognitium
                && (itemHandler.getStackInSlot(3).getItem().equals(output.getItem()) || itemHandler.getStackInSlot(3).isEmpty()) //results slot empty or same as output
                && itemHandler.getStackInSlot(3).getCount() <= 64 - output.getCount(); //results slot can accommodate output
    }

    public boolean deplete(ItemStack ingredient1, ItemStack ingredient2, ItemStack ingredient3){

        ItemStack[] recipeList = {ingredient1.copy(), ingredient2.copy(), ingredient3.copy()};
        ItemStack[] contentList = {itemHandler.getStackInSlot(0).copy(), itemHandler.getStackInSlot(1).copy(), itemHandler.getStackInSlot(2).copy()};
        Map<Item, Integer> recipeMap = new HashMap<>();
        Map<Item, Integer> contentMap = new HashMap<>();

        //populating the recipe map
        for(ItemStack stack : recipeList){
            if(!stack.isEmpty()){
                recipeMap.put(stack.getItem(), stack.getCount());
            }
        }

        //populating the content map
        for(ItemStack stack : contentList){
            if(!stack.isEmpty()){

                if(contentMap.containsKey(stack.getItem())){
                    int oldCount = contentMap.get(stack.getItem());
                    contentMap.replace(stack.getItem(), oldCount + stack.getCount());
                    //this is so we don't get duplicate instances of items in the map
                }
                else{
                    contentMap.put(stack.getItem(), stack.getCount());
                }
            }
        }

        boolean canDeplete = false;

        //checking:
        //1 -- if all the items in the recipe are represented in the contents
        //2 -- if the counts in the contents are sufficient

        if(contentMap.keySet().containsAll(recipeMap.keySet())){
            for(Item item : recipeMap.keySet()){
                if(contentMap.get(item) >= recipeMap.get(item)){
                    canDeplete = true;
                }
                else{
                    return false;
                }
            }
        }
        else{
            return false;
        }

        //depleting contents

        if(canDeplete){

            System.out.println("----- Can deplete");

            for(ItemStack stack1 : recipeList){
                Item item1 = stack1.getItem();
                int count1 = stack1.getCount();

                for(ItemStack stack2 : contentList){

                    Item item2 = stack2.getItem();
                    int count2 = stack2.getCount();

                    if(count1 > 0 && count2 > 0 && item1.equals(item2)){
                        if(count2 >= count1){
                            count2 = count2 - count1;
                            count1 = 0;
                        }
                        else{
                            count1 = count1 - count2;
                            count2 = 0;
                        }
                        stack1.setCount(count1);
                        stack2.setCount(count2);
                    }
                }
            }

            for(int i = 0; i < 3; i++){

                ItemStack stack = contentList[i];
                if(stack.is(Items.AIR) || stack.getCount() <= 0) stack = ItemStack.EMPTY;

                itemHandler.setStackInSlot(i, stack);
            }

            System.out.println("----- Successfully depleted");
        }

        return canDeplete;
    }

    public void initiateRecipe(ItemStack output, int processTime){

        this.setProcessing(true);
        this.setOutputItem(output);
        this.setProcessProgress(0);
        this.setProcessTime(processTime);
    }

    public void dispenseResult(){

        ItemStack result = itemHandler.getStackInSlot(4).copy();
        ItemStack existingStack = itemHandler.getStackInSlot(3).copy();

        System.out.println("Dispensing result: " + result);

        if(existingStack.getItem().equals(result.getItem())){
            existingStack.grow(1);
            itemHandler.setStackInSlot(3, existingStack);
        }
        else{
            itemHandler.setStackInSlot(3, result);
        }
    }

    public void handleJsonRecipes(){

        SimpleContainer container = new SimpleContainer(3);
        container.setItem(0, itemHandler.getStackInSlot(0));
        container.setItem(1, itemHandler.getStackInSlot(1));
        container.setItem(2, itemHandler.getStackInSlot(2));

        if(getRecipe(container).isPresent()){
            LaserTransfiguratorRecipe recipe = getRecipe(container).get();
            System.out.println("Recipe detected: "+recipe.getId());

            ItemStack[] ingredient1 = recipe.getIngredients().get(0).getItems();

            for(ItemStack stack : ingredient1){
                System.out.println(stack);
            }


            ItemStack result = recipe.getResultItem(null);
            int cost = recipe.getCost();
            int processTime = recipe.getProcessTime();

            if(canPerformRecipe(result, recipe.getCost())){

//                if(deplete(ingredient1, ingredient2, ingredient3)){
//                    getBoundObelisk().drain(recipe.getCost() * 20);
//                    initiateRecipe(result, recipe.getProcessTime());
//                }
            }
        }

    }

    public Optional<LaserTransfiguratorRecipe> getRecipe(SimpleContainer container){
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
        itemHandler.setStackInSlot(4, stack.copy());
        System.out.println("Output item set to: " + itemHandler.getStackInSlot(4));
    }

    //-----------NBT-----------//

    @Override
    public void setChanged() {
        if(this.level != null){
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
            //do this if live block entity data is needed in the GUI
        }
        super.setChanged();
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);

        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.isProcessing = tag.getBoolean("IsProcessing");
        this.processTime = tag.getInt("ProcessTime");
        this.processProgress = tag.getInt("ProcessProgress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putBoolean("IsProcessing", isProcessing);
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();

        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putBoolean("IsProcessing", isProcessing);
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //-----------HARDCODED TEST RECIPES-----------//

    private static class Recipe{

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

    private final Recipe NETHERITE_RECIPE = new Recipe(
            new ItemStack(Items.NETHERITE_SCRAP, 3),
            new ItemStack(Items.GOLD_INGOT, 6),
            ItemStack.EMPTY,
            new ItemStack(Items.NETHERITE_INGOT, 1),
            30, 50);

    private final Recipe LEATHER_RECIPE = new Recipe(
            new ItemStack(Items.DRIED_KELP, 4),
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            new ItemStack(Items.LEATHER, 1),
            1, 20);

    private final Recipe PRIMORDIAL_RECIPE = new Recipe(
            new ItemStack(Items.OAK_SAPLING, 1), //doesn't support tags rn, eventually i'd like to implement it
            new ItemStack(Items.GOLD_INGOT, 1),
            new ItemStack(RegisterItems.ASTUTE_ASSEMBLY.get(), 1),
            new ItemStack(RegisterItems.PRIMORDIAL_ASSEMBLY.get(), 1),
            1, 20);

}
