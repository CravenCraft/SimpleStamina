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

import static com.cravencraft.stamina.capability.StaminaData.SEGMENT_STAMINA_AMOUNT;
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


    static final int IMAGE_WIDTH = 256;
    static final int IMAGE_HEIGHT = 256;

    static final int STAMINA_BAR_START_POS_X = 0;
    static final int STAMINA_BAR_START_POS_Y = 0;
    static final int STAMINA_BAR_HEIGHT = 13;
    static final int FIRST_STAMINA_BAR_WIDTH = 31;
    static final int MIDDLE_STAMINA_BAR_WIDTH = 28;

    static final int FINAL_STAMINA_BAR_WIDTH = 30;

    static final int STAMINA_GAUGE_START_POS_X = 0;
    static final int STAMINA_GAUGE_START_POS_Y = 26;


    // Simplified Stamina Bar position information
    static final int SIMPLIFIED_STAMINA_BAR_WIDTH = 13;
    static final int SIMPLIFIED_STAMINA_BAR_TOP_POS_X = 0;
    static final int SIMPLIFIED_STAMINA_BAR_TOP_POS_Y = 0;
    static final int SIMPLIFIED_STAMINA_BAR_TOP_HEIGHT = 4;
    static final int SIMPLIFIED_STAMINA_BAR_MIDDLE_POS_X = 0;
    static final int SIMPLIFIED_STAMINA_BAR_MIDDLE_POS_Y = 6;
    static final int SIMPLIFIED_STAMINA_BAR_MIDDLE_HEIGHT = 5;
    static final int SIMPLIFIED_STAMINA_BAR_BOTTOM_POS_X = 0;
    static final int SIMPLIFIED_STAMINA_BAR_BOTTOM_POS_Y = 13;
    static final int SIMPLIFIED_STAMINA_BAR_BOTTOM_HEIGHT = 2;

    static final int SIMPLIFIED_STAMINA_GAUGE_START_POS_X = 0;
    static final int SIMPLIFIED_STAMINA_GAUGE_START_POS_Y = 17;
    static final int SIMPLIFIED_STAMINA_GAUGE_WIDTH = 9;
    static final int SIMPLIFIED_STAMINA_GAUGE_HEIGHT = 5;

    // Detailed Stamina Bar position information
    static final int DETAILED_STAMINA_BAR_HEIGHT = 17;
    static final int DETAILED_STAMINA_BAR_END_POS_X = 19;
    static final int DETAILED_STAMINA_BAR_END_POS_Y = 24;
    static final int DETAILED_STAMINA_BAR_END_WIDTH = 4;
    static final int DETAILED_STAMINA_BAR_MIDDLE_POS_X = 5;
    static final int DETAILED_STAMINA_BAR_MIDDLE_POS_Y = 24;
    static final int DETAILED_STAMINA_BAR_MIDDLE_WIDTH = 12;
    static final int DETAILED_STAMINA_BAR_BEGINNING_POS_X = 0;
    static final int DETAILED_STAMINA_BAR_BEGINNING_POS_Y = 24;
    static final int DETAILED_STAMINA_BAR_BEGINNING_WIDTH = 3;

    static final int DETAILED_STAMINA_GAUGE_START_POS_X = 0;
    static final int DETAILED_STAMINA_GAUGE_START_POS_Y = 43;
    static final int DETAILED_STAMINA_GAUGE_WIDTH = 12;
    static final int DETAILED_STAMINA_GAUGE_HEIGHT = 9;

    static final int STAMINA_GAUGE_WIDTH = 25;
    static final int STAMINA_GAUGE_HEIGHT = 5;

    static final int STAMINA_GAUGE_CONTAINER_WIDTH = 27;
    static final int STAMINA_GAUGE_CONTAINER_HEIGHT = 7;

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

        // TODO: Set this up properly.
//        if (!shouldShowStaminaBar(player, stamina)) {
//            return;
//        }

        int configOffsetX = ClientConfigs.STAMINA_BAR_X_OFFSET.get();
        int configOffsetY = ClientConfigs.STAMINA_BAR_Y_OFFSET.get();
        Anchor anchor = ClientConfigs.STAMINA_BAR_ANCHOR.get();

        int totalMaxStamina = (int) player.getAttributeValue(MAX_STAMINA);
        int currentMaxStamina = ClientStaminaManager.getClientStaminaData().getMaxStamina();
        int barX = getBarX(anchor, screenWidth) + configOffsetX;
        int barY = getBarY(anchor, screenHeight, Minecraft.getInstance().gui) - configOffsetY;

        int imageWidth = FIRST_STAMINA_BAR_WIDTH;
        int imageHeight = STAMINA_BAR_HEIGHT;
        guiGraphics.pose().pushPose();
