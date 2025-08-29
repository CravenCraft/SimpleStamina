package com.cravencraft.stamina;

import com.cravencraft.stamina.capability.StaminaData;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class StaminaManager {

    public void tick(ServerPlayer player) {
        var playerStamina = StaminaData.getPlayerStaminaData(player);
        SimpleStamina.LOGGER.info("Current player stamina: {}", playerStamina.getStamina());

        if (player.isSprinting()) {
            playerStamina.setStamina(playerStamina.getStamina() - 1);
        }
        else {
            playerStamina.addStamina(1);
            PacketDistributor.sendToPlayer(player, new SyncStaminaPacket(playerStamina));
        }
    }
}
