package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.events.ChangeStaminaEvent;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForge;

// TODO: Just make this extend StaminaData & instantiate it with the local player in the constructor.
public class ClientStaminaData extends StaminaData {

    private final LocalPlayer localPlayer;
    private int oldStamina;
    private int expectedStaminaFromServer;


    public ClientStaminaData(LocalPlayer localPlayer, int staminaFromServer) {
        super(localPlayer);
        this.localPlayer = localPlayer;
        this.expectedStaminaFromServer = staminaFromServer;
    }

    public int getOldStamina() {
        return this.oldStamina;
    }

    public void setOldStamina(int oldStamina) {
        this.oldStamina = oldStamina;
    }

    public int getExpectedStaminaFromServer() {
        return this.expectedStaminaFromServer;
    }

    public void setExpectedStaminaFromServer(int expectedStaminaFromServer) {
        this.expectedStaminaFromServer = expectedStaminaFromServer;
    }

    public void setStamina(int playerStamina) {
        this.oldStamina = (int) this.getStamina();
        ChangeStaminaEvent event = new ChangeStaminaEvent(this.player, this, this.stamina, playerStamina);
        
        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            this.stamina = event.getNewStamina();
        }
    }
}
