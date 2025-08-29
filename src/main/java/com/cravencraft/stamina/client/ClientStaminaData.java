package com.cravencraft.stamina.client;

import com.cravencraft.stamina.capability.StaminaData;

public class ClientStaminaData {
    private static final StaminaData playerStaminaData = new StaminaData();

    public static void setStamina(int playerStamina) {
        ClientStaminaData.playerStaminaData.setStamina(playerStamina);
    }

    public static int getStamina() {
        return (int) playerStaminaData.getStamina();
    }
}
