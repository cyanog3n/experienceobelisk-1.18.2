package com.cyanogen.experienceobelisk.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CognitiveAnvilScreen extends ItemCombinerScreen<CognitiveAnvilMenu> {

    private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");

    public CognitiveAnvilScreen(CognitiveAnvilMenu p_98901_, Inventory p_98902_, Component p_98903_) {
        super(p_98901_, p_98902_, p_98903_, ANVIL_LOCATION);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics p_281990_, int p_266822_, int p_267045_) {

    }

    //currently this is just a copy of the vanilla anvil screen class
    //however, changes will be made here once the new GUI is made


}