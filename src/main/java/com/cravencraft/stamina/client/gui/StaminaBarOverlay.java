package com.cravencraft.stamina.client.gui;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.client.ClientStaminaData;
import com.cravencraft.stamina.config.ClientConfigs;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static com.cravencraft.stamina.registries.AttributeRegistry.MAX_STAMINA;

public class StaminaBarOverlay implements LayeredDraw.Layer {
    private static final StaminaBarOverlay instance = new StaminaBarOverlay();
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(SimpleStamina.MODID, "textures/gui/icons.png");

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

    static final int DEFAULT_IMAGE_WIDTH = 98;
    static final int XP_IMAGE_WIDTH = 188;
    static final int IMAGE_HEIGHT = 21;
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

        if (!shouldShowStaminaBar(player)) {
            return;
        }



        int configOffsetX = ClientConfigs.STAMINA_BAR_X_OFFSET.get();
        int configOffsetY = ClientConfigs.STAMINA_BAR_Y_OFFSET.get();
        Anchor anchor = ClientConfigs.STAMINA_BAR_ANCHOR.get();

        int maxStamina = (int) player.getAttributeValue(MAX_STAMINA);
        int stamina = ClientStaminaData.getStamina();
        int barX = getBarX(anchor, screenWidth) + configOffsetX;
        int barY = getBarY(anchor, screenHeight, Minecraft.getInstance().gui) - configOffsetY;

        int imageWidth = DEFAULT_IMAGE_WIDTH;
        int spriteX = 0;
        int spriteY = 0;

        guiGraphics.blit(TEXTURE, barX, barY, spriteX, spriteY, imageWidth, IMAGE_HEIGHT, 256, 256);
        guiGraphics.blit(TEXTURE, barX, barY, spriteX, spriteY + IMAGE_HEIGHT, (int) (imageWidth * Math.min((stamina / (double) maxStamina), 1)), IMAGE_HEIGHT);

        if (ClientConfigs.STAMINA_BAR_TEXT_VISIBLE.get()) {
            int textX = ClientConfigs.STAMINA_TEXT_X_OFFSET.get() + barX + imageWidth / 2 - (int) ((("" + stamina).length() + 0.5) * CHAR_WIDTH);
            int textY = ClientConfigs.STAMINA_TEXT_Y_OFFSET.get() + barY + ICON_ROW_HEIGHT;
            String staminaFraction = (stamina) + "/" + maxStamina;

            guiGraphics.drawString(Minecraft.getInstance().font, staminaFraction, textX, textY, TEXT_COLOR);
        }
    }

    private static void drawStaminaBarText() {

    }

    public static boolean shouldShowStaminaBar(LocalPlayer player) {
        var display = ClientConfigs.STAMINA_BAR_DISPLAY.get();

        return !player.isSpectator() && display != Display.Never &&
                (display == Display.Always || ClientStaminaData.getStamina() < player.getAttributeValue(MAX_STAMINA));
    }

    public boolean doNotRenderGui(LocalPlayer player) {
        return Minecraft.getInstance().options.hideGui || player.isSpectator();
    }

    private static int getBarX(Anchor anchor, int screenWidth) {
        if (anchor == Anchor.Center || anchor == Anchor.Hunger) {
            return screenWidth / 2 - DEFAULT_IMAGE_WIDTH / 2 + (anchor == Anchor.Center ? 0 : HUNGER_BAR_OFFSET);
        }
        else if (anchor == Anchor.TopLeft || anchor == Anchor.BottomLeft) {
            return SCREEN_BORDER_MARGIN;
        }
        else return screenWidth - SCREEN_BORDER_MARGIN - DEFAULT_IMAGE_WIDTH;
    }

    private static int getBarY(Anchor anchor, int screenHeight, Gui gui) {
        if (anchor == Anchor.Hunger) {
            return screenHeight - (getAndIncrementRightHeight(gui) - 2) - IMAGE_HEIGHT / 2;
        }
        if (anchor == Anchor.Center) {
            return screenHeight - HOTBAR_HEIGHT - (int) (ICON_ROW_HEIGHT * 2.5f) - IMAGE_HEIGHT / 2 - (Math.max(gui.rightHeight, gui.leftHeight) - 49);
        }
        if (anchor == Anchor.TopLeft || anchor == Anchor.TopRight){
            return SCREEN_BORDER_MARGIN;
        }

        return screenHeight - SCREEN_BORDER_MARGIN - IMAGE_HEIGHT;
    }

    private static int getAndIncrementRightHeight(Gui gui) {
        int x = gui.rightHeight;
        gui.rightHeight += 10;
        return x;
    }
}
