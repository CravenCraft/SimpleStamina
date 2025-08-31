package com.cravencraft.stamina.manager;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.capability.ClientStaminaData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientStaminaManager extends StaminaManager {

    private LocalPlayer localPlayer;
    private static ClientStaminaData clientStaminaData;

    public static ClientStaminaData getClientStaminaData() {
        return clientStaminaData;
    }

    public static void onPlayerJoin(LocalPlayer localPlayer) {
        clientStaminaData = new ClientStaminaData(localPlayer);
        SimpleStamina.LOGGER.info("Client player has joined. Syncing Stamina: {}.", clientStaminaData.getStamina());
//        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(serverStaminaData));
    }

//    public ClientStaminaManager() {
//        this.localPlayer = Minecraft.getInstance().player;
//        CLIENT_STAMINA_DATA = new ClientStaminaData(localPlayer);
//    }

//    public ClientStaminaData getClientStaminaData() {
//        return this.clientStaminaData;
//    }

    public void clientTick(LocalPlayer localPlayer) {
        clientStaminaData.tickStamina();
        var playerStamina = clientStaminaData.getStamina();


        if (playerStamina > 0.0f) return;

        setExhaustionEffects(localPlayer);

    }

    private void setExhaustionEffects(LocalPlayer localPlayer) {
        this.disableSprint(localPlayer);
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
