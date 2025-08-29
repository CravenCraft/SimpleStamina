package com.cravencraft.stamina.config;

import com.cravencraft.stamina.client.gui.StaminaBarOverlay;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfigs {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Double> STAMINA_REGEN_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> SPRINT_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> SWIM_STAMINA_MULTIPLIER;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##   ATTENTION: These are server configs for gameplay settings  ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("");

        BUILDER.push("Multipliers");
        BUILDER.comment("Global multiplier to all players' stamina regeneration. Default: 1.0");
        STAMINA_REGEN_MULTIPLIER = BUILDER.define("staminaRegenMultiplier", 1.0D);
        BUILDER.comment("Global multipliers for all the actions a player does that requires stamina. Higher values means more stamina drained. Default: 1.0");
        SPRINT_STAMINA_MULTIPLIER = BUILDER.define("sprintStaminaMultiplier", 1.0D);
        SWIM_STAMINA_MULTIPLIER = BUILDER.define("swimStaminaMultiplier", 1.0D);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

//    public static void onConfigReload() {
//
//    }
}
