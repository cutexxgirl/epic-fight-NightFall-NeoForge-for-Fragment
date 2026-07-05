package com.hm.efn.compat.neoforge.network;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

public final class NetworkHooks {
   private NetworkHooks() {
   }

   public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
      return new ClientboundAddEntityPacket(
         entity.getId(),
         entity.getUUID(),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         entity.getXRot(),
         entity.getYRot(),
         entity.getType(),
         0,
         entity.getDeltaMovement(),
         entity.getYHeadRot()
      );
   }
}
