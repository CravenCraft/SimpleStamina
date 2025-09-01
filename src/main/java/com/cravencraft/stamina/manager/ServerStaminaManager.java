package com.cravencraft.stamina.manager;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.capability.ServerStaminaData;
import com.cravencraft.stamina.config.ServerConfigs;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;

public class ServerStaminaManager extends StaminaManager {
    public static final ResourceLocation ATTACK_SPEED_MODIFIER = ResourceLocation.fromNamespaceAndPath(SimpleStamina.MODID, "attack_speed_modifier");

    public void onPlayerJoin(ServerPlayer serverPlayer) {
        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(serverStaminaData));
    }

    public void tick(ServerPlayer serverPlayer) {
        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
        serverStaminaData.tickStamina();

        this.setExhaustionEffects(serverPlayer, serverStaminaData);
    }

    public void playerJump(ServerPlayer serverPlayer) {
        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
        serverStaminaData.playerJump();
    }

    public void playerBlockAttack(ServerPlayer serverPlayer, float blockAmount) {
        var serverStaminaData = ServerStaminaData.getPlayerStaminaData(serverPlayer);
        serverStaminaData.blockAttack(blockAmount);
    }

    /**
     * TODO: Have a server config option to fully disable attack damage when exhausted
     *     (by default, I might just have it reduce attack speed and damage by a certain percentage. Gotta set that up).
     * @param serverPlayer
     * @param serverStaminaData
     */
    public void setExhaustionEffects(ServerPlayer serverPlayer, ServerStaminaData serverStaminaData) {
        var stamina = serverStaminaData.getStamina();

//        this.modifyAttackSpeed(serverPlayer, stamina);
        this.disableSprint(serverPlayer, stamina);
        this.disableSwim(serverPlayer, stamina);
    }

    public void disableSprint(ServerPlayer serverPlayer, float stamina) {
        if (stamina > 0.0f) return;
        if (serverPlayer.isSprinting()) serverPlayer.setSprinting(false);
    }

    public void disableSwim(ServerPlayer serverPlayer, float stamina) {
        if (stamina > 0.0f) return;
        if (serverPlayer.isSwimming()) serverPlayer.setSwimming(false);
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
}
