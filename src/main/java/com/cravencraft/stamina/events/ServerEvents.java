package com.cravencraft.stamina.events;

import com.cravencraft.stamina.SimpleStamina;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@Mod(SimpleStamina.MODID)
@EventBusSubscriber(modid = SimpleStamina.MODID, value = Dist.DEDICATED_SERVER)
public class ServerEvents {


    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getUUID();
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
//        event.registerEntity(Capabilities.ItemHandler.ENTITY, (myEntity, context) -> myEntity.);
    }

}
