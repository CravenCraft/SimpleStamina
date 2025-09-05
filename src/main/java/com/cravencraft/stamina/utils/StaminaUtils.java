package com.cravencraft.stamina.utils;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.config.ServerConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

import static com.cravencraft.stamina.registries.AttributeRegistry.*;
import static com.cravencraft.stamina.registries.DatapackRegistry.*;

public class StaminaUtils {
    public static float calculateStaminaRegenIncrement(ServerPlayer serverPlayer) {
        var regenPercentage = (float) serverPlayer.getAttributeValue(STAMINA_REGEN);
        var playerMaxStamina = (int) serverPlayer.getAttributeValue(MAX_STAMINA);
        var playerStaminaRegenMultiplier = ServerConfigs.STAMINA_REGEN_MULTIPLIER.get().floatValue();

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


    public static float calculateBlockStaminaCost(ServerPlayer serverPlayer, float damageBlocked) {
        SimpleStamina.LOGGER.info("initial block cost so far: {}", damageBlocked);
        var blockCostReduction = 1 - serverPlayer.getAttributeValue(BLOCK_STAMINA_COST_REDUCTION);
        var blockStaminaCost = damageBlocked * blockCostReduction;
        var shieldBlockReduction = getDataPackItemValue(SHIELD_STAMINA_VALUES, serverPlayer.getMainHandItem());
        blockStaminaCost = blockStaminaCost * (1 - shieldBlockReduction * .01f);

        blockStaminaCost *= ServerConfigs.BLOCK_STAMINA_REDUCTION_MULTIPLIER.get().floatValue();

        return (float) blockStaminaCost;

    }

    // TODO: Later, add some attributes to modify the attack stamina cost, which can be something like an attribute
    //       that can be leveled up to decrease attack stamina cost by a percentage (similar to the regen rate attribute).
    public static float calculateAttackStaminaCost(ServerPlayer serverPlayer) {
        var attackStaminaCost = (float) serverPlayer.getAttributeValue(ATTACK_STAMINA_COST);
        var dataPackItemValue = getDataPackItemValue(MELEE_WEAPONS_STAMINA_VALUES, serverPlayer.getMainHandItem());
        attackStaminaCost = (dataPackItemValue > 0) ? dataPackItemValue : attackStaminaCost;

        attackStaminaCost *= ServerConfigs.ATTACK_STAMINA_MULTIPLIER.get().floatValue();

        return attackStaminaCost;
    }

    public static float calculateDrawBowStaminaCost(ServerPlayer serverPlayer, ItemStack rangedWeapon) {
        var drawStaminaCost = (float) serverPlayer.getAttributeValue(PULL_BOW_STAMINA_COST);
        var dataPackItemValue = getDataPackItemValue(RANGED_WEAPONS_STAMINA_VALUES, rangedWeapon);
        drawStaminaCost = (dataPackItemValue > 0) ? dataPackItemValue : drawStaminaCost;

        drawStaminaCost *= ServerConfigs.PULL_BOW_STAMINA_MULTIPLIER.get().floatValue();

        return drawStaminaCost;
    }

    public static float getDataPackItemValue(HashMap<String, Double> dataPackMap, ItemStack item) {
        var itemId = item.getDescriptionId().replace("item.", "");

        return dataPackMap.containsKey(itemId) ? dataPackMap.get(itemId).floatValue() : 0;
    }
}
