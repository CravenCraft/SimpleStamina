package com.cravencraft.stamina.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStaminaProvider implements IAttachmentSerializer<CompoundTag, ServerStaminaData> {
    @Override
    public @NotNull ServerStaminaData read(@NotNull IAttachmentHolder holder, @NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        var serverStaminaData = holder instanceof ServerPlayer serverPlayer ? new ServerStaminaData(serverPlayer) : new ServerStaminaData(true);
        serverStaminaData.loadNBTData(compoundTag, provider);
        return serverStaminaData;
    }

    @Override
    public @Nullable CompoundTag write(ServerStaminaData playerStamina, HolderLookup.@NotNull Provider provider) {
        var compoundTag = new CompoundTag();
        playerStamina.saveNBTData(compoundTag, provider);
        return compoundTag;
    }
}
