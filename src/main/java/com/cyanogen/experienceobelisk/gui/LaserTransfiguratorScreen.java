package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.LaserTransfiguratorEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LaserTransfiguratorScreen extends AbstractContainerScreen<LaserTransfiguratorMenu>{

    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/experience_obelisk.png");
    public LaserTransfiguratorEntity transfigurator;

    public LaserTransfiguratorScreen(LaserTransfiguratorMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.transfigurator = menu.transfigurator;
    }

    @Override
    protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int completionPercent = 0;
        if(transfigurator.getProcessTime() != 0){
            completionPercent = transfigurator.getProcessProgress() * 100 / transfigurator.getProcessTime();
        }

        //render background texture
        gui.blit(texture, x, y, 0, 0, 176, 166);

        gui.drawCenteredString(this.font, Component.literal(completionPercent + "%"),
                this.width / 2 + 20,this.height / 2 - 56, 0xFFFFFF);

        super.render(gui, mouseX, mouseY, partialTick);
        this.renderTooltip(gui, mouseX, mouseY);
    }
}
