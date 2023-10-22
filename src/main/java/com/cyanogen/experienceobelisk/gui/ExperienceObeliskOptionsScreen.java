package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import com.cyanogen.experienceobelisk.network.PacketHandler;
import com.cyanogen.experienceobelisk.network.experienceobelisk.XPObeliskUpdateRadius;
import com.cyanogen.experienceobelisk.network.experienceobelisk.XPObeliskUpdateRedstone;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ExperienceObeliskOptionsScreen extends AbstractContainerScreen<ExperienceObeliskMenu> {

    public BlockPos pos;
    public ExperienceObeliskEntity xpobelisk;
    public ExperienceObeliskMenu menu;

    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/container/dark_bg2.png");

    protected ExperienceObeliskOptionsScreen(BlockPos pos, ExperienceObeliskMenu menu) {
        super(menu, menu.inventory, new TextComponent("Experience Obelisk"));
        this.pos = pos;
        this.xpobelisk = menu.entity;
        this.menu = menu;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(pKeyCode, pScanCode);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        else{
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

        renderBackground(pPoseStack);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        int textureWidth = 256;
        int textureHeight = 256;
        int x = this.width / 2 - 176 / 2;
        int y = this.height / 2 - 166 / 2;


        //render gui texture
        blit(pPoseStack, x, y, 0, 0, 176, 166, textureWidth, textureHeight);

        //widgets
        setupWidgetElements();

        //descriptors & info
        drawCenteredString(new PoseStack(), this.font, new TranslatableComponent("title.experienceobelisk.experience_obelisk.settings"),
                this.width / 2,this.height / 2 - 76, 0xFFFFFF);
        drawString(new PoseStack(), this.font, new TranslatableComponent("title.experienceobelisk.experience_obelisk.radius"),
                this.width / 2 - 77,this.height / 2 - 56, 0xFFFFFF);
        drawString(new PoseStack(), this.font, new TranslatableComponent("title.experienceobelisk.experience_obelisk.redstone"),
                this.width / 2 - 77,this.height / 2 - 10, 0xFFFFFF);


        //render widgets
        for(Widget widget : this.renderables) {
            widget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

    }

    @Override
    protected void renderBg(PoseStack p_97787_, float p_97788_, int p_97789_, int p_97790_) {

    }


    private void setupWidgetElements(){

        clearWidgets();
        int w = 50; //width (divisible by 2)
        int h = 20; //height
        int s = 2; //spacing
        int y1 = 43;
        int y2 = -3;
        int y3 = -49;

        Style green = Style.EMPTY.withColor(0x45FF5B);
        Style red = Style.EMPTY.withColor(0xFF454B);
        double radius = xpobelisk.getRadius();

        TranslatableComponent status;
        if(xpobelisk.isRedstoneEnabled()){
            status = new TranslatableComponent("button.experienceobelisk.experience_obelisk.enabled");
        }
        else{
            status = new TranslatableComponent("button.experienceobelisk.experience_obelisk.ignored");
        }

        //decrease radius
        addRenderableWidget(new Button(this.width / 2 - 56, this.height / 2 - y1, 26, h,
                new TextComponent("-").setStyle(red),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new XPObeliskUpdateRadius(pos, -0.5))));

        //reset radius
        addRenderableWidget(new Button(this.width / 2 - 25, this.height / 2 - y1, 50, h,
                new TextComponent(String.valueOf(radius)),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new XPObeliskUpdateRadius(pos, 0)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.radius"), pMouseX, pMouseY)
        ));

        //increase radius
        addRenderableWidget(new Button(this.width / 2 + 30, this.height / 2 - y1, 26, h,
                new TextComponent("+").setStyle(green),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new XPObeliskUpdateRadius(pos, 0.5))));

        //toggle redstone
        addRenderableWidget(new Button(this.width / 2 - 25, this.height / 2 - y2, w, h, status, onPress -> {

            if (status.getKey().equals("button.experienceobelisk.experience_obelisk.ignored")) {
                PacketHandler.INSTANCE.sendToServer(new XPObeliskUpdateRedstone(pos, true));
            } else {
                PacketHandler.INSTANCE.sendToServer(new XPObeliskUpdateRedstone(pos, false));
            }

        }));

        //go back
        addRenderableWidget(new Button(this.width / 2 + 91, this.height / 2 - 78, 20, 20,
                new TranslatableComponent("button.experienceobelisk.experience_obelisk.back"),

                (onPress) ->
                        Minecraft.getInstance().setScreen(new ExperienceObeliskScreen(this.menu)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.back"), pMouseX, pMouseY)));


    }

}
