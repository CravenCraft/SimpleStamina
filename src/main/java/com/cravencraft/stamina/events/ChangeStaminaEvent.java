package com.cravencraft.stamina.events;

import com.cravencraft.stamina.capability.StaminaData;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ChangeStaminaEvent extends PlayerEvent implements ICancellableEvent {
    private final StaminaData staminaData;
    private final float oldStamina;
    private float newStamina;

    public ChangeStaminaEvent(Player player, StaminaData staminaData, float oldStamina, float newStamina) {
        super(player);
        this.staminaData = staminaData;
        this.oldStamina = oldStamina;
        this.newStamina = newStamina;
    }

    public StaminaData getStaminaData() {
        return this.staminaData;
    }

    public float getOldStamina() {
        return this.oldStamina;
    }

    public float getNewStamina() {
        return this.newStamina;
    }

    public void setNewStamina(float newStamina) {
        this.newStamina = newStamina;
    }
}
