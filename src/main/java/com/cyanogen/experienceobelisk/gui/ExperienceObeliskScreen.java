package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import com.cyanogen.experienceobelisk.network.PacketHandler;
import com.cyanogen.experienceobelisk.network.experience_obelisk.UpdateContents;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.cyanogen.experienceobelisk.network.experience_obelisk.UpdateContents.Request.*;
import static com.cyanogen.experienceobelisk.utils.ExperienceUtils.levelsToXP;
import static com.cyanogen.experienceobelisk.utils.ExperienceUtils.xpToLevels;


public class ExperienceObeliskScreen extends AbstractContainerScreen<ExperienceObeliskMenu> {

    public BlockPos pos;
    public ExperienceObeliskEntity xpobelisk;
    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/experience_obelisk.png");

    public ExperienceObeliskScreen(ExperienceObeliskMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.pos = menu.pos;
        this.xpobelisk = menu.entity;
    }

    protected ExperienceObeliskScreen(ExperienceObeliskMenu menu) {
        this(menu, menu.inventory, new TextComponent("Experience Obelisk"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(keyCode, scanCode);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        else{
            return super.keyPressed(keyCode, scanCode, modifiers);
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {

        renderBackground(poseStack);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);

        int textureWidth = 256;
        int textureHeight = 256;
        int x = this.width / 2 - 176 / 2;
        int y = this.height / 2 - 166 / 2;

        int experiencePoints = menu.entity.getFluidAmount() / 20;

        int n = experiencePoints - levelsToXP(xpToLevels(experiencePoints)); //remaining xp
        int m = levelsToXP(xpToLevels(experiencePoints) + 1) - levelsToXP(xpToLevels(experiencePoints)); //xp for next level
        int p = n * 138 / m;

        //render gui texture
        blit(poseStack, x, y, 0, 0, 176, 166, textureWidth, textureHeight);

        //render xp bar
        blit(poseStack, this.width / 2 - 138 / 2, this.height / 2 + 50, 0, 169, 138, 5, textureWidth, textureHeight);
        blit(poseStack, this.width / 2 - 138 / 2, this.height / 2 + 50, 0, 173, p, 5, textureWidth, textureHeight);

        //descriptors & info
        drawCenteredString(new PoseStack(), this.font, new TranslatableComponent("title.experienceobelisk.experience_obelisk"),
                this.width / 2,this.height / 2 - 76, 0xFFFFFF);
        drawString(new PoseStack(), this.font, new TranslatableComponent("title.experienceobelisk.experience_obelisk.store"),
                this.width / 2 - 77,this.height / 2 - 56, 0xFFFFFF);
        drawString(new PoseStack(), this.font, new TranslatableComponent("title.experienceobelisk.experience_obelisk.retrieve"),
                this.width / 2 - 77,this.height / 2 - 10, 0xFFFFFF);
        drawCenteredString(new PoseStack(), this.font, experiencePoints * 20 + " mB",
                this.width / 2,this.height / 2 + 35, 0xFFFFFF);
        drawCenteredString(new PoseStack(), this.font, String.valueOf(xpToLevels(experiencePoints)),
                this.width / 2,this.height / 2 + 60, 0x4DFF12);

        //widgets
        setupWidgetElements();

        //render widgets
        for(Widget widget : this.renderables) {
            widget.render(poseStack, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {

    }

    //buttons and whatnot go here
    private void setupWidgetElements() {

        clearWidgets();

        Style style = Style.EMPTY;
        Style green = style.withColor(0x45FF5B);
        Style red = style.withColor(0xFF454B);
        int w = 50; //width (divisible by 2)
        int h = 20; //height
        int s = 2; //spacing
        int y1 = 43;
        int y2 = -3;

        addRenderableWidget(new Button(this.width / 2 + 91, this.height / 2 - 78, 20, 20,
                new TranslatableComponent("button.experienceobelisk.experience_obelisk.settings"),

                (onPress) ->
                        Minecraft.getInstance().setScreen(new ExperienceObeliskOptionsScreen(pos, menu)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.settings"), pMouseX, pMouseY)));


        //deposit

        addRenderableWidget(new Button((int) (this.width / 2 - 1.5 * w - s), this.height / 2 - y1, w, h,
                new TextComponent("+1").setStyle(green),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateContents(pos, 1, FILL)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.add1"), pMouseX, pMouseY)
        ));

        addRenderableWidget(new Button(this.width / 2 - w / 2, this.height / 2 - y1, w, h,
                new TextComponent("+10").setStyle(green),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateContents(pos, 10, FILL)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.add10"), pMouseX, pMouseY)
        ));

        addRenderableWidget(new Button((int) (this.width / 2 + 0.5 * w + s), this.height / 2 - y1, w, h,
                new TextComponent("+All").setStyle(green),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateContents(pos, 0, FILL_ALL)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.addAll"), pMouseX, pMouseY)
        ));


        //withdraw

        addRenderableWidget(new Button((int) (this.width / 2 - 1.5 * w - s), this.height / 2 - y2, w, h,
                new TextComponent("-1").setStyle(red),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateContents(pos, 1, DRAIN)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.drain1"), pMouseX, pMouseY)
        ));

        addRenderableWidget(new Button(this.width / 2 - w / 2, this.height / 2 - y2, w, h,
                new TextComponent("-10").setStyle(red),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateContents(pos, 10, DRAIN)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.drain10"), pMouseX, pMouseY)
        ));

        addRenderableWidget(new Button((int) (this.width / 2 + 0.5 * w + s), this.height / 2 - y2, w, h,
                new TextComponent("-All").setStyle(red),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateContents(pos, 0, DRAIN_ALL)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, new TranslatableComponent("tooltip.experienceobelisk.experience_obelisk.drainAll"), pMouseX, pMouseY)
        ));


    }

}
