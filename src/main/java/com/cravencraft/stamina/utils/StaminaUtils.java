package com.cravencraft.stamina.utils;

import com.cravencraft.stamina.config.ServerConfigs;
import net.minecraft.server.level.ServerPlayer;

import static com.cravencraft.stamina.registries.AttributeRegistry.*;
import static com.cravencraft.stamina.registries.DatapackRegistry.MELEE_WEAPONS_STAMINA_VALUES;

public class StaminaUtils {
    public static float calculateStaminaRegenIncrement(ServerPlayer serverPlayer) {
        var regenPercentage = .01f;
        var playerMaxStamina = (int) serverPlayer.getAttributeValue(MAX_STAMINA);
        var playerStaminaRegenMultiplier = (float) serverPlayer.getAttributeValue(STAMINA_REGEN);

        return playerMaxStamina *
                regenPercentage *
                playerStaminaRegenMultiplier *
                ServerConfigs.STAMINA_REGEN_MULTIPLIER.get().floatValue();
    }

    public static float calculateSprintStaminaCost(ServerPlayer serverPlayer) {

        return  (float) serverPlayer.getAttributeValue(SPRINT_STAMINA_COST) *
                        ServerConfigs.SPRINT_STAMINA_MULTIPLIER.get().floatValue();
    }

    public static float calculateSwimStaminaCost(ServerPlayer serverPlayer) {
        return (float) serverPlayer.getAttributeValue(SWIM_STAMINA_COST) *
                       ServerConfigs.SWIM_STAMINA_MULTIPLIER.get().floatValue();
    }

    public static float calculateJumpStaminaCost(ServerPlayer serverPlayer) {
        return (float) serverPlayer.getAttributeValue(JUMP_STAMINA_COST) *
                       ServerConfigs.JUMP_STAMINA_MULTIPLIER.get().floatValue();
    }

    public static float calculateAttackStaminaCost(ServerPlayer serverPlayer) {
        var attackDelay = serverPlayer.getCurrentItemAttackStrengthDelay();
        var attackStaminaCost = (float) serverPlayer.getAttributeValue(ATTACK_STAMINA_COST);
        var attackWeaponId = serverPlayer.getMainHandItem().getDescriptionId().replace("item.", "");

        if (MELEE_WEAPONS_STAMINA_VALUES.containsKey(attackWeaponId)) {
            attackStaminaCost = MELEE_WEAPONS_STAMINA_VALUES.get(attackWeaponId).floatValue();
        }

        attackStaminaCost *= ServerConfigs.ATTACK_STAMINA_MULTIPLIER.get().floatValue();
        attackStaminaCost = attackStaminaCost / attackDelay;

        return attackStaminaCost;
    }

}
