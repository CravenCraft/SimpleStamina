package com.cravencraft.stamina.config;

import com.machinezoo.noexception.CheckedExceptionHandler;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfigs {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_SPRINT_STAMINA;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_SWIM_STAMINA;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_MELEE_ATTACK_STAMINA;
    public static final ModConfigSpec.ConfigValue<Double> STAMINA_REGEN_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> SPRINT_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> SWIM_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> JUMP_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> ATTACK_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> ATTACK_SPEED_REDUCTION_MULTIPLIER;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##   ATTENTION: These are server configs for gameplay settings                              ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##                                                                                          ##");
        BUILDER.comment("##############################################################################################");
        BUILDER.comment("");

        BUILDER.push("Toggle Stamina Actions");
        BUILDER.comment("Toggle on/off actions that will cost stamina to perform.");
        TOGGLE_SPRINT_STAMINA = BUILDER.define("toggleSprintStamina", true);
        TOGGLE_SWIM_STAMINA = BUILDER.define("toggleSwimStamina", true);
        TOGGLE_MELEE_ATTACK_STAMINA = BUILDER.define("toggleMeleeAttackStamina", true);
        BUILDER.pop();

        BUILDER.push("Multipliers");
        BUILDER.comment("Global multiplier to all players' stamina regeneration (will not affect above fields marked 'false'. Default: 1.0");
        STAMINA_REGEN_MULTIPLIER = BUILDER.define("staminaRegenMultiplier", 1.0D);
        BUILDER.comment("Global multipliers for all the actions a player does that requires stamina. Higher values means more stamina drained. Default: 1.0");
        SPRINT_STAMINA_MULTIPLIER = BUILDER.define("sprintStaminaMultiplier", 1.0D);
        SWIM_STAMINA_MULTIPLIER = BUILDER.define("swimStaminaMultiplier", 1.0D);
        JUMP_STAMINA_MULTIPLIER = BUILDER.define("jumpStaminaMultiplier", 1.0D);
        ATTACK_STAMINA_MULTIPLIER = BUILDER.define("attackStaminaMultiplier", 1.0D);
        BUILDER.comment("Global multiplier for the attack speed reduction whenever a player is out of stamina. Default 0.75D (75%).");
        ATTACK_SPEED_REDUCTION_MULTIPLIER = BUILDER.defineInRange("attackSpeedReductionMultiplier", 0.75D, 0.0D, 1.0D);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

//    public static void onConfigReload() {
//
//    }
}
