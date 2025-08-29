package com.cravencraft.stamina.setup;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SimpleStamina.MODID)
public class PayloadHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar payloadRegistrar = event.registrar(SimpleStamina.MODID).versioned("1.0.0").optional();

        payloadRegistrar.playToClient(SyncStaminaPacket.TYPE, SyncStaminaPacket.STREAM_CODEC, SyncStaminaPacket::handle);
    }
}
