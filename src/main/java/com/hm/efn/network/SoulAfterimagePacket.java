package com.hm.efn.network;

import com.hm.efn.EFN;
import com.hm.efn.particle.EFNParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SoulAfterimagePacket(int targetId, double x, double y, double z) implements CustomPacketPayload {
   public static final Type<SoulAfterimagePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EFN.MODID, "soul_afterimage"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SoulAfterimagePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT,
      SoulAfterimagePacket::targetId,
      ByteBufCodecs.DOUBLE,
      SoulAfterimagePacket::x,
      ByteBufCodecs.DOUBLE,
      SoulAfterimagePacket::y,
      ByteBufCodecs.DOUBLE,
      SoulAfterimagePacket::z,
      SoulAfterimagePacket::new
   );

   public SoulAfterimagePacket(LivingEntity target) {
      this(target.getId(), target.getX(), target.getY(), target.getZ());
   }

   @Override
   public @NotNull Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handleClientbound(SoulAfterimagePacket packet, IPayloadContext context) {
      context.enqueueWork(() -> ClientHandler.handlePacket(packet));
   }

   private static final class ClientHandler {
      private static void handlePacket(SoulAfterimagePacket packet) {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player != null && player.level().isClientSide()) {
            player.level()
               .addParticle((ParticleOptions)EFNParticles.SOUL_AFTERIMAGE.get(), packet.x(), packet.y(), packet.z(), Double.longBitsToDouble(packet.targetId()), 0.0, 0.0);
         }
      }
   }
}
