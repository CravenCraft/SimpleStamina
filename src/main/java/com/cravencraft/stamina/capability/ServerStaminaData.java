package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.events.ChangeStaminaEvent;
import com.cravencraft.stamina.registries.DataAttachmentRegistry;
import com.cravencraft.stamina.utils.StaminaUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;

/**
 * TODO: Ok, got the basics of the event system down. Need to decide how I want to do this, and if I want to break this
 *       down to an event package that calls the various classes, or have a server and client side for the player.
 *       Will decide on this in a bit after practicing with everything more.
 *       Will work by extending from LocalPlayer and ServerPlayer. Have the calculations done in the server player,
 *       then sent to the local player for various things such as displaying the GUI and such (GUI might be diff class).
 */
public class ServerStaminaData extends StaminaData {
    /** Static Fields for NBT Data **/
    public static final String STAMINA = "stamina";

    private boolean isMob;
    private boolean isSwinging;
    private float swingStaminaCost;
    private int swingDuration;
    private ServerPlayer serverPlayer = null;
    private boolean hasPlayerJumped;

    public ServerStaminaData() {
        this(false);
    }

    public ServerStaminaData(boolean isMob) {
        this.isMob = isMob;
    }


    public ServerStaminaData(ServerPlayer serverPlayer) {
        super(serverPlayer);
        this.serverPlayer = serverPlayer;
    }

    public boolean isSwinging() {
        return this.isSwinging;
    }

    public void setSwinging(boolean isSwinging) {
        this.isSwinging = isSwinging;
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

    @Override
    public void tickStamina() {
        this.tickStaminaRegen();

        this.tickSprintStaminaCost();
        this.tickSwimStaminaCost();
        this.tickJumpStaminaCost();
        this.tickAttackStaminaCost();
    }

    public void setStamina(float stamina) {
        ChangeStaminaEvent event = new ChangeStaminaEvent(this.player, this, this.stamina, stamina);
        if (!NeoForge.EVENT_BUS.post(event).isCanceled()) {
            this.stamina = event.getNewStamina();
        }

        if (this.stamina > this.maxStamina) {
            this.stamina = this.maxStamina;
        }
        if (this.stamina <= 0) {
            this.stamina = 0;
        }
        SimpleStamina.LOGGER.info("NEED TO SEND TO CLIENT. stamina to set: {}", stamina);
        this.sendToClient = true;
    }

    private void tickStaminaRegen() {

        if (!doStaminaRegen()) return;

        if (stamina == this.maxStamina) {
            return;
        }

        var increment = StaminaUtils.calculateStaminaRegenIncrement(serverPlayer);

        var staminaToSet = this.getStaminaToAdd(increment);
        this.setStamina(staminaToSet);
    }

    private boolean doStaminaRegen() {
        if (this.stamina < this.maxStamina) {
            if (STAMINA_REGEN_COOLDOWN > 0) {
                STAMINA_REGEN_COOLDOWN--;
            }

            return STAMINA_REGEN_COOLDOWN == 0;
        }
        else {
            return false;
        }
    }

    private void tickSprintStaminaCost() {
        if (!this.serverPlayer.isSprinting()) return;

        var staminaCost = StaminaUtils.calculateSprintStaminaCost(this.serverPlayer);
        var staminaToSet = this.getStaminaToSet(staminaCost);
        this.setStamina(staminaToSet);
    }

    private void tickSwimStaminaCost() {
        if (!this.serverPlayer.isSwimming()) return;

        var staminaCost = StaminaUtils.calculateSwimStaminaCost(this.serverPlayer);
        var staminaToSet = this.getStaminaToSet(staminaCost);
        this.setStamina(staminaToSet);
    }

    private void tickJumpStaminaCost() {
        if (!this.hasPlayerJumped) return;

        var staminaCost = StaminaUtils.calculateJumpStaminaCost(this.serverPlayer);
        var staminaToSet = this.getStaminaToSet(staminaCost);
        this.setStamina(staminaToSet);
        this.hasPlayerJumped = false;
    }

    // TODO: Maybe just send the total amount of stamina cost to the client side and have that phantom amount remain
    //       and tick down from the client like in dark souls. Will clog the network up less, and will put less
    //       calculations on the server. Also, it'll be converted to an integer client side, so it'll be even easier.
    //       Also, have it to where the phantom amount doesn't tick down until the client stops receiving updates for
    //       at least a second or two, and if the client receives another update just pause the reduction (unless 0).
    //       That should keep the bar there and not have any wonky movement.

    // TODO: ALSO, have the actual amount tick down gradually instead of suddenly, but still have it happen pretty fast.
    //       Maybe divide the amount to remove by 10 ticks so it all can be processed in half a second? Test it out.
    private void tickAttackStaminaCost() {
        if (this.isSwinging) {
            SimpleStamina.LOGGER.info("current swing duration: {} swing time: {}",
                    serverPlayer.getCurrentSwingDuration(),
                    serverPlayer.getCurrentItemAttackStrengthDelay());
            this.tickSwingDuration();
        }
        // For whatever reason the player is swinging when they're sleeping? Have to account for that here.
        else if (this.serverPlayer.swinging && !this.serverPlayer.isSleeping()) {
            var attackDelay = serverPlayer.getCurrentItemAttackStrengthDelay();
            var staminaCost = StaminaUtils.calculateAttackStaminaCost(this.serverPlayer);

            this.swingTool((int) attackDelay, staminaCost);
        }
    }

    private void swingTool(int swingDuration, float toolStaminaCost) {
        this.setSwingDuration(swingDuration);
        this.getStaminaToSet(toolStaminaCost);
        this.setSwingStaminaCost(toolStaminaCost);
        this.setSwinging(true);
    }

    // TODO: Maybe just send the total amount of stamina cost to the client side and have that phantom amount remain
    //       and tick down from the client like in dark souls. Will clog the network up less, and will put less
    //       calculations on the server. Also, it'll be converted to an integer client side, so it'll be even easier.
    //       Also, have it to where the phantom amount doesn't tick down until the client stops receiving updates for
    //       at least a second or two, and if the client receives another update just pause the reduction (unless 0).
    //       That should keep the bar there and not have any wonky movement.

    // TODO: ALSO, have the actual amount tick down gradually instead of suddenly, but still have it happen pretty fast.
    //       Maybe divide the amount to remove by 10 ticks so it all can be processed in half a second? Test it out.
    public void blockAttack(float damageBlocked) {
        this.getStaminaToSet(damageBlocked);
    }

    private void tickSwingDuration() {
//        SimpleStamina.LOGGER.info("tick swing duration: {}", this.swingDuration);
        if (this.swingDuration > 0  && this.stamina > 0) {
            var staminaToSet = this.getStaminaToSet(this.swingStaminaCost);
            this.setStamina(staminaToSet);
            this.swingDuration--;
        }
        else {
            this.setSwingStaminaCost(0);
            this.setSwinging(false);
        }
    }

    public void saveNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putInt(STAMINA, (int) stamina);
    }

    public void loadNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        this.stamina = compoundTag.getInt(STAMINA);
    }

    public static ServerStaminaData getPlayerStaminaData(LivingEntity livingEntity) {
        return livingEntity.getData(DataAttachmentRegistry.SERVER_STAMINA_DATA);
    }
}
