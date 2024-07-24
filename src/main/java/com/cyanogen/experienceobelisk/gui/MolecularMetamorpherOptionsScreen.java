package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.MolecularMetamorpherEntity;
import com.cyanogen.experienceobelisk.network.PacketHandler;
import com.cyanogen.experienceobelisk.network.shared.UpdateRedstone;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MolecularMetamorpherOptionsScreen extends Screen{

    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/experience_obelisk.png");
    private final MolecularMetamorpherMenu menu;
    private final MolecularMetamorpherEntity metamorpher;
    private final BlockPos pos;

    public MolecularMetamorpherOptionsScreen(MolecularMetamorpherMenu menu) {
        super(menu.component);
        this.menu = menu;
        this.metamorpher = menu.metamorpherClient;
        this.pos = menu.pos;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public Component getTitle() {
        return Component.empty();
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

        //descriptors & info
        drawCenteredString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.settings"),
                this.width / 2,this.height / 2 - 76, 0xFFFFFF);
        drawString(new PoseStack(), this.font, Component.translatable("title.experienceobelisk.experience_obelisk.redstone"),
                this.width / 2 - 77,this.height / 2 - 56, 0xFFFFFF);

        //render widgets
        clearWidgets();
        if(metamorpher.isRedstoneEnabled()){
            buttons.get(1).setMessage(Component.translatable("button.experienceobelisk.experience_obelisk.enabled"));
        }
        else{
            buttons.get(1).setMessage(Component.translatable("button.experienceobelisk.experience_obelisk.ignored"));
        }
        loadWidgetElements();


        for(Widget widget : this.renderables) {
            widget.render(poseStack, mouseX, mouseY, partialTick);
        }

        super.render(poseStack, mouseX, mouseY, partialTick);
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

        int w = 50;
        int h = 20;
        int y1 = 43;

        Button back = new Button(this.width / 2 + 91, this.height / 2 - 78, 20, 20,
                Component.translatable("button.experienceobelisk.experience_obelisk.back"),
                (onPress) ->
                        Minecraft.getInstance().setScreen(new MolecularMetamorpherScreen(menu, menu.inventory, menu.component)),
                (button, poseStack, mouseX, mouseY) ->
                        renderTooltip(poseStack, Component.translatable("tooltip.experienceobelisk.experience_obelisk.back"), mouseX, mouseY)

        );

        Button toggleRedstone = new Button(this.width / 2 - 25, this.height / 2 - y1, w, h,
                Component.empty(),
                (onPress) -> {
                    if (!metamorpher.isRedstoneEnabled()) {
                        PacketHandler.INSTANCE.sendToServer(new UpdateRedstone(pos, true));
                    } else {
                        PacketHandler.INSTANCE.sendToServer(new UpdateRedstone(pos, false));
                    }
        });

        buttons.add(back);
        buttons.add(toggleRedstone);
    }
}
