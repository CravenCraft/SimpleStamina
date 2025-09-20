package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.SimpleStamina;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import static com.cravencraft.stamina.manager.StaminaManager.STAMINA_REGEN_TICKS;
import static com.cravencraft.stamina.manager.StaminaManager.STAMINA_REGEN_COOLDOWN;
import static com.cravencraft.stamina.registries.AttributeRegistry.*;

/**
 * TODO: Ok, got the basics of the event system down. Need to decide how I want to do this, and if I want to break this
 *       down to an event package that calls the various classes, or have a server and client side for the player.
 *       Will decide on this in a bit after practicing with everything more.
 *       Will work by extending from LocalPlayer and ServerPlayer. Have the calculations done in the server player,
 *       then sent to the local player for various things such as displaying the GUI and such (GUI might be diff class).
 */
public abstract class StaminaData {
//
//    /** Static fields for stamina regen ticks */
//    public static final int STAMINA_REGEN_TICKS = 60; // TODO: Probably wanna have this as a server config.
//    public static int STAMINA_REGEN_COOLDOWN = 0;
public static final ResourceLocation PLAYER_EXHAUSTION_LEVEL_MODIFIER = ResourceLocation.fromNamespaceAndPath(SimpleStamina.MODID, "player_exhaustion_level_modifier");
    public static final ResourceLocation PLAYER_SEGMENT_EXHAUSTION_AMOUNT_MODIFIER = ResourceLocation.fromNamespaceAndPath(SimpleStamina.MODID, "player_segment_exhaustion_amount_modifier");
    public static int SEGMENT_STAMINA_AMOUNT = 25; // TODO: Probably want to make this a server config and/or capability as well?
    private boolean isMob;

