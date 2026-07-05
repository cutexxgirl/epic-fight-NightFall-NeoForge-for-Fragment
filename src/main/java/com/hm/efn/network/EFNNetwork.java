package com.hm.efn.network;

import com.hm.efn.EFN;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class EFNNetwork {
   private EFNNetwork() {
   }

   public static void registerPackets(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar(EFN.MODID).versioned("1").optional();
      registrar.playBidirectional(
         TickChangePacket.TYPE,
         TickChangePacket.STREAM_CODEC,
         new DirectionalPayloadHandler<>(TickChangePacket::handleClientbound, TickChangePacket::handleServerbound)
      );
      registrar.playToClient(SoulAfterimagePacket.TYPE, SoulAfterimagePacket.STREAM_CODEC, SoulAfterimagePacket::handleClientbound);
      registrar.playToClient(FireworksPacket.TYPE, FireworksPacket.STREAM_CODEC, FireworksPacket::handleClientbound);
      registrar.playToClient(PlayTotemAnimationPacket.TYPE, PlayTotemAnimationPacket.STREAM_CODEC, PlayTotemAnimationPacket::handleClientbound);
   }
}
