package com.cyanogen.experienceobelisk.gui;

import com.cyanogen.experienceobelisk.block_entities.LaserTransfiguratorEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LaserTransfiguratorScreen extends AbstractContainerScreen<LaserTransfiguratorMenu>{

    private final ResourceLocation texture = new ResourceLocation("experienceobelisk:textures/gui/screens/laser_transfigurator.png");
    public LaserTransfiguratorEntity transfigurator;
    private final Component title = Component.translatable("title.experienceobelisk.laser_transfigurator");
    private final Component inventoryTitle = Component.translatable("title.experienceobelisk.precision_dispeller.inventory");

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
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF);
        gui.drawString(this.font, this.inventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFFFFFF);
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
                this.width / 2 + 30,this.height / 2 - 5, 0xFFFFFF);

        super.render(gui, mouseX, mouseY, partialTick);
        this.renderTooltip(gui, mouseX, mouseY);
    }
}