//        guiGraphics.pose().scale(0.5f, 0.5f, 0.5f);
        // TODO: For when I return from the weekend trip.
        //       Implement the simplified and detailed gauges based off of these new methods and the old one (at least the detailed one. Simplified will require new max stamina decrease feature).
        //       Adjust the gauges to be in a better part of the screen that is less in the way. Maybe top-left is the best if I'm going to have the detailed gauge shoot out of the simplified one when stamina is drained?
        //       Have the simplified gauge always show, and the detailed gauge only show when actively draining stamina. Have it be animated and shoot out of the simplified gauge.
        //       Have some feedback to the player when they try to drain stamina and have none.
        //       Create a pulsating exclamation mark or something to indicate when the player is low on stamina (< 25% remaining), and they're using stamina.
        drawSimplifiedStaminaBar(guiGraphics, totalMaxStamina / 25, barX, barY);
        drawDetailedStaminaBar(guiGraphics, totalMaxStamina / 25, barX + 11, barY - DETAILED_STAMINA_BAR_HEIGHT);

        // Want 100% green at 100 stamina
        // Have red increase from 0 -> 50% at 75 stamina
        // Red and green at 100% at 50 stamina
        // Green drops to 50% at 25 stamina
        // Green is 0% and red 100% at 0 stamina
        float minColorValue = 0.25f;
        float offsetConstant = 0.015f;
        // TODO: Want to make 75 stamina to be a bit more yellow, halfway to be a bit more orange, and 25 to be more red.
        float halfMaxStamina = totalMaxStamina / 2.0f;
        float green = (stamina > halfMaxStamina) ? 1.0f : (stamina * offsetConstant) + minColorValue;
        float red = (stamina > halfMaxStamina) ? ((totalMaxStamina - stamina) * offsetConstant) + minColorValue : 1.0f;
        float blue = (stamina > halfMaxStamina) ? (stamina - halfMaxStamina) / totalMaxStamina : minColorValue;

        // 100% = r: 0.25, g: 1.0, b: 0.5
        // 50% = r: 1.0, g: 1.0, b: 0.25
        // 0% = r: 1.0, g: 0.25, b: 0.25

        guiGraphics.setColor(red, green, blue, 1.0f);
        fillDetailedStaminaBar(guiGraphics, stamina, currentMaxStamina, barX + 14, barY - 13);

        int simpleStaminaGauges = currentMaxStamina / SEGMENT_STAMINA_AMOUNT;

        // TODO: Working as expected, but want to modify these values a bit as well as the detailed one some.
        //       Want the 3 gauge to be a bit more yellow, the 2 gauge to slightly more orange, and the
        //       1 gauge is fine.
        switch (simpleStaminaGauges) {
            case 4 -> {
                red = 0.25f;
                green = 1.0f;
                blue = 0.5f;
            }
            case 3 -> {
                red = 0.5f;
                green = 1.0f;
                blue = 0.5f;
            }
            case 2 -> {
                red = 1.0f;
                green = 1.0f;
                blue = 0.25f;
            }
            case 1 -> {
                red = 1.0f;
                green = 0.25f;
                blue = 0.25f;
            }
            case 0 -> {
                red = 1.0f;
                green = 0.0f;
                blue = 0.0f;
            }

            default -> SimpleStamina.LOGGER.error("ERROR: Stamina gauges are not within the bound limits.");

        }

        guiGraphics.setColor(red, green, blue, 1.0f);
        fillSimplifiedStaminaBar(guiGraphics, currentMaxStamina,barX + 2, barY - 5);

        guiGraphics.pose().popPose();

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        // TODO: If I keep this, then I need to modify the total stamina here to show the CURRENT total stamina and not
        //      total in general. I don't want to modify the variable itself above since that dictates the gui height and width,
        //      but I just want to modify it after this if statement to show the total current.
        if (ClientConfigs.STAMINA_BAR_TEXT_VISIBLE.get()) {
            int textX = ClientConfigs.STAMINA_TEXT_X_OFFSET.get() + barX + imageWidth / 2 - (int) ((("" + stamina).length() + 0.5) * CHAR_WIDTH);
            int textY = ClientConfigs.STAMINA_TEXT_Y_OFFSET.get() + barY + ICON_ROW_HEIGHT;
            String staminaFraction = (stamina) + "/" + totalMaxStamina;
//            guiGraphics.pose().pushPose();
            guiGraphics.drawString(Minecraft.getInstance().font, staminaFraction, textX, textY, TEXT_COLOR);
        }
    }

    // Draws the simplified stamina bar for the player. TODO: Add a more detailed explanation later.
    private static void drawSimplifiedStaminaBar(GuiGraphics guiGraphics, int numberOfIncrements, int guiStartPosX, int guiStartPosY) {

        // Render the bottom of the simplified stamina bar.
        customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, SIMPLIFIED_STAMINA_BAR_BOTTOM_POS_X, SIMPLIFIED_STAMINA_BAR_BOTTOM_POS_Y, SIMPLIFIED_STAMINA_BAR_WIDTH, SIMPLIFIED_STAMINA_BAR_BOTTOM_HEIGHT);

        // Render the middle segments of the simplified stamina bar. Should be one segment for each 25 points of stamina (will potentially configure this later).
        for (int i = 0; i < numberOfIncrements; i++) {
            guiStartPosY -= (SIMPLIFIED_STAMINA_BAR_MIDDLE_HEIGHT);
            customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, SIMPLIFIED_STAMINA_BAR_MIDDLE_POS_X, SIMPLIFIED_STAMINA_BAR_MIDDLE_POS_Y, SIMPLIFIED_STAMINA_BAR_WIDTH, SIMPLIFIED_STAMINA_BAR_MIDDLE_HEIGHT);

        }

        guiStartPosY -= (SIMPLIFIED_STAMINA_BAR_TOP_HEIGHT);

        // Render the top of the simplified stamina bar after all middle segments have been rendered.
        customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, SIMPLIFIED_STAMINA_BAR_TOP_POS_X, SIMPLIFIED_STAMINA_BAR_TOP_POS_Y, SIMPLIFIED_STAMINA_BAR_WIDTH, SIMPLIFIED_STAMINA_BAR_TOP_HEIGHT);

    }

    // Draws the gauges and the fill for them based on the number of stamina increments that the player has. TODO: Change description.
    private static void fillSimplifiedStaminaBar(GuiGraphics guiGraphics, int currentMaxStamina, int guiStartPosX, int guiStartPosY) {
        int staminaPerSegment = 25;
        // TODO: This should not use "maxStamina". It should use the current max or whatever I end up using as that.
        int numberOfIncrements = currentMaxStamina / staminaPerSegment;


        for (int i = 0; i < numberOfIncrements; i++) {
            customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, SIMPLIFIED_STAMINA_GAUGE_START_POS_X, SIMPLIFIED_STAMINA_GAUGE_START_POS_Y, SIMPLIFIED_STAMINA_GAUGE_WIDTH, SIMPLIFIED_STAMINA_GAUGE_HEIGHT);
            guiStartPosY -= (SIMPLIFIED_STAMINA_GAUGE_HEIGHT);
        }
    }

    // Draws the detailed stamina bar for the player. TODO: Add a more detailed explanation later.
    private static void drawDetailedStaminaBar(GuiGraphics guiGraphics, int numberOfIncrements, int guiStartPosX, int guiStartPosY) {

        // Render the beginning of the detailed stamina bar.
        customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, DETAILED_STAMINA_BAR_BEGINNING_POS_X, DETAILED_STAMINA_BAR_BEGINNING_POS_Y, DETAILED_STAMINA_BAR_BEGINNING_WIDTH, DETAILED_STAMINA_BAR_HEIGHT);

        guiStartPosX += (DETAILED_STAMINA_BAR_BEGINNING_WIDTH);
        // Render the middle segments of the detailed stamina bar. Should be one segment for each 25 points of stamina (will potentially configure this later).
        for (int i = 0; i < numberOfIncrements; i++) {
            customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, DETAILED_STAMINA_BAR_MIDDLE_POS_X, DETAILED_STAMINA_BAR_MIDDLE_POS_Y, DETAILED_STAMINA_BAR_MIDDLE_WIDTH, DETAILED_STAMINA_BAR_HEIGHT);
            guiStartPosX += (DETAILED_STAMINA_BAR_MIDDLE_WIDTH - 1);

        }

