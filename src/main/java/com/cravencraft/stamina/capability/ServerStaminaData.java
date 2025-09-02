package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.config.ServerConfigs;
import com.cravencraft.stamina.events.ChangeStaminaEvent;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import com.cravencraft.stamina.registries.DataAttachmentRegistry;
import com.cravencraft.stamina.utils.StaminaUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.cravencraft.stamina.manager.ServerStaminaManager.ATTACK_SPEED_MODIFIER;

/**
 * TODO: Ok, got the basics of the event system down. Need to decide how I want to do this, and if I want to break this
 *       down to an event package that calls the various classes, or have a server and client side for the player.
 *       Will decide on this in a bit after practicing with everything more.
 *       Will work by extending from LocalPlayer and ServerPlayer. Have the calculations done in the server player,
 *       then sent to the local player for various things such as displaying the GUI and such (GUI might be diff class).
 */
public class ServerStaminaData extends StaminaData {
    /** Static Fields for NBT Data **/
    private static final String STAMINA = "stamina";

    private boolean isMob;
    private int swingDuration;
    private int swingTick;
    private ServerPlayer serverPlayer = null;

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

    public void playerJump() {
        var staminaCost = StaminaUtils.calculateJumpStaminaCost(this.serverPlayer);
        var staminaToSet = this.getStaminaAfterRemove(staminaCost);
        this.setStamina(staminaToSet);
    }

    @Override
    public void tickStamina() {
        this.tickStaminaRegen();

        this.tickSprintStaminaCost();
        this.tickSwimStaminaCost();
        this.tickAttackStaminaCost();
        this.tickRangedAttackStaminaCost();
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

        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(this));
    }

    private void tickStaminaRegen() {

        if (!doStaminaRegen()) return;

        if (stamina == this.maxStamina) {
            return;
        }

        var increment = StaminaUtils.calculateStaminaRegenIncrement(serverPlayer);

        var staminaToSet = this.getStaminaAfterAdd(increment);
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
        var staminaToSet = this.getStaminaAfterRemove(staminaCost);
        this.setStamina(staminaToSet);
    }

    private void tickSwimStaminaCost() {
        if (!this.serverPlayer.isSwimming()) return;

        var staminaCost = StaminaUtils.calculateSwimStaminaCost(this.serverPlayer);
        var staminaToSet = this.getStaminaAfterRemove(staminaCost);
        this.setStamina(staminaToSet);
    }

    private void tickRangedAttackStaminaCost() {
        if (!this.serverPlayer.isUsingItem() || this.stamina <= 0) return;

        var staminaCost = StaminaUtils.calculateDrawBowStaminaCost(this.serverPlayer);
        var staminaToSet = this.getStaminaAfterRemove(staminaCost);
        this.setStamina(staminaToSet);
    }

    // TODO: Maybe just send the total amount of stamina cost to the client side and have that phantom amount remain
    //       and tick down from the client like in dark souls. Will clog the network up less, and will put less
    //       calculations on the server. Also, it'll be converted to an integer client side, so it'll be even easier.
    //       Also, have it to where the phantom amount doesn't tick down until the client stops receiving updates for
    //       at least a second or two, and if the client receives another update just pause the reduction (unless 0).
    //       That should keep the bar there and not have any wonky movement.
    private void tickAttackStaminaCost() {

        if (this.swingTick < this.swingDuration) {
            this.swingTick++;
        }
        // For whatever reason the player is swinging when they're sleeping? Have to account for that here.
        else if (this.serverPlayer.swinging && !this.serverPlayer.isSleeping()) {
            this.swingTick = 0;
            this.modifyAttackSpeed();
            this.swingTool();
        }
    }

    // TODO: Add an attribute modifier to reduce knockback as well.
    private void modifyAttackSpeed() {
        AttributeInstance attackSpeedAttribute = this.serverPlayer.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            if (this.stamina > 0.0f) {
                if (attackSpeedAttribute.hasModifier(ATTACK_SPEED_MODIFIER)) {
                    attackSpeedAttribute.removeModifier(ATTACK_SPEED_MODIFIER);
                }

                return;
            }

            var attackSpeed = attackSpeedAttribute.getValue();
            var newAttackSpeed = attackSpeed * ServerConfigs.ATTACK_SPEED_REDUCTION_MULTIPLIER.get();

            AttributeModifier modifiedAttackSpeed = new AttributeModifier(ATTACK_SPEED_MODIFIER, -newAttackSpeed, AttributeModifier.Operation.ADD_VALUE);
            if (attackSpeedAttribute.hasModifier(ATTACK_SPEED_MODIFIER)) return;
            attackSpeedAttribute.addPermanentModifier(modifiedAttackSpeed);
            this.serverPlayer.resetAttackStrengthTicker();
        }
    }

    private void swingTool() {

        var currentSwingDuration = (int) serverPlayer.getCurrentItemAttackStrengthDelay();
        var toolStaminaCost = StaminaUtils.calculateAttackStaminaCost(this.serverPlayer);
        var staminaAfterRemove = this.getStaminaAfterRemove(toolStaminaCost);
        this.setStamina(staminaAfterRemove);

        // If the swing duration field hasn't been set yet, and stamina is 0, then set it to an arbitrary 20 to avoid headaches.
        if (this.stamina <= 0) {
            this.swingDuration = 20;
            return;
        }

        // If the current swing duration is equal to the set swing duration field, then keep using the set swing duration.
        if (this.swingDuration == currentSwingDuration) return;


        this.swingDuration = (int) serverPlayer.getCurrentItemAttackStrengthDelay();
    }

    // TODO: Maybe just send the total amount of stamina cost to the client side and have that phantom amount remain
    //       and tick down from the client like in dark souls. Will clog the network up less, and will put less
    //       calculations on the server. Also, it'll be converted to an integer client side, so it'll be even easier.
    //       Also, have it to where the phantom amount doesn't tick down until the client stops receiving updates for
    //       at least a second or two, and if the client receives another update just pause the reduction (unless 0).
    //       That should keep the bar there and not have any wonky movement.
    public void blockAttack(float damageBlocked) {
        var staminaCost = StaminaUtils.calculateBlockStaminaCost(this.serverPlayer, damageBlocked);
        var staminaToSet = this.getStaminaAfterRemove(staminaCost);
        if (staminaToSet == 0.0f) this.serverPlayer.disableShield();
        this.setStamina(staminaToSet);
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
