package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import com.cyanogen.experienceobelisk.network.PacketHandler;
import com.cyanogen.experienceobelisk.network.experience_obelisk.UpdateRadius;
import com.cyanogen.experienceobelisk.network.shared.UpdateRedstone;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ExperienceObeliskOptionsScreen extends Screen {

    public BlockPos pos;
    public ExperienceObeliskEntity xpobelisk;
    public ExperienceObeliskMenu menu;

    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/experience_obelisk.png");

    protected ExperienceObeliskOptionsScreen(BlockPos pos, ExperienceObeliskMenu menu) {
        super(Component.literal("Experience Obelisk"));
        this.pos = pos;
        this.xpobelisk = menu.entity;
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        renderBackground(poseStack);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        int textureWidth = 256;
        int textureHeight = 256;
        int x = this.width / 2 - 176 / 2;
        int y = this.height / 2 - 166 / 2;


        //render gui texture
        blit(poseStack, x, y, 0, 0, 176, 166, textureWidth, textureHeight);

        //widgets
        clearWidgets();
        loadWidgetElements();

        //descriptors & info
        drawCenteredString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.settings"),
                this.width / 2,this.height / 2 - 76, 0xFFFFFF);
        drawString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.radius"),
                this.width / 2 - 77,this.height / 2 - 56, 0xFFFFFF);
        drawString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.redstone"),
                this.width / 2 - 77,this.height / 2 - 10, 0xFFFFFF);


        //render widgets
        for(Widget widget : this.renderables) {
            widget.render(poseStack, mouseX, mouseY, partialTick);
        }

    }

    private void loadWidgetElements(){
        if(!this.buttons.isEmpty()){
            for(Button b : this.buttons){
                addRenderableWidget(b);
            }
        }
    }

    private final List<Button> buttons = new ArrayList<>();
    private void setupWidgetElements(){

        buttons.clear();

        int w = 50; //width (divisible by 2)
        int h = 20; //height
        int y1 = 43;
        int y2 = -3;

        Style green = Style.EMPTY.withColor(0x45FF5B);
        Style red = Style.EMPTY.withColor(0xFF454B);
        double radius = xpobelisk.getRadius();
        boolean redstoneEnabled = xpobelisk.isRedstoneEnabled();

        MutableComponent status;
        if(redstoneEnabled){
            status = Component.translatable("button.experienceobelisk.experience_obelisk.enabled");
        }
        else{
            status = Component.translatable("button.experienceobelisk.experience_obelisk.ignored");
        }

        //decrease radius
        Button decreaseRadius = new Button(this.width / 2 - 56, this.height / 2 - y1, 26, h,
                Component.literal("-").setStyle(red),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateRadius(pos, -0.5)));

        //reset radius
        Button resetRadius = new Button(this.width / 2 - 25, this.height / 2 - y1, 50, h,
                Component.literal(String.valueOf(radius)),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateRadius(pos, 0)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, Component.translatable("tooltip.experienceobelisk.experience_obelisk.radius"), pMouseX, pMouseY));

        //increase radius
        Button increaseRadius = new Button(this.width / 2 + 30, this.height / 2 - y1, 26, h,
                Component.literal("+").setStyle(green),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateRadius(pos, 0.5)));

        //toggle redstone
        Button toggleRedstone = new Button(this.width / 2 - 25, this.height / 2 - y2, w, h, status, onPress -> {

            if (!redstoneEnabled) {
                PacketHandler.INSTANCE.sendToServer(new UpdateRedstone(pos, true));
            } else {
                PacketHandler.INSTANCE.sendToServer(new UpdateRedstone(pos, false));
            }});

        //go back
        Button back = new Button(this.width / 2 + 91, this.height / 2 - 78, 20, 20,
                Component.translatable("button.experienceobelisk.experience_obelisk.back"),

                (onPress) ->
                        Minecraft.getInstance().setScreen(new ExperienceObeliskScreen(this.menu)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, Component.translatable("tooltip.experienceobelisk.experience_obelisk.back"), pMouseX, pMouseY));

        buttons.add(decreaseRadius);
        buttons.add(resetRadius);
        buttons.add(increaseRadius);
        buttons.add(toggleRedstone);
        buttons.add(back);

    }

}
