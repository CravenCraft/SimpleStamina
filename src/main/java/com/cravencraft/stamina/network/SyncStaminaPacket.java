package com.cravencraft.stamina.network;

import com.cravencraft.stamina.SimpleStamina;
import com.cravencraft.stamina.capability.StaminaData;
import com.cravencraft.stamina.manager.ClientStaminaManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class SyncStaminaPacket implements CustomPacketPayload {
    private int playerStamina = 0;

    private StaminaData staminaData = null;

    public static final CustomPacketPayload.Type<SyncStaminaPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SimpleStamina.MODID, "sync_stamina"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncStaminaPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncStaminaPacket::write, SyncStaminaPacket::new);

    public SyncStaminaPacket(StaminaData staminaData) {
        this.staminaData = staminaData;
    }

    public SyncStaminaPacket(FriendlyByteBuf buf) {
        this.playerStamina = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt((int) this.staminaData.getStamina());
    }

    public static void handle(SyncStaminaPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            SimpleStamina.LOGGER.info("SENDING STAMINA TO THE CLIENT: {}", packet.playerStamina);

            if (ClientStaminaManager.getClientStaminaData() == null) {
                SimpleStamina.LOGGER.info("CLIENT STAMINA IS NULL.");
                if (Minecraft.getInstance().player != null) {
                    ClientStaminaManager.onPlayerJoin(packet.playerStamina);
                }
                else {
                    SimpleStamina.LOGGER.info("CLIENT PLAYER IS NULL.");
                }
            }
            else {
                ClientStaminaManager.setStaminaFromServer(packet.playerStamina);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
