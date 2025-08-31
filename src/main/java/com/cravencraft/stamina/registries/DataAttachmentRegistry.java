package com.cravencraft.stamina.registries;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.capability.PlayerStaminaProvider;
import com.cravencraft.stamina.capability.ServerStaminaData;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DataAttachmentRegistry {
    private static final String STAMINA_DATA_NAME = "stamina_data";
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SimpleStamina.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ServerStaminaData>> SERVER_STAMINA_DATA = ATTACHMENT_TYPES.register(STAMINA_DATA_NAME,
            () -> AttachmentType.builder((holder) -> holder instanceof ServerPlayer serverPlayer ? new ServerStaminaData(serverPlayer) : new ServerStaminaData()).serialize(new PlayerStaminaProvider()).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}

