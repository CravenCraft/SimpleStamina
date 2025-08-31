package com.cravencraft.stamina.manager;

import com.cravencraft.stamina.client.ClientStaminaData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientStaminaManager extends StaminaManager {


    public void clientTick(LocalPlayer localPlayer) {
        var playerStamina = ClientStaminaData.getStamina();

        if (playerStamina > 0.0f) return;

        disableSprint(localPlayer);
//        localPlayer.attack();

    }

    /**
     * Disables sprinting and fast swimming if the player does not have any stamina.
     * @param localPlayer
     */
    private void disableSprint(LocalPlayer localPlayer) {
        Minecraft.getInstance().options.keySprint.setDown(false);
        localPlayer.setSprinting(false);
    }

}
