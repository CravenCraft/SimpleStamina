package com.cravencraft.stamina;

import com.cravencraft.stamina.capability.StaminaData;
import com.cravencraft.stamina.client.ClientStaminaData;
import com.cravencraft.stamina.config.ServerConfigs;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.cravencraft.stamina.registries.AttributeRegistry.*;

public class StaminaManager {
    public static final int STAMINA_REGEN_TICKS = 20;
    public static int STAMINA_REGEN_COOLDOWN = STAMINA_REGEN_TICKS;

    public void clientTick(LocalPlayer localPlayer) {
        var playerStamina = ClientStaminaData.getStamina();

        if (playerStamina > 0) return;

        localPlayer.setSprinting(false);
    }

    public void tick(ServerPlayer player) {
        var playerStamina = StaminaData.getPlayerStaminaData(player);
//        SimpleStamina.LOGGER.info("Current player stamina: {}", playerStamina.getStamina());
        modifyStamina(player, playerStamina);
    }

    public boolean doStaminaRegen(int playerMaxStamina, float playerCurrentStamina) {
        if (playerCurrentStamina != playerMaxStamina) {
            if (STAMINA_REGEN_COOLDOWN > 0) STAMINA_REGEN_COOLDOWN--;
//            SimpleStamina.LOGGER.info("STAMINA REGEN COOLDOWN: {}", STAMINA_REGEN_COOLDOWN);
            return STAMINA_REGEN_COOLDOWN == 0;
        }
        else {
            return false;
        }
    }

    public void modifyStamina(ServerPlayer serverPlayer, StaminaData staminaData) {
        regenStamina(serverPlayer, staminaData);
        removeStamina(serverPlayer, staminaData);
        setExhaustionEffects(serverPlayer, staminaData);
    }

    public void regenStamina(ServerPlayer serverPlayer, StaminaData staminaData) {
        int playerMaxStamina = (int) serverPlayer.getAttributeValue(MAX_STAMINA);
        var stamina = staminaData.getStamina();

        if (!doStaminaRegen(playerMaxStamina, stamina)) return;
        if (stamina == playerMaxStamina) {
            STAMINA_REGEN_COOLDOWN = 0;
            return;
        }

        float playerStaminaRegenMultiplier = (float) serverPlayer.getAttributeValue(STAMINA_REGEN);
        var increment = playerMaxStamina * playerStaminaRegenMultiplier * .01f * ServerConfigs.STAMINA_REGEN_MULTIPLIER.get().floatValue();

        staminaData.addStamina(Mth.clamp(increment, 0, playerMaxStamina));
        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(staminaData));
    }

    public void removeStamina(ServerPlayer serverPlayer, StaminaData staminaData) {
        int playerMaxStamina = (int) serverPlayer.getAttributeValue(MAX_STAMINA);
        var stamina = staminaData.getStamina();

        if (stamina <= 0) return;

        float staminaToRemove = 0.0f;
        if (serverPlayer.isSprinting()) {
            staminaToRemove = (float) serverPlayer.getAttributeValue(SPRINT_STAMINA_COST) * ServerConfigs.SPRINT_STAMINA_MULTIPLIER.get().floatValue();
        }
        else if (serverPlayer.isSwimming()) {
            staminaToRemove = (float) serverPlayer.getAttributeValue(SWIM_STAMINA_COST) * ServerConfigs.SWIM_STAMINA_MULTIPLIER.get().floatValue();
        }

        if (staminaToRemove <= 0.0f) return;

        STAMINA_REGEN_COOLDOWN = STAMINA_REGEN_TICKS;
        staminaData.removeStamina(Mth.clamp(staminaToRemove, 0, playerMaxStamina));

        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(staminaData));
    }

    public void setExhaustionEffects(ServerPlayer serverPlayer, StaminaData staminaData) {
        int playerMaxStamina = (int) serverPlayer.getAttributeValue(MAX_STAMINA);
        var stamina = staminaData.getStamina();

        if (stamina > 0) return;
        SimpleStamina.LOGGER.info("IS SPRINTING: {} AT SPEED: {}", serverPlayer.isSprinting(), serverPlayer.getSpeed());
//        serverPlayer.setSprinting(false);
        serverPlayer.setJumping(false);
        if (serverPlayer.isSprinting()) {
//            serverPlayer.isSprinting()
            serverPlayer.setSprinting(false);
            serverPlayer.setSpeed(0.08f);
//            serverPlayer.setJumping(false);
//            serverPlayer.canSprint()
        }
        if (serverPlayer.isSwimming()) {
            SimpleStamina.LOGGER.info("PLAYER IS SWIMMING.");
        serverPlayer.setSwimming(false);

        }
//        serverPlayer.setSprinting(false);
//        serverPlayer.setSwimming(false);
    }
}
