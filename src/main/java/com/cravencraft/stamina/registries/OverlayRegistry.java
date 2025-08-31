package com.cravencraft.stamina.registries;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.client.gui.StaminaBarOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = SimpleStamina.MODID, value = Dist.CLIENT)
public class OverlayRegistry {

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, SimpleStamina.id("stamina_overlay"), StaminaBarOverlay.getInstance());
    }
}
