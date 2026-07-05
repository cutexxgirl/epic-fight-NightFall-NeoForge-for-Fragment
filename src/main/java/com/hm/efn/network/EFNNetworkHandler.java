package com.hm.efn.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class EFNNetworkHandler {
   public static void sendToAllPlayersTrackingEntity(LivingEntity target, SoulAfterimagePacket packet) {
      if (target.level() instanceof ServerLevel serverLevel) {
         PacketDistributor.sendToPlayersTrackingEntity(target, packet);
      }
   }

   public static void sendToPlayer(ServerPlayer player, SoulAfterimagePacket packet) {
      PacketDistributor.sendToPlayer(player, packet);
   }

   public static void sendToAllPlayersInRange(Player source, LivingEntity target, double range) {
      if (source.level() instanceof ServerLevel serverLevel) {
         SoulAfterimagePacket packet = new SoulAfterimagePacket(target);

         for (ServerPlayer player : serverLevel.players()) {
            if (player.distanceToSqr(source) <= range * range) {
               sendToPlayer(player, packet);
            }
         }
      }
   }
}
