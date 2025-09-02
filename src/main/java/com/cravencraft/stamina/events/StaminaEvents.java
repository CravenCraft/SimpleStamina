package com.cravencraft.stamina.events;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.capability.ServerStaminaData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BowItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SimpleStamina.MODID)
public class StaminaEvents {

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.onPlayerJoin(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.tick(serverPlayer);
        }
        else if (player instanceof LocalPlayer localPlayer) {
            SimpleStamina.CLIENT_STAMINA_MANAGER.clientTick(localPlayer);
        }
    }

    /**
     * Fires whenever the player attempts to block an attack. Fires even if the block attempt fails (block too late).
     * TODO: Holding a shield should slow stamina regen by half.
     * @param event
     */
    @SubscribeEvent
    public static void onPlayerBlock(LivingShieldBlockEvent event) {
        if (event.getBlocked() && event.getEntity() instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.playerBlockAttack(serverPlayer, event.getBlockedDamage());
        }
    }

    @SubscribeEvent
    public static void onPlayerJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.playerJump(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void cancelBowDraw(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            var isPlayerStaminaDepleted = SimpleStamina.SERVER_STAMINA_MANAGER.shouldCancelBowDraw(serverPlayer, event.getItemStack());

            event.setCanceled(isPlayerStaminaDepleted);
        }
    }
}
