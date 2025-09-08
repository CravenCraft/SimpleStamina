package com.cravencraft.stamina.client.gui;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.config.ClientConfigs;
import com.cravencraft.stamina.manager.ClientStaminaManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static com.cravencraft.stamina.registries.AttributeRegistry.MAX_STAMINA;

public class StaminaBarOverlay implements LayeredDraw.Layer {
    private static final StaminaBarOverlay instance = new StaminaBarOverlay();
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SimpleStamina.MODID, "textures/gui/stamina_bar.png");

    public enum Display {
        Never,
        Always,
        Contextual
    }

    public enum Anchor {
        Hunger,
        Center,
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight
    }


//    static final int DEFAULT_IMAGE_WIDTH = 98;
//    static final int IMAGE_HEIGHT = 21;

    static final int STAMINA_BAR_INCREMENT_WIDTH = 31;
    static final int STAMINA_BAR_INCREMENT_HEIGHT = 13;
    static final int XP_IMAGE_WIDTH = 188;

    static final int IMAGE_FILLED_STAMINA_WIDTH = 67;
    static final int IMAGE_FILLED_STAMINA_HEIGHT = 19;

    static final int HOTBAR_HEIGHT = 25;
    static final int ICON_ROW_HEIGHT = 11;
    static final int CHAR_WIDTH = 6;
    static final int HUNGER_BAR_OFFSET = 50;
    static final int SCREEN_BORDER_MARGIN = 20;
    static final int TEXT_COLOR = 14737632;

    public static StaminaBarOverlay getInstance() {
        return instance;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        var player = Minecraft.getInstance().player;
        if (player == null || doNotRenderGui(player)) {
            return;
        }

        var screenWidth = guiGraphics.guiWidth();
        var screenHeight = guiGraphics.guiHeight();

        int stamina = (int) ClientStaminaManager.getClientStaminaData().getStamina();
        if (!shouldShowStaminaBar(player, stamina)) {
            return;
        }



        int configOffsetX = ClientConfigs.STAMINA_BAR_X_OFFSET.get();
        int configOffsetY = ClientConfigs.STAMINA_BAR_Y_OFFSET.get();
        Anchor anchor = ClientConfigs.STAMINA_BAR_ANCHOR.get();

        int maxStamina = (int) player.getAttributeValue(MAX_STAMINA);
        int barX = getBarX(anchor, screenWidth) + configOffsetX;
        int barY = getBarY(anchor, screenHeight, Minecraft.getInstance().gui) - configOffsetY;

        int imageWidth = STAMINA_BAR_INCREMENT_WIDTH;
        int imageHeight = STAMINA_BAR_INCREMENT_HEIGHT;
        int spriteX = 0;
        int spriteY = 0;

        // TODO: So far, so good. Need to make a method for incrementing, and account for the beginning section, middle sections, and end section.
        //      By default, there will be one beginning, two middle, and one end. Each section should consist of 25 stamina points, or maybe somewhere later
        //      down the road a certain fraction of the player's total stamina. A simple for-loop could probably get most of what I want done with the exception
        //      of maybe the end. Will have to see as I work through it.
        customGraphicsRenderer(guiGraphics, barX, barY, spriteX, spriteY, imageWidth, imageHeight, 256, 256);
        guiGraphics.setColor(0.0f, 1.0f, 0.0f, 1.0f);
        // TODO: Probably want to modify the stamina bar that drains so that the last bit doesn't get removed until the user is at absolute 0 on stamina.
//        customGraphicsRenderer(guiGraphics, barX, barY, spriteX, spriteY + imageHeight, (int) (imageWidth * Math.min((stamina / (double) maxStamina), 1)), imageHeight,  256, 256);

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (ClientConfigs.STAMINA_BAR_TEXT_VISIBLE.get()) {
            int textX = ClientConfigs.STAMINA_TEXT_X_OFFSET.get() + barX + imageWidth / 2 - (int) ((("" + stamina).length() + 0.5) * CHAR_WIDTH);
            int textY = ClientConfigs.STAMINA_TEXT_Y_OFFSET.get() + barY + ICON_ROW_HEIGHT;
            String staminaFraction = (stamina) + "/" + maxStamina;
//            guiGraphics.pose().pushPose();
            guiGraphics.drawString(Minecraft.getInstance().font, staminaFraction, textX, textY, TEXT_COLOR);
        }
    }

    private static void customGraphicsRenderer(GuiGraphics guiGraphics, float barMinX, float barMinY, float textureOffsetX, float textureOffsetY, float textureWidth, float textureHeight, float textureTotalWidth, float textureTotalHeight) {
        var barMaxX = barMinX + textureWidth;
        var barMaxY = barMinY + textureHeight;
        var minSpriteX = (textureOffsetX + 0.0F) / textureTotalWidth;
        var minSpriteY = (textureOffsetY + 0.0F) / textureTotalHeight;
        var maxSpriteX = (textureOffsetX + textureWidth) / textureTotalWidth;
        var maxSpriteY = (textureOffsetY + textureHeight) / textureTotalHeight;

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, barMinX, barMinY, 0).setColor(255, 1, 1, 255).setUv(minSpriteX, minSpriteY);
        bufferBuilder.addVertex(matrix4f, barMinX, barMaxY, 0).setColor(255, 1, 1, 255).setUv(minSpriteX, maxSpriteY);
        bufferBuilder.addVertex(matrix4f, barMaxX, barMaxY, 0).setColor(255, 1, 1, 255).setUv(maxSpriteX, maxSpriteY);
        bufferBuilder.addVertex(matrix4f, barMaxX, barMinY, 0).setColor(255, 1, 1, 255).setUv(maxSpriteX, minSpriteY);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public static boolean shouldShowStaminaBar(LocalPlayer player, int stamina) {
        var display = ClientConfigs.STAMINA_BAR_DISPLAY.get();

        return !player.isSpectator() && display != Display.Never &&
                (display == Display.Always || stamina < player.getAttributeValue(MAX_STAMINA));
    }

    public boolean doNotRenderGui(LocalPlayer player) {
        return Minecraft.getInstance().options.hideGui || player.isSpectator();
    }

    private static int getBarX(Anchor anchor, int screenWidth) {
        if (anchor == Anchor.Center || anchor == Anchor.Hunger) {
            return screenWidth / 2 - STAMINA_BAR_INCREMENT_WIDTH / 2 + (anchor == Anchor.Center ? 0 : HUNGER_BAR_OFFSET);
        }
        else if (anchor == Anchor.TopLeft || anchor == Anchor.BottomLeft) {
            return SCREEN_BORDER_MARGIN;
        }
        else return screenWidth - SCREEN_BORDER_MARGIN - STAMINA_BAR_INCREMENT_WIDTH;
    }

    private static int getBarY(Anchor anchor, int screenHeight, Gui gui) {
        if (anchor == Anchor.Hunger) {
            return screenHeight - (getAndIncrementRightHeight(gui) - 2) - STAMINA_BAR_INCREMENT_HEIGHT / 2;
        }
        if (anchor == Anchor.Center) {
            return screenHeight - HOTBAR_HEIGHT - (int) (ICON_ROW_HEIGHT * 2.5f) - STAMINA_BAR_INCREMENT_HEIGHT / 2 - (Math.max(gui.rightHeight, gui.leftHeight) - 49);
        }
        if (anchor == Anchor.TopLeft || anchor == Anchor.TopRight){
            return SCREEN_BORDER_MARGIN;
        }

        return screenHeight - SCREEN_BORDER_MARGIN - STAMINA_BAR_INCREMENT_HEIGHT;
    }

    private static int getAndIncrementRightHeight(Gui gui) {
        int x = gui.rightHeight;
        gui.rightHeight += 10;
        return x;
    }
}
