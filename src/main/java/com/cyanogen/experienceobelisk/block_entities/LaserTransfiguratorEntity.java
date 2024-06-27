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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
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
import java.util.Map;
import java.util.Optional;

public class LaserTransfiguratorEntity extends ExperienceReceivingEntity implements GeoBlockEntity {

    public LaserTransfiguratorEntity(BlockPos pos, BlockState state) {
        super(RegisterBlockEntities.LASER_TRANSFIGURATOR_BE.get(), pos, state);
    }

    boolean isProcessing = false;
    int processTime = 0;
    int processProgress = 0;
    NonNullList<ItemStack> remainderItems = NonNullList.withSize(4, ItemStack.EMPTY);
    ResourceLocation recipeId;

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

                    transfigurator.dispenseResult();
                }
                else{
                    transfigurator.incrementProcessProgress();
                }
            }
            else if(transfigurator.hasContents()){
                transfigurator.handleJsonRecipes();
            }

        }

    }

    public boolean hasContents(){

        boolean hasContents = false;

        for(int i = 0; i < 3; i++){
            ItemStack stack = itemHandler.getStackInSlot(i);
            if(!stack.isEmpty() && !stack.getItem().equals(Items.AIR)){
                hasContents = true;
                break;
            }
        }
        return hasContents;
    }

    //-----------ITEM HANDLER-----------//

    protected ItemStackHandler itemHandler = transfiguratorHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private ItemStackHandler transfiguratorHandler(){

        return new ItemStackHandler(4){

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot <= 2;
            }

            @Override
            protected void onContentsChanged(int slot) {
                if(slot <= 2 && isProcessing){

                    if(getRecipe().isEmpty()){
                        setProcessing(false);
                        resetAll();
                    }
                    else{
                        setRemainderItems(deplete(getRecipe().get()));
                    }

                }
                super.onContentsChanged(slot);
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

    public void handleJsonRecipes(){

        if(getRecipe().isPresent()){
            LaserTransfiguratorRecipe recipe = getRecipe().get();
            ItemStack output = recipe.getResultItem(null);
            int cost = recipe.getCost();
            int processTime = recipe.getProcessTime();

            System.out.println("Recipe detected: " + recipe.getId());

            if(canPerformRecipe(output, cost)){
                System.out.println("Can perform recipe, proceeding... process time: " + processTime);

                initiateRecipe(recipe);
                getBoundObelisk().drain(cost * 20);
            }

        }

    }

    public boolean canPerformRecipe(ItemStack output, int cost){

        //here we check for criteria that are independent of recipe parameters

        return getBoundObelisk() != null //has been bound to a valid obelisk
                && getBoundObelisk().getFluidAmount() >= cost * 20 //obelisk has enough Cognitium
                && (itemHandler.getStackInSlot(3).getItem().equals(output.getItem())
                || itemHandler.getStackInSlot(3).isEmpty() || itemHandler.getStackInSlot(3).is(Items.AIR)) //results slot empty or same as output
                && itemHandler.getStackInSlot(3).getCount() <= 64 - output.getCount(); //results slot can accommodate output
    }

    public void initiateRecipe(LaserTransfiguratorRecipe recipe){

        this.setProcessing(true);
        this.setRecipeId(recipe);
        this.setOutputItem(recipe.getResultItem(null));
        this.setRemainderItems(deplete(recipe));
        this.setProcessProgress(0);
        this.setProcessTime(recipe.getProcessTime());
    }

    public SimpleContainer deplete(LaserTransfiguratorRecipe recipe){

        SimpleContainer container = getRecipeContainer();

        Map<Ingredient, Integer> ingredientMap = recipe.getIngredientMapNoFiller();

        for(Map.Entry<Ingredient, Integer> entry : ingredientMap.entrySet()){

            Ingredient ingredient = entry.getKey();
            int count = entry.getValue();

            for(int i = 0; i < 3; i++){
                ItemStack stack = container.getItem(i);

                if(ingredient.test(stack)){
                    stack.shrink(count);
                    break;
                }
            }
        }

        return container;
    }

    public void dispenseResult(){

        setProcessing(false);

        ItemStack result = remainderItems.get(3);
        ItemStack existingStack = itemHandler.getStackInSlot(3).copy();

        if(existingStack.getItem().equals(result.getItem())){
            existingStack.grow(1);
            itemHandler.setStackInSlot(3, existingStack);
        }
        else{
            itemHandler.setStackInSlot(3, result);
        }

        for(int i = 0; i < 3; i++){
            itemHandler.setStackInSlot(i, remainderItems.get(i));
        }

        resetAll();

        System.out.println("Recipe done, dispensing result: " + result);
    }

    public Optional<LaserTransfiguratorRecipe> getRecipe(){
        return this.level.getRecipeManager().getRecipeFor(LaserTransfiguratorRecipe.Type.INSTANCE, getRecipeContainer(), level);
    }

    public SimpleContainer getRecipeContainer(){
        SimpleContainer container = new SimpleContainer(3);
        container.setItem(0, itemHandler.getStackInSlot(0).copy());
        container.setItem(1, itemHandler.getStackInSlot(1).copy());
        container.setItem(2, itemHandler.getStackInSlot(2).copy());

        return container;
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

    public void setRemainderItems(SimpleContainer container){
        for(int i = 0; i < 3; i++){
            remainderItems.set(i, container.getItem(i));
        }
        setChanged();

        System.out.println("Remainder items set to: " + remainderItems);
    }

    public void setOutputItem(ItemStack stack){
        remainderItems.set(3, stack);
        setChanged();

        System.out.println("Output item set to: " + stack);
    }

    public void setRecipeId(LaserTransfiguratorRecipe recipe){
        this.recipeId = recipe.getId();
        setChanged();
    }

    public void resetAll(){

        processProgress = 0;
        processTime = 0;
        this.remainderItems = NonNullList.withSize(4, ItemStack.EMPTY);
        recipeId = null;
        setChanged();
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
        this.remainderItems = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, remainderItems);

        this.isProcessing = tag.getBoolean("IsProcessing");
        this.processTime = tag.getInt("ProcessTime");
        this.processProgress = tag.getInt("ProcessProgress");
        this.recipeId = new ResourceLocation(tag.getString("RecipeID"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);

        tag.put("Inventory", itemHandler.serializeNBT());
        ContainerHelper.saveAllItems(tag, remainderItems);

        tag.putBoolean("IsProcessing", isProcessing);
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);

        if(recipeId != null){
            tag.putString("RecipeID", recipeId.toString());
        }
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag tag = super.getUpdateTag();

        tag.put("Inventory", itemHandler.serializeNBT());
        ContainerHelper.saveAllItems(tag, remainderItems);

        tag.putBoolean("IsProcessing", isProcessing);
        tag.putInt("ProcessTime", processTime);
        tag.putInt("ProcessProgress", processProgress);

        if(recipeId != null){
            tag.putString("RecipeID", recipeId.toString());
        }

        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
