package com.cravencraft.stamina.events;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.StaminaManager;
import com.cravencraft.stamina.client.ClientStaminaData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class StaminaEvents {
    public static final int STAMINA_REGEN_TICKS = 10;

    public static void onServerPlayerTick(PlayerTickEvent.Pre event) {
        SimpleStamina.LOGGER.info("is client side player tick: {}", event.getEntity().level().isClientSide());
        var player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            SimpleStamina.STAMINA_MANAGER.tick(serverPlayer);
        }
        else if (player instanceof LocalPlayer localPlayer) {
            var clientPlayerStamina = ClientStaminaData.getStamina();
            localPlayer.sendSystemMessage(Component.literal("Current player stamina: " + clientPlayerStamina));
        }
    }
}
