package com.cravencraft.stamina.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfigs {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<Integer> SERVER_TICK_RATE;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_SPRINT_STAMINA;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_SWIM_STAMINA;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_JUMP_STAMINA;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_MELEE_ATTACK_STAMINA;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_RANGED_ATTACK_STAMINA;
    public static final ModConfigSpec.ConfigValue<Boolean> TOGGLE_BLOCK_ATTACK_STAMINA;
    public static final ModConfigSpec.ConfigValue<Double> STAMINA_REGEN_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> SPRINT_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> SWIM_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> JUMP_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> ATTACK_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> PULL_BOW_STAMINA_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Double> BLOCK_STAMINA_REDUCTION_MULTIPLIER;
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

        BUILDER.push("Server Tick Rate");
        BUILDER.comment(
                """
                 Set the tick rate of the server. Larger tick increments = more choppy bar movement, but less network traffic.
                 Minecraft works at a 20 tick per second rate. So, a rate of 5 here would mean stamina is updated once every 5 ticks,
                 or every 1/4th of a second.
                """);
        SERVER_TICK_RATE = BUILDER.defineInRange("serverTickRate", 1, 1, 20);
        BUILDER.pop();

        BUILDER.push("Toggle Stamina Actions");
        BUILDER.comment("Toggle on/off actions that will cost stamina to perform.");
        TOGGLE_SPRINT_STAMINA = BUILDER.define("toggleSprintStamina", true);
        TOGGLE_SWIM_STAMINA = BUILDER.define("toggleSwimStamina", true);
        TOGGLE_JUMP_STAMINA = BUILDER.define("toggleJumpStamina", true);
        TOGGLE_MELEE_ATTACK_STAMINA = BUILDER.define("toggleMeleeAttackStamina", true);
        TOGGLE_RANGED_ATTACK_STAMINA = BUILDER.define("toggleRangedAttackStamina", true);
        TOGGLE_BLOCK_ATTACK_STAMINA = BUILDER.define("toggleBlockAttackStamina", true);


        BUILDER.push("Multipliers");
        BUILDER.comment("Global multiplier to all players' stamina regeneration (NOTE: will not affect above fields marked 'false').");
        STAMINA_REGEN_MULTIPLIER = BUILDER.defineInRange("staminaRegenMultiplier", 1.0D, 1.0D, 10.0D);
        BUILDER.comment("Global multipliers for all the actions a player does that requires stamina. Higher values means more stamina drained.");
        SPRINT_STAMINA_MULTIPLIER = BUILDER.defineInRange("sprintStaminaMultiplier", 1.0D, 1.0D, 10.0D);
        SWIM_STAMINA_MULTIPLIER = BUILDER.defineInRange("swimStaminaMultiplier", 1.0D, 1.0D, 10.0D);
        JUMP_STAMINA_MULTIPLIER = BUILDER.defineInRange("jumpStaminaMultiplier", 1.0D, 1.0D, 10.0D);
        ATTACK_STAMINA_MULTIPLIER = BUILDER.defineInRange("attackStaminaMultiplier", 1.0D, 1.0D, 10.0D);
        BUILDER.comment("For both the bow and crossbow.");
        PULL_BOW_STAMINA_MULTIPLIER = BUILDER.define("pullBowStaminaMultiplier", 1.0D);
        BLOCK_STAMINA_REDUCTION_MULTIPLIER = BUILDER.defineInRange("blockStaminaMultiplier", 1.0D, 0.0D, 1.0D);
        BUILDER.comment("Global multiplier for the attack speed reduction whenever a player is out of stamina. (Default 0.75D = 75% attack speed reduction).");
        ATTACK_SPEED_REDUCTION_MULTIPLIER = BUILDER.defineInRange("attackSpeedReductionMultiplier", 0.75D, 0.0D, 1.0D);
        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

//    public static void onConfigReload() {
//
//    }
}
