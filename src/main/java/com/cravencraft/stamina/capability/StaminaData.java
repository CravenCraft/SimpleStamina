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

    /** Static fields for stamina regen ticks */
    public static final int STAMINA_REGEN_TICKS = 60; // TODO: Probably wanna have this as a server config.
    public static int STAMINA_REGEN_COOLDOWN = 0;

    private boolean isMob;
    protected int maxStamina;
    protected float stamina;
    public Player player = null;

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
    public float getStaminaAfterAdd(float stamina) {
        if (this.stamina >= this.maxStamina) return 0.0f;

        var staminaToSet = (Mth.clamp(stamina, 0.0f, this.maxStamina));
        return Math.min(this.stamina + staminaToSet, this.maxStamina);
    }

    public float getStaminaAfterRemove(float stamina) {
        STAMINA_REGEN_COOLDOWN = STAMINA_REGEN_TICKS;
        if (this.stamina <= 0.0f) return 0.0f;

        var playerMaxStamina = (float) this.player.getAttributeValue(MAX_STAMINA);
        var staminaToSet = Mth.clamp(stamina, 0.0f, playerMaxStamina);
        return Math.max(this.stamina - staminaToSet, 0);
    }
}
