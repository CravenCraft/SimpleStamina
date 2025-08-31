package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.SimpleStamina;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import static com.cravencraft.stamina.registries.AttributeRegistry.MAX_STAMINA;

/**
 * TODO: Ok, got the basics of the event system down. Need to decide how I want to do this, and if I want to break this
 *       down to an event package that calls the various classes, or have a server and client side for the player.
 *       Will decide on this in a bit after practicing with everything more.
 *       Will work by extending from LocalPlayer and ServerPlayer. Have the calculations done in the server player,
 *       then sent to the local player for various things such as displaying the GUI and such (GUI might be diff class).
 */
public abstract class StaminaData {
    /** Static Fields for NBT Data **/
    public static final String STAMINA = "stamina";

    /** Static fields for stamina regen ticks */
    public static final int STAMINA_REGEN_TICKS = 60; // TODO: Probably wanna have this as a server config.
    public static int STAMINA_REGEN_COOLDOWN = 0;

    private boolean isMob;
    private boolean isSwinging;
    protected boolean sendToClient;
    protected int maxStamina;
    protected float stamina;
    private float swingStaminaCost;
    private int swingDuration;
    public Player player = null;
    private boolean hasPlayerJumped;

    public StaminaData(boolean isMob) {
        this.isMob = isMob;
    }

    public StaminaData() {
        this(false);
    }

    public StaminaData(Player player) {
        this(false);
        this.player = player;
        SimpleStamina.LOGGER.info("max stamina from attribute: {}", this.player.getAttributeValue(MAX_STAMINA));
        this.maxStamina = (int) this.player.getAttributeValue(MAX_STAMINA);
        SimpleStamina.LOGGER.info("max stamina set: {}", this.maxStamina);
    }

    public abstract void tickStamina();

    public boolean isSwinging() {
        return this.isSwinging;
    }

    public void setSwinging(boolean isSwinging) {
        this.isSwinging = isSwinging;
    }

    public boolean shouldSendToClient() {
        return this.sendToClient;
    }

    public void setSendToClient(boolean sendToClient) {
        this.sendToClient = sendToClient;
    }

    public int getSwingDuration() {
        return this.swingDuration;
    }

    public void setSwingDuration(int swingDuration) {
        this.swingDuration = swingDuration;
    }

    public float getSwingStaminaCost() {
        return this.swingStaminaCost;
    }

    public void setSwingStaminaCost(float swingStaminaCost) {
        this.swingStaminaCost = swingStaminaCost;
    }

    public boolean hasPlayerJumped() {
        return this.hasPlayerJumped;
    }

    public void setPlayerJumped() {
        this.hasPlayerJumped = true;
    }

    public int getMaxStamina() {
        return this.maxStamina;
    }

    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }

    public float getStamina() {
        return this.stamina;
    }

    // TODO: I'd like to rework these a bit later.
    public float getStaminaToAdd(float stamina) {
        if (this.stamina >= this.maxStamina) return 0.0f;

        var staminaToSet = (Mth.clamp(stamina, 0.0f, this.maxStamina));
        return this.stamina + staminaToSet;
    }

    public float getStaminaToSet(float stamina) {
        STAMINA_REGEN_COOLDOWN = STAMINA_REGEN_TICKS;
        if (this.stamina <= 0.0f) return 0.0f;

        var playerMaxStamina = (float) this.player.getAttributeValue(MAX_STAMINA);
        var staminaToSet = Mth.clamp(stamina, 0.0f, playerMaxStamina);
        staminaToSet = this.stamina - stamina;
        staminaToSet = (staminaToSet > 1) ? staminaToSet : 0;
        return staminaToSet;
    }
}
