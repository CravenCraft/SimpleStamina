package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.events.ChangeStaminaEvent;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForge;

import static com.cravencraft.stamina.registries.AttributeRegistry.STAMINA_REGEN;

// TODO: Just make this extend StaminaData & instantiate it with the local player in the constructor.
public class ClientStaminaData extends StaminaData {
    //
    private static final int STAMINA_REMOVAL_PER_TICK = 3;

    private final LocalPlayer localPlayer;
    private int oldStamina;
    private int expectedStaminaFromServer;


    public ClientStaminaData(LocalPlayer localPlayer, int staminaFromServer) {
        super(localPlayer);
        this.localPlayer = localPlayer;
        this.expectedStaminaFromServer = staminaFromServer;
    }

    @Override
    public void tickStamina() {
        var currentStamina = this.getStamina();

        if (this.expectedStaminaFromServer == currentStamina) return;

        this.tickAddStamina();
        this.tickRemoveStamina();

    }

    private void tickAddStamina() {
        var addStaminaSpeed = (int) (this.localPlayer.getAttributeValue(STAMINA_REGEN) * this.maxStamina);
        for (int i = 0; i < addStaminaSpeed; i++) {
            if (this.expectedStaminaFromServer <= this.getStamina()) return;
            var staminaToAdd = this.getStaminaAfterAdd(1);
            this.setStamina((int) staminaToAdd);

        }
    }

    private void tickRemoveStamina() {
        for (int i = 0; i < STAMINA_REMOVAL_PER_TICK; i++) {
            if (this.expectedStaminaFromServer >= this.getStamina()) return;
            var staminaToSet = this.getStaminaAfterRemove(1);
            this.setStamina((int) staminaToSet);
        }
    }

    public int getOldStamina() {
        return this.oldStamina;
    }

    /**
     * Want to set the stamina to remove client-side. Will factor in potential new stamina values, and account if they
     * are going to be less than 0.
     * @param playerStamina
     */
    public void setStaminFromServer(int playerStamina) {
        this.oldStamina = (int) this.getStamina();
        this.expectedStaminaFromServer = playerStamina;
//        SimpleStamina.LOGGER.info("CLIENT SIDE. Old stamina: {} | Current Stamina: {} | New Stamina: {}", oldStamina, getStamina(), expectedStaminaFromServer);
    }

    public void setStamina(int playerStamina) {
        this.oldStamina = (int) this.getStamina();
        ChangeStaminaEvent event = new ChangeStaminaEvent(this.player, this, this.stamina, playerStamina);
        
        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            this.stamina = event.getNewStamina();
        }
    }

}
