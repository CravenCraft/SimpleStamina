package com.cravencraft.stamina.config;

import com.cravencraft.stamina.client.gui.StaminaBarOverlay;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfigs {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<StaminaBarOverlay.Display> STAMINA_BAR_DISPLAY;
    public static final ModConfigSpec.ConfigValue<StaminaBarOverlay.Anchor> STAMINA_BAR_ANCHOR;
    public static final ModConfigSpec.ConfigValue<Integer> STAMINA_BAR_X_OFFSET;
    public static final ModConfigSpec.ConfigValue<Integer> STAMINA_BAR_Y_OFFSET;
    public static final ModConfigSpec.ConfigValue<Integer> STAMINA_TEXT_X_OFFSET;
    public static final ModConfigSpec.ConfigValue<Integer> STAMINA_TEXT_Y_OFFSET;
    public static final ModConfigSpec.ConfigValue<Boolean> STAMINA_BAR_TEXT_VISIBLE;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##   ATTENTION: These are client configs. For gameplay settings, go to the SERVER CONFIGS   ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("");

        BUILDER.push("UI");
        BUILDER.push("Stamina Bar");
        BUILDER.comment("By default (Contextual), the stamina bar only appears when stamina is being consumed or regenerated.");
        STAMINA_BAR_DISPLAY = BUILDER.defineEnum("staminaBarDisplay", StaminaBarOverlay.Display.Contextual);
        BUILDER.comment("Used to adjust the stamina bar's position.");
        STAMINA_BAR_X_OFFSET = BUILDER.define("staminaBarXOffset", 0);
        STAMINA_BAR_Y_OFFSET = BUILDER.define("staminaBarYOffset", 0);
        STAMINA_BAR_TEXT_VISIBLE = BUILDER.define("staminaBarTextVisible", true);
        STAMINA_BAR_ANCHOR = BUILDER.defineEnum("staminaBarAnchor", StaminaBarOverlay.Anchor.Center);
        STAMINA_TEXT_X_OFFSET = BUILDER.define("staminaTextXOffset", 0);
        STAMINA_TEXT_Y_OFFSET = BUILDER.define("staminaTextYOffset", 0);
        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

//    public static void onConfigReload() {
//
//    }
}
