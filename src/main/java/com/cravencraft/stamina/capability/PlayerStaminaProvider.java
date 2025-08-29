package com.cravencraft.stamina.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStaminaProvider implements IAttachmentSerializer<CompoundTag, StaminaData> {
    @Override
    public @NotNull StaminaData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        var staminaData = holder instanceof ServerPlayer serverPlayer ? new StaminaData(serverPlayer) : new StaminaData(true);
        staminaData.loadNBTData(compoundTag, provider);
        return staminaData;
    }

    @Override
    public @Nullable CompoundTag write(StaminaData playerStamina, HolderLookup.@NotNull Provider provider) {
        var compoundTag = new CompoundTag();
        playerStamina.saveNBTData(compoundTag, provider);
        return compoundTag;
    }
}
