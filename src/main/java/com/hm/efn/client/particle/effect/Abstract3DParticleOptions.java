package com.hm.efn.client.particle.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class Abstract3DParticleOptions implements ParticleOptions {
   private final ParticleType<Abstract3DParticleOptions> type;
   private final int entityId;

   public static MapCodec<Abstract3DParticleOptions> codec(ParticleType<Abstract3DParticleOptions> particleType) {
      return RecordCodecBuilder.mapCodec(
         instance -> instance.group(Codec.INT.fieldOf("entityId").forGetter(o -> o.entityId))
            .apply(instance, entityId -> new Abstract3DParticleOptions(particleType, entityId))
      );
   }

   public static StreamCodec<RegistryFriendlyByteBuf, Abstract3DParticleOptions> streamCodec(ParticleType<Abstract3DParticleOptions> particleType) {
      return StreamCodec.of(
         (buffer, options) -> buffer.writeVarInt(options.getEntityId()),
         buffer -> new Abstract3DParticleOptions(particleType, buffer.readVarInt())
      );
   }

   public Abstract3DParticleOptions(ParticleType<Abstract3DParticleOptions> type, int entityId) {
      this.type = type;
      this.entityId = entityId;
   }

   public int getEntityId() {
      return this.entityId;
   }

   @NotNull
   public ParticleType<?> getType() {
      return this.type;
   }
}
