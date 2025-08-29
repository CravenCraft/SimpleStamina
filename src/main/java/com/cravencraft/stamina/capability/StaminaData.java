package com.cravencraft.stamina.capability;

import com.cravencraft.stamina.events.ChangeStaminaEvent;
import com.cravencraft.stamina.registries.AttributeRegistry;
import com.cravencraft.stamina.registries.DataAttachmentRegistry;
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
public class StaminaData {
    /** Static Fields for NBT Data **/
    public static final String STAMINA = "stamina";

    private boolean isMob;
    private float stamina;
    private ServerPlayer serverPlayer = null;

    public StaminaData(boolean isMob) {
        this.isMob = isMob;
    }

    public StaminaData() {
        this(false);
    }

    public StaminaData(ServerPlayer serverPlayer) {
        this(false);
        this.serverPlayer = serverPlayer;
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
            float maxStamina = (float) serverPlayer.getAttributeValue(AttributeRegistry.MAX_STAMINA);
            if (this.stamina > maxStamina) {
                this.stamina = maxStamina;
            }
            if (this.stamina <= 0) {
                this.stamina = 0;
            }
        }
    }

    public void addStamina(float stamina) {
        setStamina(this.stamina + stamina);
    }

    public void removeStamina(float stamina) {
        setStamina(this.stamina - stamina);
    }

    public static StaminaData getPlayerStaminaData(LivingEntity livingEntity) {
        return livingEntity.getData(DataAttachmentRegistry.STAMINA_DATA);
    }

    public void saveNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putInt(STAMINA, (int) stamina);
    }

    public void loadNBTData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        stamina = compoundTag.getInt(STAMINA);
    }

}
