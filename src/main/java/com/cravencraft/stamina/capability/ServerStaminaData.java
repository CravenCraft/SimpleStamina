package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.events.ChangeStaminaEvent;
import com.cravencraft.stamina.network.SyncStaminaPacket;
import com.cravencraft.stamina.registries.DataAttachmentRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.cravencraft.stamina.manager.StaminaManager.STAMINA_REGEN_COOLDOWN;
import static com.cravencraft.stamina.manager.StaminaManager.STAMINA_REGEN_TICKS;
import static com.cravencraft.stamina.registries.AttributeRegistry.MAX_STAMINA;

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
    private int swingTick;
    private int swingDuration;
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

    public int getSwingTick() {
        return this.swingTick;
    }

    public void setSwingTick(int swingTick) {
        this.swingTick = swingTick;
    }

    public int getSwingDuration() {
        return this.swingDuration;
    }

    public void setSwingDuration(int swingDuration) {
        this.swingDuration = swingDuration;
    }

    @Override
    public void setMaxStamina(int maxStamina) {
        super.setMaxStamina(maxStamina);
        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(this));
    }

    public void restoreMaxStamina() {
        this.maxStamina = (int) this.player.getAttributeValue(MAX_STAMINA);
        PacketDistributor.sendToPlayer(serverPlayer, new SyncStaminaPacket(this));
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

    @Override
    public float getStaminaAfterRemove(float stamina) {
        STAMINA_REGEN_COOLDOWN = STAMINA_REGEN_TICKS;
        if (this.stamina <= 0.0f) return 0.0f;

        var playerMaxStamina = (float) this.player.getAttributeValue(MAX_STAMINA);
        var staminaToSet = Mth.clamp(stamina, 0.0f, playerMaxStamina);

        iterateSegmentExhaustion(staminaToSet);

        return Math.max(this.stamina - staminaToSet, 0);
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

    private void iterateSegmentExhaustion(double staminaToRemove) {
        if (this.maxStamina <= SEGMENT_STAMINA_AMOUNT) return;

        this.totalStaminaConsumed = this.totalStaminaConsumed + staminaToRemove;

        SimpleStamina.LOGGER.info("iterate segment exhaustion. total stamina consumed: {}", this.totalStaminaConsumed);
    }
}
