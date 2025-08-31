package com.cravencraft.stamina.registries;

import com.cravencraft.stamina.SimpleStamina;

import java.util.HashMap;
import java.util.Map;

import static com.cravencraft.stamina.events.ServerEvents.*;

public class DatapackRegistry {
    public static Map<String, Double> MELEE_WEAPONS_STAMINA_VALUES = new HashMap<>();
    public static Map<String, Double> RANGED_WEAPONS_STAMINA_VALUES = new HashMap<>();
    public static Map<String, Double> SHIELD_STAMINA_VALUES = new HashMap<>();

    public static void addDataPackStaminaValues(String type, String itemStack, double staminaValue) {
        SimpleStamina.LOGGER.info("Adding stamina value {} for the item {} of type {}", staminaValue, itemStack, type);
        switch (type) {
            case MELEE_WEAPON_PATH -> MELEE_WEAPONS_STAMINA_VALUES.put(itemStack, staminaValue);
            case RANGED_WEAPON_PATH -> RANGED_WEAPONS_STAMINA_VALUES.put(itemStack, staminaValue);
            case SHIELD_PATH -> SHIELD_STAMINA_VALUES.put(itemStack, staminaValue);
        }
    }
}
