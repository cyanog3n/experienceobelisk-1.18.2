package com.cyanogen.experienceobelisk.block_entities;

import com.cyanogen.experienceobelisk.recipe.LaserTransfiguratorRecipe;
import com.cyanogen.experienceobelisk.registries.RegisterBlockEntities;
import com.cyanogen.experienceobelisk.registries.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
                }
                else{
                    transfigurator.incrementProcessProgress();
                }
            }
            else if(transfigurator.hasContents()){
                System.out.println("Items detected.");
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

    public void handleJsonRecipes(){

        SimpleContainer container = new SimpleContainer(3);
        container.setItem(0, itemHandler.getStackInSlot(0));
        container.setItem(1, itemHandler.getStackInSlot(1));
        container.setItem(2, itemHandler.getStackInSlot(2));

        if(getRecipe(container).isPresent()){
            LaserTransfiguratorRecipe recipe = getRecipe(container).get();
            ItemStack output = recipe.getResultItem(null);
            int cost = recipe.getCost();
            int processTime = recipe.getProcessTime();

            System.out.println("Recipe detected: " + recipe.getId());

            if(canPerformRecipe(output, cost)){
                System.out.println("Can perform recipe, proceeding... process time: " + processTime);
                initiateRecipe(output, processTime);
                getBoundObelisk().drain(cost * 20);
                deplete(recipe);
                System.out.println("Contents depleted.");
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

    public void deplete(LaserTransfiguratorRecipe recipe){

        Map<Ingredient, Integer> ingredientMap = recipe.getIngredientMapNoFiller();

        for(Map.Entry<Ingredient, Integer> entry : ingredientMap.entrySet()){

            Ingredient ingredient = entry.getKey();
            int count = entry.getValue();

            for(int i = 0; i < 3; i++){
                ItemStack stack = itemHandler.getStackInSlot(i);

                if(ingredient.test(stack)){
                    stack.shrink(count);
                    break;
                }
            }
        }
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

        System.out.println("Recipe done, dispensing result: " + result);

        if(existingStack.getItem().equals(result.getItem())){
            existingStack.grow(1);
            itemHandler.setStackInSlot(3, existingStack);
        }
        else{
            itemHandler.setStackInSlot(3, result);
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

}
