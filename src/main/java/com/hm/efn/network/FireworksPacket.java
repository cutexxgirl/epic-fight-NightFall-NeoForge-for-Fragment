package com.hm.efn.network;

import com.hm.efn.EFN;
import com.hm.efn.event.PlayerRandomFireworksClientEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record FireworksPacket(boolean enabled) implements CustomPacketPayload {
   public static final Type<FireworksPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EFN.MODID, "fireworks"));
   public static final StreamCodec<RegistryFriendlyByteBuf, FireworksPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL,
      FireworksPacket::enabled,
      FireworksPacket::new
   );

   @Override
   public @NotNull Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handleClientbound(FireworksPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> PlayerRandomFireworksClientEvent.setEnabled(packet.enabled()));
   }
}
