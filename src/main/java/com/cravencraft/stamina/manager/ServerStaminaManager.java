package com.cravencraft.stamina.manager;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.capability.ServerStaminaData;
import com.cravencraft.stamina.config.ServerConfigs;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import com.cravencraft.stamina.utils.StaminaUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.cravencraft.stamina.capability.StaminaData.SEGMENT_STAMINA_AMOUNT;
import static com.cravencraft.stamina.config.ServerConfigs.*;
import static com.cravencraft.stamina.registries.AttributeRegistry.MAX_STAMINA;
import static com.cravencraft.stamina.registries.DatapackRegistry.RANGED_WEAPONS_STAMINA_VALUES;

public class ServerStaminaManager extends StaminaManager {
    public static final ResourceLocation ATTACK_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(SimpleStamina.MODID, "attack_speed_modifier");

    // TODO: Need to factor in exhaustion attributes here as well.
    public void onPlayerJoin(ServerPlayer serverPlayer) {
        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
//        serverStaminaData.updateMaxStaminaOnPlayerJoin();
        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(serverStaminaData));
    }

    public void tick(ServerPlayer serverPlayer) {
        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);

        // TODO: Maybe setup a tick increment here, and factor that in to how much is drained
        //       from the below methods. Should be straightforward. Larger tick increments = more choppy bar movement,
        //       but less network traffic.
        // TODO: Add the separate ticks here. THIS IS WHERE YOU GO TO. REMOVE THE tickStamina METHOD FROM THIS CLASS
        //      AND JUST ADD THE TICKS FROM THE ServerStaminaData CLASS TO HERE SO YOU CAN ADD THE TOGGLES AND SEPARATE
        //      THE RESPONSIBILITIES OF THE DATA CLASS.
        this.tickStaminaRegen(serverPlayer, serverStaminaData);

        this.tickSprintStaminaCost(serverPlayer, serverStaminaData);
        this.tickSwimStaminaCost(serverPlayer, serverStaminaData);
        this.tickAttackStaminaCost(serverPlayer, serverStaminaData);

        if (serverPlayer.tickCount % 20 == 0) {
            this.tickStaminaExhaustion(serverStaminaData);
        }


        this.setExhaustionEffects(serverPlayer, serverStaminaData);
    }

    // TODO: Rename this and the field.
    public void modifyStaminaConsumedBeforeExhaustion(ServerStaminaData serverStaminaData) {
        SimpleStamina.LOGGER.info("total stamina consumed: {}", serverStaminaData.getTotalStaminaConsumed());
        if (serverStaminaData.getTotalStaminaConsumed() < serverStaminaData.getSegmentExhaustionLimit()) return;

        int exhaustionToAdd = (int) (serverStaminaData.getTotalStaminaConsumed() / serverStaminaData.getSegmentExhaustionLimit());
        var playerExhaustionLevel = serverStaminaData.getPlayerExhaustionLevel();
        var staminaConsumedToRemove = serverStaminaData.getTotalStaminaConsumed() - (exhaustionToAdd * serverStaminaData.getSegmentExhaustionLimit());
        // TODO: If I set exhaustion to go below 25 segments, then this could potentially add up.
        //  There will at the very least be several issues to address if I want total stamina to actually hit 0.
        serverStaminaData.setTotalStaminaConsumed(staminaConsumedToRemove);
        if (playerExhaustionLevel >= 100) return;

        serverStaminaData.setPlayerExhaustionLevel(playerExhaustionLevel + exhaustionToAdd);

    }

    // TODO: This SHOULD work fairly well, and not be TOO taxing. I can limit this to only being called every 20 ticks
    //       to limit its impact. Changing max stamina with a second delay shouldn't be too big of a gameplay concern.
    public void modifyMaxStaminaBasedOnExhaustion(ServerStaminaData serverStaminaData) {
        SimpleStamina.LOGGER.info("player exhaustion level before setting: {}", serverStaminaData.getPlayerExhaustionLevel());
        var staminaNotExhausted = serverStaminaData.player.getAttributeValue(MAX_STAMINA) - serverStaminaData.getPlayerExhaustionLevel();
        int newMaxStamina;
        if (serverStaminaData.getMaxStamina() < staminaNotExhausted)  {
            newMaxStamina = serverStaminaData.getMaxStamina() + SEGMENT_STAMINA_AMOUNT;
            if (newMaxStamina > staminaNotExhausted) return;

        }
        else {
            newMaxStamina = serverStaminaData.getMaxStamina() - SEGMENT_STAMINA_AMOUNT;
            if (newMaxStamina < staminaNotExhausted) return;


        }

        SimpleStamina.LOGGER.info("player exhaustion level AFTER setting: {}", serverStaminaData.getPlayerExhaustionLevel());
        serverStaminaData.setMaxStamina(newMaxStamina);
    }

    public void tickStaminaExhaustion(ServerStaminaData serverStaminaData) {
        modifyStaminaConsumedBeforeExhaustion(serverStaminaData);
        modifyMaxStaminaBasedOnExhaustion(serverStaminaData);
    }

    public void tickSprintStaminaCost(ServerPlayer serverPlayer, ServerStaminaData serverStaminaData) {
        if (!TOGGLE_SPRINT_STAMINA.get() || !serverPlayer.isSprinting()) return;

        var staminaCost = StaminaUtils.calculateSprintStaminaCost(serverPlayer);
        var staminaToSet = serverStaminaData.getStaminaAfterRemove(staminaCost);
        serverStaminaData.setStamina(staminaToSet);
    }

    public void tickSwimStaminaCost(ServerPlayer serverPlayer, ServerStaminaData serverStaminaData) {
        if (!TOGGLE_SWIM_STAMINA.get() || !serverPlayer.isSwimming()) return;

        var staminaCost = StaminaUtils.calculateSwimStaminaCost(serverPlayer);
        var staminaToSet = serverStaminaData.getStaminaAfterRemove(staminaCost);
        serverStaminaData.setStamina(staminaToSet);
    }

    // TODO: Maybe just send the total amount of stamina cost to the client side and have that phantom amount remain
    //       and tick down from the client like in dark souls. Will clog the network up less, and will put less
    //       calculations on the server. Also, it'll be converted to an integer client side, so it'll be even easier.
    //       Also, have it to where the phantom amount doesn't tick down until the client stops receiving updates for
    //       at least a second or two, and if the client receives another update just pause the reduction (unless 0).
    //       That should keep the bar there and not have any wonky movement.
    public void tickAttackStaminaCost(ServerPlayer serverPlayer, ServerStaminaData serverStaminaData) {
        if (!TOGGLE_MELEE_ATTACK_STAMINA.get()) return;

        if (serverStaminaData.getSwingTick() < serverStaminaData.getSwingDuration()) {
            serverStaminaData.setSwingTick(serverStaminaData.getSwingTick() + 1);
        }
        // For whatever reason the player is swinging when they're sleeping? Have to account for that here.
        else if (serverPlayer.swinging && !serverPlayer.isSleeping()) {
            serverStaminaData.setSwingTick(0);
            this.modifyAttackSpeed(serverPlayer, serverStaminaData.getStamina());
            this.swingTool(serverPlayer, serverStaminaData);
        }
    }

    public void playerJump(ServerPlayer serverPlayer) {
        if (!TOGGLE_JUMP_STAMINA.get()) return;

        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
        var staminaCost = StaminaUtils.calculateJumpStaminaCost(serverPlayer);
        var staminaToSet = serverStaminaData.getStaminaAfterRemove(staminaCost);
        serverStaminaData.setStamina(staminaToSet);
    }

    public void playerBlockAttack(ServerPlayer serverPlayer, float blockAmount) {
        if (!TOGGLE_BLOCK_ATTACK_STAMINA.get()) return;

        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
        var staminaCost = StaminaUtils.calculateBlockStaminaCost(serverPlayer, blockAmount);
        var staminaToSet = serverStaminaData.getStaminaAfterRemove(staminaCost);
        if (staminaToSet == 0.0f) serverPlayer.disableShield();
        serverStaminaData.setStamina(staminaToSet);
    }

    // TODO: Should probably set delay ticks here. Like every 5 ticks maybe.
    // TODO: Still have to work on this some. Need to disable the item indefinitely until stamina is above 0. Maybe
    //          Mixin to whatever method enabled the field startUsingItem?
    public void useRangedWeapon(ServerPlayer serverPlayer, ItemStack itemStack) {
        var serverPlayerStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);

        if (StaminaUtils.getDataPackItemValue(RANGED_WEAPONS_STAMINA_VALUES, itemStack) == 0) return;

        if (serverPlayerStaminaData.getStamina() <= 0) {
            serverPlayer.stopUsingItem();
            return;
        }

        var staminaCost = StaminaUtils.calculateDrawBowStaminaCost(serverPlayer, itemStack);
        var staminaToSet = serverPlayerStaminaData.getStaminaAfterRemove(staminaCost);
        serverPlayerStaminaData.setStamina(staminaToSet);
    }

    public void restorePlayerStaminaAfterSleeping(ServerPlayer serverPlayer) {
//        serverPlayer.startSleepInBed()
        var serverPlayerStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
        serverPlayerStaminaData.removePlayerExhaustionAttributeModifications();
        serverPlayerStaminaData.removePlayerSegmentExhaustionAttributeModifications();
        serverPlayerStaminaData.restoreMaxStamina();
//        serverPlayerStaminaData.updateMaxStamina();
//        serverPlayerStaminaData.setMaxStamina((int) serverPlayer.getAttributeValue(MAX_STAMINA));

    }

    /**
     * TODO: Have a server config option to fully disable attack damage when exhausted
     *     (by default, I might just have it reduce attack speed and damage by a certain percentage. Gotta set that up).
     * @param serverPlayer
     * @param serverStaminaData
     */
    private void setExhaustionEffects(ServerPlayer serverPlayer, ServerStaminaData serverStaminaData) {
        var stamina = serverStaminaData.getStamina();

        if (stamina > 0.0f) return;

        this.disableSprint(serverPlayer);
        this.disableSwim(serverPlayer);
    }

    private void disableSprint(ServerPlayer serverPlayer) {
        if (!TOGGLE_SPRINT_STAMINA.get() || !serverPlayer.isSprinting()) return;

        serverPlayer.setSprinting(false);
    }

    private void disableSwim(ServerPlayer serverPlayer) {
        if (!TOGGLE_SWIM_STAMINA.get() || !serverPlayer.isSwimming()) return;

        serverPlayer.setSwimming(false);
    }

    private void tickStaminaRegen(ServerPlayer serverPlayer, ServerStaminaData serverStaminaData) {
        if (!doStaminaRegen(serverStaminaData) || serverStaminaData.getStamina() == serverStaminaData.getMaxStamina())
            return;

        var increment = StaminaUtils.calculateStaminaRegenIncrement(serverPlayer);

        var staminaToSet = serverStaminaData.getStaminaAfterAdd(increment);
        serverStaminaData.setStamina(staminaToSet);
    }

    private boolean doStaminaRegen(ServerStaminaData serverStaminaData) {
        if (serverStaminaData.getStamina() < serverStaminaData.getMaxStamina()) {
            if (STAMINA_REGEN_COOLDOWN > 0) {
                STAMINA_REGEN_COOLDOWN--;
            }

            return STAMINA_REGEN_COOLDOWN == 0;
        }
        else {
            return false;
        }
    }

    private void modifyAttackSpeed(ServerPlayer serverPlayer, float stamina) {
        AttributeInstance attackSpeedAttribute = serverPlayer.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            if (stamina > 0.0f) {
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
            serverPlayer.resetAttackStrengthTicker();
        }
    }

    private void swingTool(ServerPlayer serverPlayer, ServerStaminaData serverStaminaData) {

        var currentSwingDuration = (int) serverPlayer.getCurrentItemAttackStrengthDelay();
        var toolStaminaCost = StaminaUtils.calculateAttackStaminaCost(serverPlayer);
        var staminaAfterRemove = serverStaminaData.getStaminaAfterRemove(toolStaminaCost);
        serverStaminaData.setStamina(staminaAfterRemove);

        // If the swing duration field hasn't been set yet, and stamina is 0, then set it to an arbitrary 20 to avoid headaches.
        if (serverStaminaData.getStamina() <= 0) {
            serverStaminaData.setSwingDuration(20);
            return;
        }

        // If the current swing duration is equal to the set swing duration field, then keep using the set swing duration.
        if (serverStaminaData.getSwingDuration() == currentSwingDuration) return;


        serverStaminaData.setSwingDuration((int) serverPlayer.getCurrentItemAttackStrengthDelay());
    }
}
