package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.events.ChangeStaminaEvent;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import com.cravencraft.stamina.registries.DataAttachmentRegistry;
import com.cravencraft.stamina.utils.StaminaUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.cravencraft.stamina.registries.AttributeRegistry.MAX_STAMINA;

/**
 * TODO: Ok, got the basics of the event system down. Need to decide how I want to do this, and if I want to break this
 *       down to an event package that calls the various classes, or have a server and client side for the player.
 *       Will decide on this in a bit after practicing with everything more.
 *       Will work by extending from LocalPlayer and ServerPlayer. Have the calculations done in the server player,
 *       then sent to the local player for various things such as displaying the GUI and such (GUI might be diff class).
 */
public class StaminaData {
    /** Static Fields for NBT Data **/
    public static final String STAMINA = "stamina";

    /** Static fields for stamina regen ticks */
    public static final int STAMINA_REGEN_TICKS = 60; // TODO: Probably wanna have this as a server config.
    public static int STAMINA_REGEN_COOLDOWN = 0;

    private boolean isMob;
    private boolean isSwinging;
    private boolean sendToClient;
    private int maxStamina;
    private float stamina;
    private float swingStaminaCost;
    private int swingDuration;
    private ServerPlayer serverPlayer = null;
    private boolean hasPlayerJumped;

    public StaminaData(boolean isMob) {
        this.isMob = isMob;
    }

    public StaminaData() {
        this(false);
    }

    public StaminaData(ServerPlayer serverPlayer) {
        this(false);
        this.serverPlayer = serverPlayer;
        this.maxStamina = (int) this.serverPlayer.getAttributeValue(MAX_STAMINA);
    }

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

    public void tickStamina() {
        this.tickStaminaRegen();

        this.tickSprintStaminaCost();
        this.tickSwimStaminaCost();
        this.tickJumpStaminaCost();
        this.tickAttackStaminaCost();
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

    public void setStamina(float stamina) {
        ChangeStaminaEvent event = new ChangeStaminaEvent(this.serverPlayer, this, this.stamina, stamina);
        if (this.serverPlayer == null || !NeoForge.EVENT_BUS.post(event).isCanceled()) {
            this.stamina = event.getNewStamina();
        }

        if (this.serverPlayer != null) {
            if (this.stamina > this.maxStamina) {
                this.stamina = this.maxStamina;
            }
            if (this.stamina <= 0) {
                this.stamina = 0;
            }
        }
        this.sendToClient = true;
    }

    public void addStamina(float stamina) {
        if (this.stamina >= this.maxStamina) return;

        var staminaToSet = (Mth.clamp(stamina, 0.0f, this.maxStamina));
        setStamina(this.stamina + staminaToSet);
    }

    public void removeStamina(float stamina) {
        STAMINA_REGEN_COOLDOWN = STAMINA_REGEN_TICKS;
        if (this.stamina <= 0.0f) return;

        var playerMaxStamina = (float) this.serverPlayer.getAttributeValue(MAX_STAMINA);
        var staminaToSet = Mth.clamp(stamina, 0.0f, playerMaxStamina);
        staminaToSet = this.stamina - stamina;
        staminaToSet = (staminaToSet > 1) ? staminaToSet : 0;
        setStamina(staminaToSet);
    }

    private void tickStaminaRegen() {

        if (!doStaminaRegen()) return;

        if (stamina == this.maxStamina) {
            return;
        }

        var increment = StaminaUtils.calculateStaminaRegenIncrement(serverPlayer);

        this.addStamina(increment);

        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(this));
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
        this.removeStamina(staminaCost);
    }

    private void tickSwimStaminaCost() {
        if (!this.serverPlayer.isSwimming()) return;

        var staminaCost = StaminaUtils.calculateSwimStaminaCost(this.serverPlayer);
        this.removeStamina(staminaCost);
    }

    private void tickJumpStaminaCost() {
        if (!this.hasPlayerJumped) return;

        var staminaCost = StaminaUtils.calculateJumpStaminaCost(this.serverPlayer);
        this.removeStamina(staminaCost);
        this.hasPlayerJumped = false;
    }

    private void tickAttackStaminaCost() {
        if (this.isSwinging) {
            SimpleStamina.LOGGER.info("current swing duration: {} swing time: {}",
                    serverPlayer.getCurrentSwingDuration(),
                    serverPlayer.getCurrentItemAttackStrengthDelay());
            this.tickSwingDuration();
        }
        else if (this.serverPlayer.swinging) {
            var attackDelay = serverPlayer.getCurrentItemAttackStrengthDelay();
            var staminaCost = StaminaUtils.calculateAttackStaminaCost(this.serverPlayer);

            this.swingTool((int) attackDelay, staminaCost);
        }
    }

    private void swingTool(int swingDuration, float toolStaminaCost) {
        this.setSwingDuration(swingDuration);
        this.removeStamina(toolStaminaCost);
        this.setSwingStaminaCost(toolStaminaCost);
        this.setSwinging(true);
    }

    private void tickSwingDuration() {
        SimpleStamina.LOGGER.info("tick swing duration: {}", this.swingDuration);
        if (this.swingDuration > 0  && this.stamina > 0) {
            this.removeStamina(this.swingStaminaCost);
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

    public static StaminaData getPlayerStaminaData(LivingEntity livingEntity) {
        return livingEntity.getData(DataAttachmentRegistry.STAMINA_DATA);
    }
}
