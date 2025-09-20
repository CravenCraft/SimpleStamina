package com.cravencraft.stamina.events;

import com.cravencraft.stamina.SimpleStamina;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
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
        else if (player instanceof LocalPlayer) {
            SimpleStamina.CLIENT_STAMINA_MANAGER.clientTick();
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
    public static void onPlayerUseItem(LivingEntityUseItemEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getDuration() > 0) {
            SimpleStamina.LOGGER.info("how long have you been using the item: {}", event.getDuration());
            SimpleStamina.SERVER_STAMINA_MANAGER.useRangedWeapon(serverPlayer, event.getItem());
        }
    }

    // TODO: I'll need to send this data to the client too I believe.
    // TODO: Set an attribute for the player's current max stamina so that it is saved when the player logs out.
    // TODO:  How funny would it be to force the player to sleep whenever their stamina reaches 0? They'd pass out for
    //        around an in game hour or two, then wake back up with 25 stamina, but their foods wouldn't be reset.
    //        Could be a cool "hardcore" setting.
    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
//        event.getProblem().
        SimpleStamina.LOGGER.info("player is sleeping {}", event.getEntity().isSleeping());

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            SimpleStamina.SERVER_STAMINA_MANAGER.restorePlayerStaminaAfterSleeping(serverPlayer);
//            event.setContinueSleeping(canPlayerKeepSleeping);
        }

    }

    @SubscribeEvent
    public static void onPlayerSleep(CanPlayerSleepEvent event) {
        // TODO: During the day the value is NOT_POSSIBLE_NOW
        SimpleStamina.LOGGER.info("can player sleep {}", event.getProblem());

    }
}