    // TODO: Ok, when setting the max stamina on player join, I need to ensure that I maybe call update stamina. I should probably always do that to ensure that all of the attributes are
    //       properly synced up at all times.
    protected int maxStamina;
    protected float stamina;
    public int segmentExhaustionLimit;
    protected int playerExhaustionLevel;
    protected double totalStaminaConsumed;
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
        this.segmentExhaustionLimit = (int) this.player.getAttributeValue(PLAYER_EXHAUSTION_LIMIT);
        SimpleStamina.LOGGER.info("Setting stamina data for the player. segmentExhaustionLimit set: {}", this.segmentExhaustionLimit);
        this.playerExhaustionLevel = (int) this.player.getAttributeValue(PLAYER_EXHAUSTION_LEVEL);
        SimpleStamina.LOGGER.info("max stamina set: {}", this.maxStamina);
    }

    public int getMaxStamina() {
        return this.maxStamina;
    }

    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;

        if (this.stamina <= this.maxStamina) return;

        this.stamina = this.maxStamina;
    }

    public double getTotalStaminaConsumed() {
        return this.totalStaminaConsumed;
    }

    public void setTotalStaminaConsumed(double totalStaminaConsumed) {
        this.totalStaminaConsumed = totalStaminaConsumed;
    }

    public int getPlayerExhaustionLevel() {
        return (int) this.player.getAttributeValue(PLAYER_EXHAUSTION_LEVEL);
    }

    public void setPlayerExhaustionLevel(int playerExhaustionLevel) {
        AttributeInstance playerExhaustionLevelAttribute = this.player.getAttribute(PLAYER_EXHAUSTION_LEVEL);

        if (playerExhaustionLevelAttribute == null) return;

        AttributeModifier modifiedPlayerExhaustionLevel = new AttributeModifier(PLAYER_EXHAUSTION_LEVEL_MODIFIER, playerExhaustionLevel, AttributeModifier.Operation.ADD_VALUE);
        playerExhaustionLevelAttribute.addOrReplacePermanentModifier(modifiedPlayerExhaustionLevel);

        SimpleStamina.LOGGER.info("player exhaustion level AFTER modification: {}", playerExhaustionLevelAttribute.getValue());
    }

    public int getSegmentExhaustionLimit() {
        return this.segmentExhaustionLimit;
    }

    public void setSegmentExhaustionLimit(int segmentExhaustionLimit) {
        this.segmentExhaustionLimit = segmentExhaustionLimit;
    }

    // TODO: When this happens, need to set the current stamina to not be greater than the max stamina.
    //       Probably want to change the name of this method as well. Maybe to something like "decreaseMaxStamina".
    public void decreaseMaxStamina() {
        if (playerExhaustionLevel % SEGMENT_STAMINA_AMOUNT == 0) {
            this.maxStamina = (int) this.player.getAttributeValue(MAX_STAMINA);
            var playerExhaustionLevel = (int) this.player.getAttributeValue(PLAYER_EXHAUSTION_LEVEL);
            SimpleStamina.LOGGER.info("Updating the max stamina value {} by adding the player exhaustion level: {}", this.maxStamina, playerExhaustionLevel);
            this.maxStamina = this.maxStamina - (playerExhaustionLevel);

            if (this.stamina <= this.maxStamina) return;

            this.stamina = this.maxStamina;
        }
    }

    public void removePlayerExhaustionAttributeModifications () {
        AttributeInstance playerExhaustionLevelAttribute = this.player.getAttribute(PLAYER_EXHAUSTION_LEVEL);

        if (playerExhaustionLevelAttribute == null) return;

        SimpleStamina.LOGGER.info("Exhaustion attribute BEFORE attribute modification removal: {}", playerExhaustionLevelAttribute.getValue());
        playerExhaustionLevelAttribute.removeModifier(PLAYER_EXHAUSTION_LEVEL_MODIFIER);
        SimpleStamina.LOGGER.info("Exhaustion attribute AFTER attribute modification removal: {}", playerExhaustionLevelAttribute.getValue());
    }

    public void modifyPlayerSegmentExhaustionAmount(int amount) {

        AttributeInstance playerSegmentExhaustionAmountAttribute = this.player.getAttribute(PLAYER_SEGMENT_EXHAUSTION_AMOUNT);

        if (playerSegmentExhaustionAmountAttribute == null) return;

        var playerSegmentExhaustionAmountValue = playerSegmentExhaustionAmountAttribute.getValue();
        SimpleStamina.LOGGER.info("player segment exhaustion amount before modification: {}", playerSegmentExhaustionAmountAttribute.getValue());

        playerSegmentExhaustionAmountValue += amount;


        AttributeModifier modifiedPlayerExhaustionLevel = new AttributeModifier(PLAYER_SEGMENT_EXHAUSTION_AMOUNT_MODIFIER, playerSegmentExhaustionAmountValue, AttributeModifier.Operation.ADD_VALUE);
        playerSegmentExhaustionAmountAttribute.addOrReplacePermanentModifier(modifiedPlayerExhaustionLevel);

        SimpleStamina.LOGGER.info("player segment exhaustion amount AFTER modification: {}", playerSegmentExhaustionAmountAttribute.getValue());
    }

    public void removePlayerSegmentExhaustionAttributeModifications() {
        AttributeInstance playerSegmentExhaustionAmountAttribute = this.player.getAttribute(PLAYER_SEGMENT_EXHAUSTION_AMOUNT);

        if (playerSegmentExhaustionAmountAttribute == null) return;

        SimpleStamina.LOGGER.info("Segment exhaustion amount attribute BEFORE attribute modification removal: {}", playerSegmentExhaustionAmountAttribute.getValue());
        playerSegmentExhaustionAmountAttribute.removeModifier(PLAYER_SEGMENT_EXHAUSTION_AMOUNT_MODIFIER);
        SimpleStamina.LOGGER.info("Segment exhaustion amount attribute AFTER attribute modification removal: {}", playerSegmentExhaustionAmountAttribute.getValue());
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
