package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.MolecularMetamorpherEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MolecularMetamorpherScreen extends AbstractContainerScreen<MolecularMetamorpherMenu>{

    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/molecular_metamorpher.png");
    public MolecularMetamorpherEntity metamorpher;
    private final Component title = Component.translatable("title.experienceobelisk.molecular_metamorpher");
    private final Component inventoryTitle = Component.translatable("title.experienceobelisk.precision_dispeller.inventory");
    private final MolecularMetamorpherMenu menu;

    public MolecularMetamorpherScreen(MolecularMetamorpherMenu menu, Inventory inventory, Component component) {
        super(menu, menu.inventory, menu.component);
        this.metamorpher = menu.metamorpherClient;
        this.menu = menu;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        setupWidgetElements();
        super.init();
    }

    @Override
    protected void renderBg(PoseStack poseStack, float f, int x, int y) {

    }

    @Override
    protected void renderLabels(PoseStack poseStack, int p_97809_, int p_97810_) {
        drawString(poseStack, this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF);
        drawString(poseStack, this.font, this.inventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFF);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        renderBackground(poseStack);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int arrowWidth = 26;
        double completion = 0;
        if(metamorpher.getProcessTime() != 0){
            completion = metamorpher.getProcessProgress() / (float) metamorpher.getProcessTime();
        }

        //render background texture
        blit(poseStack, x, y, 0, 0, 176, 166);

        //render recipe progress
        blit(poseStack, this.width / 2 + 109 - 88, this.height / 2 + 48 - 83, 0, 175, (int) (arrowWidth * completion), 4);

        //render xp bar
        int xpBarWidth = 61;
        int levels;
        int points;
        double progress;

        if(metamorpher.obeliskStillExists){
            levels = metamorpher.obeliskLevels;
            points = metamorpher.obeliskPoints;
            progress = metamorpher.obeliskProgress;

            blit(poseStack, this.width / 2 + 105 - 88, this.height / 2 + 70 - 83, 0, 179, 64, 11);
            blit(poseStack, this.width / 2 + 107 - 88, this.height / 2 + 71 - 83, 0, 166, (int) (xpBarWidth * progress), 9);

            //render level counter
            drawCenteredString(new PoseStack(), this.font, Component.literal(String.valueOf(levels)).withStyle(ChatFormatting.GREEN),
                    this.width / 2 + 52,this.height / 2 - 11, 0xFFFFFF);

            //render XP tooltip
            int x1 = this.width / 2 + 19;
            int y1 = this.height / 2 - 12;
            int x2 = x1 + xpBarWidth;
            int y2 = y1 + 9;

            List<Component> tooltipList = new ArrayList<>();

            tooltipList.add(Component.translatable("tooltip.experienceobelisk.molecular_metamorpher.bound"));

            tooltipList.add(Component.translatable("tooltip.experienceobelisk.molecular_metamorpher.xp",
                    Component.literal(String.valueOf(points)).withStyle(ChatFormatting.GREEN)));

            if(mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2){
                renderTooltip(new PoseStack(), tooltipList, Optional.empty(), mouseX, mouseY);
            }
        }

        clearWidgets();
        loadWidgetElements();

        //render settings button
        for(Widget widget : this.renderables){
            widget.render(poseStack, mouseX, mouseY, partialTick);
        }

        super.render(poseStack, mouseX, mouseY, partialTick);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    private void loadWidgetElements(){
        if(!this.buttons.isEmpty()){
            for(Button b : this.buttons){
                addRenderableWidget(b);
            }
        }
    }

    private final List<Button> buttons = new ArrayList<>();
    private void setupWidgetElements() {

        buttons.clear();

        Button settings = new Button(this.width / 2 + 91, this.height / 2 - 78, 20, 20,
                Component.translatable("button.experienceobelisk.experience_obelisk.settings"),
                (onPress) ->
                        Minecraft.getInstance().setScreen(new MolecularMetamorpherOptionsScreen(menu)),
                (button, poseStack, mouseX, mouseY) ->
                        renderTooltip(poseStack, Component.translatable("tooltip.experienceobelisk.experience_obelisk.settings"), mouseX, mouseY)

        );

        buttons.add(settings);

    }

}
