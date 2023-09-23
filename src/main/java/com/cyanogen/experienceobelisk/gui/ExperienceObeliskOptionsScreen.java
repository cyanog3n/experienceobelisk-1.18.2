package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.ExperienceObeliskEntity;
import com.cyanogen.experienceobelisk.network.PacketHandler;
import com.cyanogen.experienceobelisk.network.experience_obelisk.UpdateRadius;
import com.cyanogen.experienceobelisk.network.experience_obelisk.UpdateRedstone;
import com.mojang.blaze3d.platform.InputConstants;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExperienceObeliskOptionsScreen extends Screen {

    public Level level;
    public Player player;
    public BlockPos pos;
    public ExperienceObeliskEntity xpobelisk;

    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/experience_obelisk.png");

    protected ExperienceObeliskOptionsScreen(Level level, Player player, BlockPos pos, ExperienceObeliskScreen screen) {
        super(Component.literal("Experience Obelisk"));
        this.level = level;
        this.player = player;
        this.pos = pos;
        this.xpobelisk = screen.xpobelisk;
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

        if(player.position().distanceTo(Vec3.atCenterOf(pos)) > 7){
            this.onClose();
        }

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
        drawCenteredString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.settings"),
                this.width / 2,this.height / 2 - 76, 0xFFFFFF);
        drawString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.radius"),
                this.width / 2 - 77,this.height / 2 - 56, 0xFFFFFF);
        drawString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.redstone"),
                this.width / 2 - 77,this.height / 2 - 10, 0xFFFFFF);


        //render widgets
        for(Widget widget : this.renderables) {
            widget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

    }


    private void setupWidgetElements(){

        clearWidgets();
        int w = 50; //width (divisible by 2)
        int h = 20; //height
        int s = 2; //spacing
        int y1 = 43;
        int y2 = -3;

        Style green = Style.EMPTY.withColor(0x45FF5B);
        Style red = Style.EMPTY.withColor(0xFF454B);
        double radius = xpobelisk.getRadius();

        MutableComponent status;
        if(xpobelisk.isRedstoneEnabled()){
            status = Component.translatable("button.experienceobelisk.experience_obelisk.enabled");
        }
        else{
            status = Component.translatable("button.experienceobelisk.experience_obelisk.ignored");
        }


        //decrease radius
        addRenderableWidget(new Button(this.width / 2 - 56, this.height / 2 - y1, 26, h,
                Component.literal("-").setStyle(red),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateRadius(pos, -0.5))));

        //reset radius
        addRenderableWidget(new Button(this.width / 2 - 25, this.height / 2 - y1, 50, h,
                Component.literal(String.valueOf(radius)),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateRadius(pos, 0)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, Component.translatable("tooltip.experienceobelisk.experience_obelisk.radius"), pMouseX, pMouseY)
        ));

        //increase radius
        addRenderableWidget(new Button(this.width / 2 + 30, this.height / 2 - y1, 26, h,
                Component.literal("+").setStyle(green),

                (onPress) ->
                        PacketHandler.INSTANCE.sendToServer(new UpdateRadius(pos, 0.5))));

        //toggle redstone
        addRenderableWidget(new Button(this.width / 2 - 25, this.height / 2 - y2, w, h, status, onPress -> {

            if (!xpobelisk.isRedstoneEnabled()) {
                PacketHandler.INSTANCE.sendToServer(new UpdateRedstone(pos, true));
            } else {
                PacketHandler.INSTANCE.sendToServer(new UpdateRedstone(pos, false));
            }

        }));

        //go back
        addRenderableWidget(new Button(this.width / 2 + 91, this.height / 2 - 78, 20, 20,
                Component.translatable("button.experienceobelisk.experience_obelisk.back"),

                (onPress) ->
                        Minecraft.getInstance().setScreen(new ExperienceObeliskScreen(level, player, pos)),

                (pButton, pPoseStack, pMouseX, pMouseY) ->
                        renderTooltip(pPoseStack, Component.translatable("tooltip.experienceobelisk.experience_obelisk.back"), pMouseX, pMouseY)));


    }

}
