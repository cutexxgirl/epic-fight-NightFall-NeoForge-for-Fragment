package com.hm.efn.compat.epicfight.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface PacketBufferCodec<T> extends StreamCodec<ByteBuf, T> {
   PacketBufferCodec<Boolean> BOOLEAN = new PacketBufferCodec<Boolean>() {
      public Boolean decode(FriendlyByteBuf buf) {
         return buf.readBoolean();
      }

      public void encode(Boolean value, FriendlyByteBuf buf) {
         buf.writeBoolean(value);
      }
   };
   PacketBufferCodec<Integer> INTEGER = new PacketBufferCodec<Integer>() {
      public Integer decode(FriendlyByteBuf buf) {
         return buf.readInt();
      }

      public void encode(Integer value, FriendlyByteBuf buf) {
         buf.writeInt(value);
      }
   };
   PacketBufferCodec<Float> FLOAT = new PacketBufferCodec<Float>() {
      public Float decode(FriendlyByteBuf buf) {
         return buf.readFloat();
      }

      public void encode(Float value, FriendlyByteBuf buf) {
         buf.writeFloat(value);
      }
   };
   PacketBufferCodec<Double> DOUBLE = new PacketBufferCodec<Double>() {
      public Double decode(FriendlyByteBuf buf) {
         return buf.readDouble();
      }

      public void encode(Double value, FriendlyByteBuf buf) {
         buf.writeDouble(value);
      }
   };

   T decode(FriendlyByteBuf buf);

   void encode(T value, FriendlyByteBuf buf);

   @Override
   default T decode(ByteBuf buf) {
      return this.decode(asFriendlyByteBuf(buf));
   }

   @Override
   default void encode(ByteBuf buf, T value) {
      this.encode(value, asFriendlyByteBuf(buf));
   }

   private static FriendlyByteBuf asFriendlyByteBuf(ByteBuf buf) {
      return buf instanceof FriendlyByteBuf friendlyByteBuf ? friendlyByteBuf : new FriendlyByteBuf(buf);
   }
}
