package com.cravencraft.stamina.events;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.client.ClientStaminaData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class StaminaEvents {

    public static void onServerPlayerTick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
//        event.getEntity().is
        if (player instanceof ServerPlayer serverPlayer) {
            SimpleStamina.STAMINA_MANAGER.tick(serverPlayer);
        }
        else if (player instanceof LocalPlayer localPlayer) {
            SimpleStamina.STAMINA_MANAGER.clientTick(localPlayer);
            var clientPlayerStamina = ClientStaminaData.getStamina();
        }
    }
}
