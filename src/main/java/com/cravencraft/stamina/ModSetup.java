package com.cravencraft.stamina;

import com.cravencraft.stamina.events.StaminaEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public class ModSetup {

    public static void setup() {
        IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(StaminaEvents::onPlayerJoin);
        bus.addListener(StaminaEvents::onPlayerTick);
        bus.addListener(StaminaEvents::onPlayerJump);
    }
}
