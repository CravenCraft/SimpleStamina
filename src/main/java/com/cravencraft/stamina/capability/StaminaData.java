package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.SimpleStamina;
//import net.minecraft.client.player.LocalPlayer;
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
//    private ServerPlayer serverPlayer = null;
//    private LocalPlayer localPlayer = null;
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

//    public StaminaData(ServerPlayer serverPlayer) {
//        this(false);
//        this.player = serverPlayer;
//        this.serverPlayer = serverPlayer;
//        this.maxStamina = (int) this.player.getAttributeValue(MAX_STAMINA);
//    }

//    public StaminaData(LocalPlayer localPlayer) {
//        this(false);
//        this.player = localPlayer;
//        this.localPlayer = localPlayer;
//        this.maxStamina = (int) this.player.getAttributeValue(MAX_STAMINA);
//    }

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

//    public void tickStamina() {
//        this.tickStaminaRegen();
//
//        this.tickSprintStaminaCost();
//        this.tickSwimStaminaCost();
//        this.tickJumpStaminaCost();
//        this.tickAttackStaminaCost();
//    }

    public int getMaxStamina() {
        return this.maxStamina;
    }

    public void setMaxStamina(int maxStamina) {
        this.maxStamina = maxStamina;
    }

    public float getStamina() {
        return this.stamina;
    }

//    public void setStamina(float stamina) {
//        ChangeStaminaEvent event = new ChangeStaminaEvent(this.player, this, this.stamina, stamina);
//        if (this.player instanceof LocalPlayer || !NeoForge.EVENT_BUS.post(event).isCanceled()) {
//            this.stamina = event.getNewStamina();
//        }

//        if (this.stamina > this.maxStamina) {
//            this.stamina = this.maxStamina;
//        }
//        if (this.stamina <= 0) {
//            this.stamina = 0;
//        }

//        if (this.player instanceof ServerPlayer) {
//            if (this.stamina > this.maxStamina) {
//                this.stamina = this.maxStamina;
//            }
//            if (this.stamina <= 0) {
//                this.stamina = 0;
//            }
//            SimpleStamina.LOGGER.info("NEED TO SEND TO CLIENT. stamina to set: {}", stamina);
//            this.sendToClient = true;
//        }
//    }

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

//    private void tickStaminaRegen() {
//
//        if (!doStaminaRegen()) return;
//
//        if (stamina == this.maxStamina) {
//            return;
//        }
//
//        var increment = StaminaUtils.calculateStaminaRegenIncrement(serverPlayer);
//
//        this.addStamina(increment);
//    }

//    private boolean doStaminaRegen() {
//        if (this.stamina < this.maxStamina) {
//            if (STAMINA_REGEN_COOLDOWN > 0) {
//                STAMINA_REGEN_COOLDOWN--;
//            }
//
//            return STAMINA_REGEN_COOLDOWN == 0;
//        }
//        else {
//            return false;
//        }
//    }

//    private void tickSprintStaminaCost() {
//        if (!this.serverPlayer.isSprinting()) return;
//
//        var staminaCost = StaminaUtils.calculateSprintStaminaCost(this.serverPlayer);
//        this.removeStamina(staminaCost);
//    }
//
//    private void tickSwimStaminaCost() {
//        if (!this.serverPlayer.isSwimming()) return;
//
//        var staminaCost = StaminaUtils.calculateSwimStaminaCost(this.serverPlayer);
//        this.removeStamina(staminaCost);
//    }
//
//    private void tickJumpStaminaCost() {
//        if (!this.hasPlayerJumped) return;
//
//        var staminaCost = StaminaUtils.calculateJumpStaminaCost(this.serverPlayer);
//        this.removeStamina(staminaCost);
//        this.hasPlayerJumped = false;
//    }

//    // TODO: Maybe just send the total amount of stamina cost to the client side and have that phantom amount remain
//    //       and tick down from the client like in dark souls. Will clog the network up less, and will put less
//    //       calculations on the server. Also, it'll be converted to an integer client side, so it'll be even easier.
//    //       Also, have it to where the phantom amount doesn't tick down until the client stops receiving updates for
//    //       at least a second or two, and if the client receives another update just pause the reduction (unless 0).
//    //       That should keep the bar there and not have any wonky movement.
//
//    // TODO: ALSO, have the actual amount tick down gradually instead of suddenly, but still have it happen pretty fast.
//    //       Maybe divide the amount to remove by 10 ticks so it all can be processed in half a second? Test it out.
//    private void tickAttackStaminaCost() {
//        if (this.isSwinging) {
//            SimpleStamina.LOGGER.info("current swing duration: {} swing time: {}",
//                    serverPlayer.getCurrentSwingDuration(),
//                    serverPlayer.getCurrentItemAttackStrengthDelay());
//            this.tickSwingDuration();
//        }
//        // For whatever reason the player is swinging when they're sleeping? Have to account for that here.
//        else if (this.serverPlayer.swinging && !this.serverPlayer.isSleeping()) {
//            var attackDelay = serverPlayer.getCurrentItemAttackStrengthDelay();
//            var staminaCost = StaminaUtils.calculateAttackStaminaCost(this.serverPlayer);
//
//            this.swingTool((int) attackDelay, staminaCost);
//        }
//    }

//    private void swingTool(int swingDuration, float toolStaminaCost) {
//        this.setSwingDuration(swingDuration);
//        this.removeStamina(toolStaminaCost);
//        this.setSwingStaminaCost(toolStaminaCost);
//        this.setSwinging(true);
//    }

    // TODO: Maybe just send the total amount of stamina cost to the client side and have that phantom amount remain
    //       and tick down from the client like in dark souls. Will clog the network up less, and will put less
    //       calculations on the server. Also, it'll be converted to an integer client side, so it'll be even easier.
    //       Also, have it to where the phantom amount doesn't tick down until the client stops receiving updates for
    //       at least a second or two, and if the client receives another update just pause the reduction (unless 0).
    //       That should keep the bar there and not have any wonky movement.

    // TODO: ALSO, have the actual amount tick down gradually instead of suddenly, but still have it happen pretty fast.
    //       Maybe divide the amount to remove by 10 ticks so it all can be processed in half a second? Test it out.
//    public void blockAttack(float damageBlocked) {
//        this.removeStamina(damageBlocked);
//    }

//    private void tickSwingDuration() {
////        SimpleStamina.LOGGER.info("tick swing duration: {}", this.swingDuration);
//        if (this.swingDuration > 0  && this.stamina > 0) {
//            this.removeStamina(this.swingStaminaCost);
//            this.swingDuration--;
//        }
//        else {
//            this.setSwingStaminaCost(0);
//            this.setSwinging(false);
//        }
//    }

//    public void saveNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
//        compoundTag.putInt(STAMINA, (int) stamina);
//    }
//
//    public void loadNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
//        this.stamina = compoundTag.getInt(STAMINA);
//    }
}