//        // Render the end of the detailed stamina bar after all middle segments have been rendered.
        customGraphicsRenderer(guiGraphics, guiStartPosX + 1, guiStartPosY, DETAILED_STAMINA_BAR_END_POS_X, DETAILED_STAMINA_BAR_END_POS_Y, DETAILED_STAMINA_BAR_END_WIDTH, DETAILED_STAMINA_BAR_HEIGHT);
    }

    // Draws the gauges and the fill for them based on the number of stamina increments that the player has. TODO: Change description.
    // TODO: This could potentially cause issues with the increments. Need to have all increments and staminaPerSegment set to constants like 25.
    private static void fillDetailedStaminaBar(GuiGraphics guiGraphics, int stamina, int maxStamina, int guiStartPosX, int guiStartPosY) {
        int staminaPerSegment = 25;
        int numberOfIncrements = maxStamina / staminaPerSegment;

//        SimpleStamina.LOGGER.info("Current stamina: {}", stamina);
        for (int i = 0; i < numberOfIncrements; i++) {
//            SimpleStamina.LOGGER.info("Iteration: {}", i);
//            int j = numberOfIncrements - i;m
            int segmentMaxStamina = staminaPerSegment * (i + 1);
            int segmentMinStamina = segmentMaxStamina - staminaPerSegment;
//            SimpleStamina.LOGGER.info("segment min stamina: {} | segment max stamina: {}", segmentMinStamina, segmentMaxStamina);

            if (stamina >= segmentMinStamina) {
//                int gaugePercentFilled = segmentMaxStamina - stamina;
                int gaugePixelsFilled = (int) (DETAILED_STAMINA_GAUGE_WIDTH * Math.min(((double) (stamina - segmentMinStamina) / staminaPerSegment), 1));

//                SimpleStamina.LOGGER.info("stamina: {} | stamina per segment: {}", stamina, staminaPerSegment);
//                SimpleStamina.LOGGER.info("stamina over stamina per segment: {}", ((double) stamina / staminaPerSegment));
//                SimpleStamina.LOGGER.info("math min of that and 1: {}", Math.min((stamina / staminaPerSegment), 1));
//                SimpleStamina.LOGGER.info("stamina gauge width: {}", STAMINA_GAUGE_WIDTH);
//                SimpleStamina.LOGGER.info("gauge percentage filled: {}", gaugePixelsFilled);
                customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, DETAILED_STAMINA_GAUGE_START_POS_X, DETAILED_STAMINA_GAUGE_START_POS_Y, gaugePixelsFilled, DETAILED_STAMINA_GAUGE_HEIGHT);
//                guiStartPosX += (STAMINA_GAUGE_WIDTH + padding);
            }
            guiStartPosX += (DETAILED_STAMINA_GAUGE_WIDTH - 1);

//            int staminaGaugeMax = (int) (STAMINA_GAUGE_WIDTH * Math.min((stamina / (double) (maxStamina / j)), 1));
//            customGraphicsRenderer(guiGraphics, guiStartPosX, guiStartPosY, imgStartPosX, imgStartPosY, staminaGaugeMax, STAMINA_BAR_HEIGHT);
//            guiStartPosX += (STAMINA_GAUGE_WIDTH + padding);
        }
    }

    private static void customGraphicsRenderer(GuiGraphics guiGraphics, float barMinX, float barMinY, float textureOffsetX, float textureOffsetY, float textureWidth, float textureHeight) {
        var barMaxX = barMinX + textureWidth;
        var barMaxY = barMinY + textureHeight;
        var minSpriteX = (textureOffsetX + 0.0F) / IMAGE_WIDTH;
        var minSpriteY = (textureOffsetY + 0.0F) / IMAGE_HEIGHT;
        var maxSpriteX = (textureOffsetX + textureWidth) / IMAGE_WIDTH;
        var maxSpriteY = (textureOffsetY + textureHeight) / IMAGE_HEIGHT;

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, barMinX, barMinY, 0).setColor(255, 1, 1, 100).setUv(minSpriteX, minSpriteY);
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
            return screenWidth / 2 - FIRST_STAMINA_BAR_WIDTH / 2 + (anchor == Anchor.Center ? 0 : HUNGER_BAR_OFFSET);
        }
        else if (anchor == Anchor.TopLeft || anchor == Anchor.BottomLeft) {
            return SCREEN_BORDER_MARGIN;
        }
        else return screenWidth - SCREEN_BORDER_MARGIN - FIRST_STAMINA_BAR_WIDTH;
    }

    private static int getBarY(Anchor anchor, int screenHeight, Gui gui) {
        if (anchor == Anchor.Hunger) {
            return screenHeight - (getAndIncrementRightHeight(gui) - 2) - STAMINA_BAR_HEIGHT / 2;
        }
        if (anchor == Anchor.Center) {
            return screenHeight - HOTBAR_HEIGHT - (int) (ICON_ROW_HEIGHT * 2.5f) - STAMINA_BAR_HEIGHT / 2 - (Math.max(gui.rightHeight, gui.leftHeight) - 49);
        }
        if (anchor == Anchor.TopLeft || anchor == Anchor.TopRight){
            return SCREEN_BORDER_MARGIN;
        }

        return screenHeight - SCREEN_BORDER_MARGIN - STAMINA_BAR_HEIGHT;
    }

    private static int getAndIncrementRightHeight(Gui gui) {
        int x = gui.rightHeight;
        gui.rightHeight += 10;
        return x;
    }
}
