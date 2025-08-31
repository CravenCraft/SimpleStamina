package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.events.ChangeStaminaEvent;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForge;

// TODO: Just make this extend StaminaData & instantiate it with the local player in the constructor.
public class ClientStaminaData extends StaminaData {
    private static final int STAMINA_REMOVAL_TICK_RATE = 10;
//    private static final StaminaData playerStaminaData = new StaminaData(Minecraft.getInstance().player);

    private final LocalPlayer localPlayer;
    private static int oldStamina;
    private static int expectedStaminaFromServer;


    public ClientStaminaData(LocalPlayer localPlayer) {
        super(localPlayer);
        this.localPlayer = localPlayer;
    }

    @Override
    public void tickStamina() {
        var currentStamina = this.getStamina();
        SimpleStamina.LOGGER.info("current stamina: {} | new stamina: {}", currentStamina, expectedStaminaFromServer);
        if (expectedStaminaFromServer > currentStamina) {
            var staminaToAdd = this.getStaminaToAdd(1);
            SimpleStamina.LOGGER.info("stamina to add: {} | max stamina: {}", staminaToAdd, this.maxStamina);
            this.setStamina((int) staminaToAdd);
        }
        else if (expectedStaminaFromServer < currentStamina) {
            var staminaToSet = this.getStaminaToSet(1);
            this.setStamina((int) staminaToSet);
        }

    }

    public int getOldStamina() {
        return oldStamina;
    }

    /**
     * TODO: This is the amount from the server. Not the amount to remove.
     * Want to set the stamina to remove client-side. Will factor in potential new stamina values, and account if they
     * are going to be less than 0.
     * @param playerStamina
     */
    public void setStaminFromServer(int playerStamina) {
        oldStamina = (int) this.getStamina();
        expectedStaminaFromServer = playerStamina;
        SimpleStamina.LOGGER.info("CLIENT SIDE. Old stamina: {} | Current Stamina: {} | New Stamina: {}", oldStamina, getStamina(), expectedStaminaFromServer);
    }

    public void setStamina(int playerStamina) {
        oldStamina = (int) this.getStamina();
        ChangeStaminaEvent event = new ChangeStaminaEvent(this.player, this, this.stamina, playerStamina);
        
        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            this.stamina = event.getNewStamina();
        }
//        super.setStamina(playerStamina);
    }

}
