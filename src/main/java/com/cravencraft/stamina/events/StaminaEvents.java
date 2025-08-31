package com.cravencraft.stamina.events;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.client.ClientStaminaData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class StaminaEvents {

    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.onPlayerJoin(serverPlayer);
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.tick(serverPlayer);
        }
        else if (player instanceof LocalPlayer localPlayer) {
            SimpleStamina.CLIENT_STAMINA_MANAGER.clientTick(localPlayer);
            var clientPlayerStamina = ClientStaminaData.getStamina();
        }
    }

    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.playerJump(serverPlayer);
        }
    }
}
