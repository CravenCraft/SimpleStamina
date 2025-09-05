// TODO: Delete
//package com.cravencraft.stamina.client.events;
//
//import com.cravencraft.stamina.SimpleStamina;
//import com.cravencraft.stamina.manager.ClientStaminaManager;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.player.LocalPlayer;
//import net.neoforged.api.distmarker.Dist;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.common.Mod;
//import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
//
//@Mod(SimpleStamina.MODID)
//@EventBusSubscriber(modid = SimpleStamina.MODID, value = Dist.CLIENT)
//public class ClientEvents {
//
////    @SubscribeEvent
////    public static void onPlayerAttackAnimation(RenderHandEvent event) {
////        event.getHand().
////        event.setCanceled(true);
////        SimpleStamina.LOGGER.info("PLAYER HAND SWING PROGRESS: {}", event.getSwingProgress());
////    }
//
////    @SubscribeEvent
////    public static void onPlayerClickEvent(PlayerInteractEvent.LeftClickEmpty event) {
//////        SimpleStamina.LOGGER.info("IS ATTACKING CLIENT SIDE: {}", event.getEntity().level().isClientSide());
////
////        var player = event.getEntity();
////        if (player instanceof LocalPlayer localPlayer) {
//////            localPlayer.isHandsBusy()
////
//////            SimpleStamina.LOGGER.info("IS ATTACKING CLIENT SIDE: {}", event.getEntity().level().isClientSide());
////            var currentPlayerStamina = ClientStaminaData.getStamina();
////            if (currentPlayerStamina > 0) return;
////
//////            SimpleStamina.LOGGER.info("IS SETTING ATTACK KEY PRESSED TO FALSE.");
////
//////            Minecraft.getInstance().options.keyAttack.setDown(false);
////        }
////    }
//
//    /**
//     * Is fired on both the client and server.
//     */
//    @SubscribeEvent
//    public static void onPlayerAttack(AttackEntityEvent event) {
//
//        var player = event.getEntity();
//
//        // TODO: This isn't accessed via the client. IDK why. Maybe it's serverside only? If so, just set the cancel
//        //       event for the server player instance.
//        if (player instanceof LocalPlayer localPlayer) {
//            SimpleStamina.LOGGER.info("IS ATTACKING CLIENT SIDE: {}", event.getEntity().level().isClientSide());
////            localPlayer.set
//            var currentPlayerStamina = ClientStaminaManager.getClientStaminaData().getStamina();
//            if (currentPlayerStamina > 0) return;
//
//            event.setCanceled(true);
////            localPlayer.ani
//            Minecraft.getInstance().options.keyAttack.setDown(false);
////            Minecraft.getInstance().
//        }
//
////        if (player instanceof ServerPlayer serverPlayer) {
////
////            var staminaData = StaminaData.getPlayerStaminaData(serverPlayer);
//////            SimpleStamina.LOGGER.info("swing time: {}", serverPlayer.swingTime);
//////            SimpleStamina.LOGGER.info("current swing duration: {}", serverPlayer.getCurrentSwingDuration());
////            if (staminaData.getStamina() > 0.0f) return;
////
//////            SimpleStamina.LOGGER.info("CANCELLING ATTACK");
//////            event.setCanceled(true);
////        }
//    }
//}