package com.hm.efn.network;

import com.hm.efn.EFN;
import com.hm.efn.event.TickChange;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TickChangePacket(float percent) implements CustomPacketPayload {
   public static final Type<TickChangePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EFN.MODID, "tick_change"));
   public static final StreamCodec<RegistryFriendlyByteBuf, TickChangePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.FLOAT,
      TickChangePacket::percent,
      TickChangePacket::new
   );

   @Override
   public @NotNull Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handleServerbound(TickChangePacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer) {
            TickChange.PERCENT = packet.percent();
            PacketDistributor.sendToAllPlayers(new TickChangePacket(packet.percent()));
         }
      });
   }

   public static void handleClientbound(TickChangePacket packet, IPayloadContext context) {
      context.enqueueWork(() -> TickChange.PERCENT = packet.percent());
   }
}
