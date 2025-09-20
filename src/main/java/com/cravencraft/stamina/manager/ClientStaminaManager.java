package com.cravencraft.stamina.manager;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.capability.ClientStaminaData;
import com.cravencraft.stamina.utils.StaminaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import static com.cravencraft.stamina.config.ServerConfigs.*;
import static com.cravencraft.stamina.registries.AttributeRegistry.MAX_STAMINA;
import static com.cravencraft.stamina.registries.AttributeRegistry.STAMINA_REGEN;
import static com.cravencraft.stamina.registries.DatapackRegistry.RANGED_WEAPONS_STAMINA_VALUES;

public class ClientStaminaManager extends StaminaManager {
    private static final int STAMINA_REMOVAL_PER_TICK = 3;
    private static LocalPlayer localPlayer;
    private static ClientStaminaData clientStaminaData;

    public static ClientStaminaData getClientStaminaData() {
        return clientStaminaData;
    }

    public static void onPlayerJoin(int staminaFromServer) {
        clientStaminaData = new ClientStaminaData(Minecraft.getInstance().player, staminaFromServer);
        localPlayer = Minecraft.getInstance().player;
        SimpleStamina.LOGGER.info("Client player has joined. Syncing Stamina: {}.", clientStaminaData.getStamina());
    }

    /**
     * Want to set the stamina to remove client-side. Will factor in potential new stamina values, and account if they
     * are going to be less than 0.
     * @param playerStamina
     */
    public static void setStaminaFromServer(int playerStamina) {
//        SimpleStamina.LOGGER.info("client side current stamina: {}", clientStaminaData.getStamina());
        clientStaminaData.setOldStamina((int) clientStaminaData.getStamina());
        clientStaminaData.setExpectedStaminaFromServer(playerStamina);
//        SimpleStamina.LOGGER.info("CLIENT SIDE. Old stamina: {} | Current Stamina: {} | New Stamina: {}", oldStamina, getStamina(), expectedStaminaFromServer);
    }

    public static void setMaxStaminaFromServer(int playerMaxStamina) {
        if (clientStaminaData.getMaxStamina() == playerMaxStamina) return;
        SimpleStamina.LOGGER.info("setting max stamina from server {}", playerMaxStamina);
        clientStaminaData.setMaxStamina(playerMaxStamina);
    }

    public void clientTick() {
//        SimpleStamina.LOGGER.info("--- client tick. current stamina: {} | max stamina: {} ---", clientStaminaData.getStamina(), clientStaminaData.getMaxStamina());
        if (clientStaminaData.getExpectedStaminaFromServer() != clientStaminaData.getStamina()) {
            this.tickAddStamina();
            this.tickRemoveStamina();
        }

        setExhaustionEffects();
    }

    private void tickAddStamina() {
        var addStaminaSpeed = (int) (localPlayer.getAttributeValue(STAMINA_REGEN) * localPlayer.getAttributeValue(MAX_STAMINA));
//        SimpleStamina.LOGGER.info("stamina regen multiplier: {} | max stamina: {}", localPlayer.getAttributeValue(STAMINA_REGEN), clientStaminaData.getMaxStamina());
//        SimpleStamina.LOGGER.info("add stamina speed: {}", addStaminaSpeed);
        for (int i = 0; i < addStaminaSpeed; i++) {
            if (clientStaminaData.getExpectedStaminaFromServer() <= clientStaminaData.getStamina()) return;
            var staminaToAdd = clientStaminaData.getStaminaAfterAdd(1);
            clientStaminaData.setStamina((int) staminaToAdd);

        }
    }

    private void tickRemoveStamina() {
        for (int i = 0; i < STAMINA_REMOVAL_PER_TICK; i++) {
            if (clientStaminaData.getExpectedStaminaFromServer() >= clientStaminaData.getStamina()) return;
            var staminaToSet = clientStaminaData.getStaminaAfterRemove(1);
            clientStaminaData.setStamina((int) staminaToSet);
        }
    }

    private void setExhaustionEffects() {
        if (clientStaminaData.getStamina() > 0.0f) return;

        this.disableSprint();
        this.disableSwim();
        this.disableRangedWeaponUse();
    }

    /**
     * Disables sprinting and fast swimming if the player does not have any stamina.
     */
    private void disableSprint() {
        if (!TOGGLE_SPRINT_STAMINA.get()) return;

        Minecraft.getInstance().options.keySprint.setDown(false);
        localPlayer.setSprinting(false);
    }

    private void disableSwim() {
        if (!TOGGLE_SWIM_STAMINA.get()) return;

        localPlayer.setSwimming(false);
    }

    private void disableRangedWeaponUse() {
        if (!TOGGLE_RANGED_ATTACK_STAMINA.get()) return;

        var useItem = localPlayer.getUseItem();
        if (StaminaUtils.getDataPackItemValue(RANGED_WEAPONS_STAMINA_VALUES, useItem) == 0) return;

        SimpleStamina.LOGGER.info("Disabling use item key use.");
        Minecraft.getInstance().options.keyUse.setDown(false);

    }
}
